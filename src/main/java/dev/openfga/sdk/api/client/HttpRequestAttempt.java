package dev.openfga.sdk.api.client;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;
import static dev.openfga.sdk.util.Validation.assertParamExists;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.*;
import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import dev.openfga.sdk.telemetry.Telemetry;
import dev.openfga.sdk.util.RetryAfterHeaderParser;
import dev.openfga.sdk.util.RetryStrategy;
import java.io.IOException;
import java.io.PrintStream;
import java.net.http.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

public class HttpRequestAttempt<T> {
    private final ApiClient apiClient;
    private final Configuration configuration;
    private final Class<T> clazz;
    private final String name;
    private final HttpRequest request;
    private final Telemetry telemetry;
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
        this.telemetry = new Telemetry(configuration);
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

        addTelemetryAttribute(Attributes.HTTP_HOST, request.uri().getHost());
        addTelemetryAttribute(Attributes.URL_SCHEME, request.uri().getScheme());
        addTelemetryAttribute(Attributes.URL_FULL, request.uri().toString());
        addTelemetryAttribute(Attributes.HTTP_REQUEST_METHOD, request.method());
        addTelemetryAttribute(Attributes.USER_AGENT, configuration.getUserAgent());

        return attemptHttpRequest(getHttpClient(), 0, null);
    }

    private HttpClient getHttpClient() {
        return apiClient.getHttpClient();
    }

    private CompletableFuture<ApiResponse<T>> attemptHttpRequest(
            HttpClient httpClient, int retryNumber, Throwable previousError) {
        return httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        // Handle network errors (no HTTP response received)
                        return handleNetworkError(throwable, retryNumber);
                    }

                    // Handle HTTP response (including error status codes)
                    return processHttpResponse(response, retryNumber, previousError);
                })
                .thenCompose(Function.identity());
    }

    private CompletableFuture<ApiResponse<T>> handleNetworkError(Throwable throwable, int retryNumber) {
        if (retryNumber < configuration.getMaxRetries()) {
            // Network errors should be retried with exponential backoff (no Retry-After header available)
            Duration retryDelay = RetryStrategy.calculateRetryDelay(
                    Optional.empty(), retryNumber, configuration.getMinimumRetryDelay());

            // Add telemetry for network error retry
            addTelemetryAttribute(Attributes.HTTP_REQUEST_RESEND_COUNT, String.valueOf(retryNumber + 1));

            return delayedRetry(retryDelay, retryNumber + 1, throwable);
        } else {
            // Max retries exceeded, fail with the network error
            return CompletableFuture.failedFuture(new ApiException(throwable));
        }
    }

    private CompletableFuture<ApiResponse<T>> handleHttpErrorRetry(
            Optional<Duration> retryAfterDelay, int retryNumber, FgaError error) {
        // Calculate appropriate delay
        Duration retryDelay =
                RetryStrategy.calculateRetryDelay(retryAfterDelay, retryNumber, configuration.getMinimumRetryDelay());

        // Add telemetry for HTTP error retry
        addTelemetryAttribute(Attributes.HTTP_REQUEST_RESEND_COUNT, String.valueOf(retryNumber + 1));

        return delayedRetry(retryDelay, retryNumber + 1, error);
    }

    /**
     * Performs a delayed retry using CompletableFuture.delayedExecutor().
     * This method centralizes the common delay logic used by both network error and HTTP error retries.
     *
     * @param retryDelay The duration to wait before retrying
     * @param nextRetryNumber The next retry attempt number (1-based)
     * @param previousError The previous error that caused the retry
     * @return CompletableFuture that completes after the delay with the retry attempt
     */
    private CompletableFuture<ApiResponse<T>> delayedRetry(
            Duration retryDelay, int nextRetryNumber, Throwable previousError) {
        // Use CompletableFuture.delayedExecutor() to delay the retry attempt itself
        return CompletableFuture.runAsync(
                        () -> {
                            // No-op task, we only care about the delay timing
                        },
                        CompletableFuture.delayedExecutor(retryDelay.toNanos(), TimeUnit.NANOSECONDS))
                .thenCompose(ignored -> {
                    // Get HttpClient when needed (just returns cached instance)
                    return attemptHttpRequest(getHttpClient(), nextRetryNumber, previousError);
                });
    }

    private CompletableFuture<ApiResponse<T>> processHttpResponse(
            HttpResponse<String> response, int retryNumber, Throwable previousError) {
        Optional<FgaError> fgaError = FgaError.getError(name, request, configuration, response, previousError);

        if (fgaError.isPresent()) {
            FgaError error = fgaError.get();
            int statusCode = error.getStatusCode();

            if (retryNumber < configuration.getMaxRetries()) {
                // Parse Retry-After header if present
                Optional<Duration> retryAfterDelay = response.headers()
                        .firstValue(FgaConstants.RETRY_AFTER_HEADER_NAME)
                        .flatMap(RetryAfterHeaderParser::parseRetryAfter);

                // Check if we should retry based on the new strategy
                if (RetryStrategy.shouldRetry(statusCode)) {
                    return handleHttpErrorRetry(retryAfterDelay, retryNumber, error);
                }
            }

            return CompletableFuture.failedFuture(error);
        }

        addTelemetryAttributes(Attributes.fromHttpResponse(response, this.configuration.getCredentials()));

        if (retryNumber > 0) {
            addTelemetryAttribute(Attributes.HTTP_REQUEST_RESEND_COUNT, String.valueOf(retryNumber));
        }

        if (response.headers()
                .firstValue(FgaConstants.QUERY_DURATION_HEADER_NAME)
                .isPresent()) {
            String queryDuration = response.headers()
                    .firstValue(FgaConstants.QUERY_DURATION_HEADER_NAME)
                    .orElse(null);

            if (!isNullOrWhitespace(queryDuration)) {
                try {
                    double queryDurationDouble = Double.parseDouble(queryDuration);
                    telemetry.metrics().queryDuration(queryDurationDouble, this.getTelemetryAttributes());
                } catch (NumberFormatException e) {
                    // Ignore malformed fga-query-duration-ms header values to prevent exceptions
                    // on otherwise valid responses. The telemetry metric will simply not be recorded.
                }
            }
        }

        Double requestDuration = (double) (System.currentTimeMillis() - requestStarted);

        telemetry.metrics().requestDuration(requestDuration, this.getTelemetryAttributes());

        return deserializeResponse(response)
                .thenApply(modeledResponse -> new ApiResponse<>(
                        response.statusCode(), response.headers().map(), response.body(), modeledResponse));
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
