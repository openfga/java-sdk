package dev.openfga.sdk.api.client;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.*;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;

public class HttpRequestAttempt<T> {
    private final ApiClient apiClient;
    private final Configuration configuration;
    private final Class<T> clazz;
    private final String name;
    private final HttpRequest request;

    public HttpRequestAttempt(
            HttpRequest request, String name, Class<T> clazz, ApiClient apiClient, Configuration configuration)
            throws FgaInvalidParameterException {
        if (configuration.getMaxRetries() == null) {
            throw new FgaInvalidParameterException("maxRetries", "Configuration");
        }
        this.apiClient = apiClient;
        this.configuration = configuration;
        this.name = name;
        this.request = request;
        this.clazz = clazz;
    }

    public CompletableFuture<ApiResponse<T>> attemptHttpRequest() throws ApiException {
        int retryNumber = 0;
        return attemptHttpRequest(apiClient.getHttpClient(), retryNumber, null);
    }

    private CompletableFuture<ApiResponse<T>> attemptHttpRequest(
            HttpClient httpClient, int retryNumber, Throwable previousError) {
        return httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    Optional<FgaError> fgaError =
                            FgaError.getError(name, request, configuration, response, previousError);

                    if (fgaError.isPresent()) {
                        FgaError error = fgaError.get();
                        if (HttpStatusCode.isRetryable(error.getStatusCode())
                                && retryNumber < configuration.getMaxRetries()) {

                            HttpClient delayingClient = getDelayedHttpClient();
                            return attemptHttpRequest(delayingClient, retryNumber + 1, error);
                        }
                        return CompletableFuture.failedFuture(error);
                    }

                    return deserializeResponse(response)
                            .thenApply(modeledResponse -> new ApiResponse<>(
                                    response.statusCode(), response.headers().map(), response.body(), modeledResponse));
                });
    }

    private CompletableFuture<T> deserializeResponse(HttpResponse<String> response) {
        if (clazz == Void.class && isNullOrWhitespace(response.body())) {
            return CompletableFuture.completedFuture(null);
        }

        try {
            T deserialized = apiClient.getObjectMapper().readValue(response.body(), clazz);
            return CompletableFuture.completedFuture(deserialized);
        } catch (IOException e) {
            // Malformed response.
            return CompletableFuture.failedFuture(new ApiException(e));
        }
    }

    private HttpClient getDelayedHttpClient() {
        Duration retryDelay = configuration.getMinimumRetryDelay();
        return apiClient
                .getHttpClientBuilder()
                .executor(CompletableFuture.delayedExecutor(retryDelay.toNanos(), TimeUnit.NANOSECONDS))
                .build();
    }
}
