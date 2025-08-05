package dev.openfga.sdk.api.client;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;
import static dev.openfga.sdk.util.Validation.assertParamExists;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.*;
import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import dev.openfga.sdk.telemetry.Telemetry;
import dev.openfga.sdk.util.RetryAfterHeaderParser;
import dev.openfga.sdk.util.RetryStrategy;
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
import java.util.concurrent.*;

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

        return attemptHttpRequest(createClient(), 0, null);
    }

    private HttpClient createClient() {
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
                    // No network error, proceed with normal HTTP response handling
                    return processHttpResponse(response, retryNumber, previousError);
                })
                .thenCompose(future -> future);
    }

    private CompletableFuture<ApiResponse<T>> handleNetworkError(Throwable throwable, int retryNumber) {
        if (retryNumber < configuration.getMaxRetries()) {
            // Network errors should be retried with exponential backoff (no Retry-After header available)
            Duration retryDelay = RetryStrategy.calculateRetryDelay(
                    Optional.empty(), retryNumber, configuration.getMinimumRetryDelay());

            // Add telemetry for network error retry
            addTelemetryAttribute(Attributes.HTTP_REQUEST_RESEND_COUNT, String.valueOf(retryNumber + 1));

            // Create delayed client and retry asynchronously without blocking
            HttpClient delayingClient = getDelayedHttpClient(retryDelay);
            return attemptHttpRequest(delayingClient, retryNumber + 1, throwable);
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

        // Create delayed client and retry asynchronously without blocking
        HttpClient delayingClient = getDelayedHttpClient(retryDelay);
        return attemptHttpRequest(delayingClient, retryNumber + 1, error);
    }

    private CompletableFuture<ApiResponse<T>> processHttpResponse(
            HttpResponse<String> response, int retryNumber, Throwable previousError) {
        Optional<FgaError> fgaError = FgaError.getError(name, request, configuration, response, previousError);

        if (fgaError.isPresent()) {
            FgaError error = fgaError.get();
            int statusCode = error.getStatusCode();

            if (retryNumber < configuration.getMaxRetries()) {
                // Parse Retry-After header if present
                Optional<Duration> retryAfterDelay =
                        response.headers().firstValue("retry-after").flatMap(RetryAfterHeaderParser::parseRetryAfter);

                // Check if we should retry based on the new strategy
                if (RetryStrategy.shouldRetry(statusCode)) {
                    return handleHttpErrorRetry(retryAfterDelay, retryNumber, error);
                } else {
                }
            }

            return CompletableFuture.failedFuture(error);
        }

        addTelemetryAttributes(Attributes.fromHttpResponse(response, this.configuration.getCredentials()));

        if (retryNumber > 0) {
            addTelemetryAttribute(Attributes.HTTP_REQUEST_RESEND_COUNT, String.valueOf(retryNumber));
        }

        if (response.headers().firstValue("fga-query-duration-ms").isPresent()) {
            String queryDuration =
                    response.headers().firstValue("fga-query-duration-ms").orElse(null);

            if (!isNullOrWhitespace(queryDuration)) {
                double queryDurationDouble = Double.parseDouble(queryDuration);
                telemetry.metrics().queryDuration(queryDurationDouble, this.getTelemetryAttributes());
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

    private HttpClient getDelayedHttpClient(Duration retryDelay) {
        if (retryDelay == null || retryDelay.isZero() || retryDelay.isNegative()) {
            // Fallback to minimum retry delay if invalid
            retryDelay = configuration.getMinimumRetryDelay();
            if (retryDelay == null) {
                // Default fallback if no minimum retry delay is configured
                retryDelay = Duration.ofMillis(100);
            }
        }

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
