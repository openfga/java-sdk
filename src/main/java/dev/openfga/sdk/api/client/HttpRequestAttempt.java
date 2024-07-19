package dev.openfga.sdk.api.client;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;
import static dev.openfga.sdk.util.Validation.assertParamExists;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaError;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.errors.HttpStatusCode;
import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import dev.openfga.sdk.telemetry.Telemetry;
import java.io.IOException;
import java.io.PrintStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

public class HttpRequestAttempt<T> {
    private final ApiClient apiClient;
    private final Configuration configuration;
    private final Class<T> clazz;
    private final String name;
    private final HttpRequest request;
    private final Telemetry telemetry = new Telemetry();
    private Long requestStarted;
    private Map<Attribute, String> telemetryAttributes;

    // Intended for only testing the OpenFGA SDK itself.
    private final boolean enableDebugLogging = "enable".equals(System.getProperty("HttpRequestAttempt.debug-logging"));

    public HttpRequestAttempt(
            HttpRequest request, String name, Class<T> clazz, ApiClient apiClient, Configuration configuration)
            throws FgaInvalidParameterException {
        assertParamExists(configuration.getMaxRetries(), "maxRetries", "Configuration");
        this.apiClient = apiClient;
        this.configuration = configuration;
        this.name = name;
        this.request = request;
        this.clazz = clazz;
        this.telemetryAttributes = new HashMap<>();
    }

    public Map<Attribute, String> getTelemetryAttributes() {
        return telemetryAttributes;
    }

    public HttpRequestAttempt<T> setTelemetryAttributes(Map<Attribute, String> attributes) {
        this.telemetryAttributes = attributes;
        return this;
    }

    public HttpRequestAttempt<T> addTelemetryAttribute(Attribute attribute, String value) {
        this.telemetryAttributes.put(attribute, value);
        return this;
    }

    public HttpRequestAttempt<T> addTelemetryAttributes(Map<Attribute, String> attributes) {
        this.telemetryAttributes.putAll(attributes);
        return this;
    }

    public CompletableFuture<ApiResponse<T>> attemptHttpRequest() throws ApiException {
        this.requestStarted = System.currentTimeMillis();

        if (enableDebugLogging) {
            request.bodyPublisher()
                    .ifPresent(requestBodyPublisher ->
                            requestBodyPublisher.subscribe(new BodyLogger(System.err, "request")));
        }

        addTelemetryAttribute(Attributes.HTTP_HOST, configuration.getApiUrl());
        addTelemetryAttribute(Attributes.HTTP_METHOD, request.method());

        try {
            addTelemetryAttribute(
                    Attributes.REQUEST_CLIENT_ID,
                    configuration.getCredentials().getClientCredentials().getClientId());
        } catch (Exception e) {
        }

        return attemptHttpRequest(createClient(), 0, null);
    }

    private HttpClient createClient() {
        return apiClient.getHttpClient();
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

                    addTelemetryAttributes(Attributes.fromHttpResponse(response, this.configuration.getCredentials()));
                    addTelemetryAttribute(Attributes.REQUEST_RETRIES, String.valueOf(retryNumber));

                    if (response.headers().firstValue("fga-query-duration-ms").isPresent()) {
                        double queryDuration = Double.parseDouble(response.headers()
                                .firstValue("fga-query-duration-ms")
                                .get());
                        telemetry.metrics().queryDuration(queryDuration, this.getTelemetryAttributes());
                    }

                    telemetry
                            .metrics()
                            .requestDuration(
                                    (double) (System.currentTimeMillis() - this.requestStarted),
                                    this.getTelemetryAttributes());

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

    private static class BodyLogger implements Flow.Subscriber<ByteBuffer> {
        private final PrintStream out;
        private final String target;

        BodyLogger(PrintStream out, String target) {
            this.out = out;
            this.target = target;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            out.printf("[%s] subscribed: %s\n", this.getClass().getName(), subscription);
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer item) {
            out.printf(
                    "[%s] %s: %s\n",
                    this.getClass().getName(), target, new String(item.array(), StandardCharsets.UTF_8));
        }

        @Override
        public void onError(Throwable throwable) {
            out.printf("[%s] error: %s\n", this.getClass().getName(), throwable);
        }

        @Override
        public void onComplete() {
            out.flush();
        }
    }
}
