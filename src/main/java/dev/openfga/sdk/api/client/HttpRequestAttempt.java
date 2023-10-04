package dev.openfga.sdk.api.client;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.api.configuration.BaseConfiguration;
import dev.openfga.sdk.errors.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;

public class HttpRequestAttempt<T> {
    private final ApiClient apiClient;
    private final BaseConfiguration configuration;
    private final Class<T> clazz;
    private final String name;
    private final HttpRequest request;

    public HttpRequestAttempt(
            HttpRequest request, String name, Class<T> clazz, ApiClient apiClient, BaseConfiguration configuration)
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

    public CompletableFuture<T> attemptHttpRequest() throws ApiException {
        int retryNumber = 0;
        return attemptHttpRequest(apiClient.getHttpClient(), retryNumber);
    }

    private CompletableFuture<T> attemptHttpRequest(HttpClient httpClient, int retryNumber) {
        return httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    int status = response.statusCode();
                    String responseBody = response.body();

                    try {
                        checkStatus(name, response);
                    } catch (FgaApiRateLimitExceededError | FgaApiInternalError e) {
                        if (retryNumber < configuration.getMaxRetries()) {
                            HttpClient delayingClient = getDelayedHttpClient();
                            return attemptHttpRequest(delayingClient, retryNumber + 1);
                        }
                        return CompletableFuture.failedFuture(e);
                    } catch (ApiException e) {
                        return CompletableFuture.failedFuture(e);
                    }

                    if (status != HttpURLConnection.HTTP_OK
                            && status != HttpURLConnection.HTTP_CREATED
                            && status != HttpURLConnection.HTTP_NO_CONTENT) {
                        // An HTTP failure that is not modeled in checkStatus(..., ...) below.
                        return CompletableFuture.failedFuture(new ApiException(name, response));
                    }

                    if (clazz == Void.class && isNullOrWhitespace(responseBody)) {
                        return CompletableFuture.completedFuture(null);
                    }

                    try {
                        T body = apiClient.getObjectMapper().readValue(responseBody, clazz);
                        return CompletableFuture.completedFuture(body);
                    } catch (IOException e) {
                        // Malformed response.
                        return CompletableFuture.failedFuture(new ApiException(e));
                    }
                });
    }

    private HttpClient getDelayedHttpClient() {
        Duration retryDelay = configuration.getMinimumRetryDelay();
        return apiClient
                .getHttpClientBuilder()
                .executor(CompletableFuture.delayedExecutor(retryDelay.toNanos(), TimeUnit.NANOSECONDS))
                .build();
    }

    private static void checkStatus(String name, HttpResponse<String> response)
            throws FgaApiValidationError, FgaApiAuthenticationError, FgaApiNotFoundError, FgaApiRateLimitExceededError,
                    FgaApiInternalError {

        int status = response.statusCode();
        String body = response.body();

        switch (status) {
            case HttpURLConnection.HTTP_BAD_REQUEST:
            case 422: // HTTP 422 Unprocessable Entity
                throw new FgaApiValidationError(name, status, response.headers(), body);

            case HttpURLConnection.HTTP_UNAUTHORIZED:
            case HttpURLConnection.HTTP_FORBIDDEN:
                throw new FgaApiAuthenticationError(name, status, response.headers(), body);

            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new FgaApiNotFoundError(name, status, response.headers(), body);

            case 429: // HTTP 429 Too Many Requests
                throw new FgaApiRateLimitExceededError(name, status, response.headers(), body);

            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new FgaApiInternalError(name, status, response.headers(), body);
        }
    }
}
