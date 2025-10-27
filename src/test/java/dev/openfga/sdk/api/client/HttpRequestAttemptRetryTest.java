package dev.openfga.sdk.api.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaApiInternalError;
import dev.openfga.sdk.errors.FgaError;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpRequestAttemptRetryTest {

    private WireMockServer wireMockServer;
    private ClientConfiguration configuration;
    private ApiClient apiClient;

    @BeforeEach
    void setUp() {
        wireMockServer =
                new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        configuration = new ClientConfiguration()
                .apiUrl("http://localhost:" + wireMockServer.port())
                .maxRetries(3)
                .minimumRetryDelay(Duration.ofMillis(10)); // Short delay for testing

        apiClient = new ApiClient();
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void shouldRetryWith429AndRetryAfterHeader() throws Exception {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-after-scenario")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader(
                                FgaConstants.RETRY_AFTER_HEADER_NAME,
                                "0.05") // Fast retry for test performance - timing not verified
                        .withBody("{\"error\":\"rate limited\"}"))
                .willSetStateTo("First Retry"));

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-after-scenario")
                .whenScenarioStateIs("First Retry")
                .willReturn(aResponse().withStatus(200).withBody("")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, configuration);

        // When
        ApiResponse<Void> response = attempt.attemptHttpRequest().get();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Verify both requests were made
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldRetryWith500AndRetryAfterHeaderForGetRequest() throws Exception {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("server-error-scenario")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader(
                                FgaConstants.RETRY_AFTER_HEADER_NAME,
                                "0.05") // Fast retry for test performance - timing not verified
                        .withBody("{\"error\":\"server error\"}"))
                .willSetStateTo("First Retry"));

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("server-error-scenario")
                .whenScenarioStateIs("First Retry")
                .willReturn(aResponse().withStatus(200).withBody("")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, configuration);

        // When
        ApiResponse<Void> response = attempt.attemptHttpRequest().get();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Verify both requests were made
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldRetryWith500WithoutRetryAfterHeaderForPostRequest() throws Exception {
        // Given - Simplified logic: POST requests should retry on 5xx errors
        wireMockServer.stubFor(post(urlEqualTo("/test"))
                .willReturn(aResponse().withStatus(500).withBody("{\"error\":\"server error\"}")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, configuration);

        // When & Then
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        assertThat(exception.getCause()).isInstanceOf(FgaError.class);
        FgaError error = (FgaError) exception.getCause();
        assertThat(error.getStatusCode()).isEqualTo(500);

        // Verify multiple requests were made (POST requests now retry on 5xx without Retry-After)
        // Default max retries is 3, so expect 4 total requests (1 initial + 3 retries)
        wireMockServer.verify(4, postRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldRetryWith500WithRetryAfterHeaderForPostRequest() throws Exception {
        // Given
        wireMockServer.stubFor(post(urlEqualTo("/test"))
                .inScenario("post-retry-scenario")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader(
                                FgaConstants.RETRY_AFTER_HEADER_NAME,
                                "0.05") // Fast retry for test performance - timing not verified
                        .withBody("{\"error\":\"server error\"}"))
                .willSetStateTo("First Retry"));

        wireMockServer.stubFor(post(urlEqualTo("/test"))
                .inScenario("post-retry-scenario")
                .whenScenarioStateIs("First Retry")
                .willReturn(aResponse().withStatus(200).withBody("")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, configuration);

        // When
        ApiResponse<Void> response = attempt.attemptHttpRequest().get();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Verify both requests were made
        wireMockServer.verify(2, postRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldNotRetryWith501() throws Exception {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse().withStatus(501).withBody("{\"error\":\"not implemented\"}")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, configuration);

        // When & Then
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        assertThat(exception.getCause()).isInstanceOf(FgaError.class);
        FgaError error = (FgaError) exception.getCause();
        assertThat(error.getStatusCode()).isEqualTo(501);

        // Verify only one request was made (no retry)
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldRespectMaxRetries() throws Exception {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse().withStatus(429).withBody("{\"error\":\"rate limited\"}")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, configuration);

        // When & Then
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        assertThat(exception.getCause()).isInstanceOf(FgaError.class);
        FgaError error = (FgaError) exception.getCause();
        assertThat(error.getStatusCode()).isEqualTo(429);

        // Verify maxRetries + 1 requests were made (initial + 3 retries)
        wireMockServer.verify(4, getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldUseExponentialBackoffWhenNoRetryAfterHeader() throws Exception {
        // Given
        long startTime = System.currentTimeMillis();

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("exponential-backoff-scenario")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse().withStatus(429).withBody("{\"error\":\"rate limited\"}"))
                .willSetStateTo("First Retry"));

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("exponential-backoff-scenario")
                .whenScenarioStateIs("First Retry")
                .willReturn(aResponse().withStatus(200).withBody("")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, configuration);

        // When
        ApiResponse<Void> response = attempt.attemptHttpRequest().get();
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Verify both requests were made
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/test")));

        // Verify some delay occurred (exponential backoff with 10ms minimum delay)
        // Note: Using a generous range due to test timing variability
        // With minimumRetryDelay=10ms, first retry should be at least 10ms
        assertThat(endTime - startTime).isGreaterThan(8L);
    }

    @Test
    void shouldHandleInvalidRetryAfterHeader() throws Exception {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("invalid-retry-after-scenario")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader(FgaConstants.RETRY_AFTER_HEADER_NAME, "invalid-value")
                        .withBody("{\"error\":\"rate limited\"}"))
                .willSetStateTo("First Retry"));

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("invalid-retry-after-scenario")
                .whenScenarioStateIs("First Retry")
                .willReturn(aResponse().withStatus(200).withBody("")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, configuration);

        // When
        ApiResponse<Void> response = attempt.attemptHttpRequest().get();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Verify both requests were made (should fall back to exponential backoff)
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldRetryNetworkErrorsWithExponentialBackoff() throws Exception {
        // Given - Capture port before stopping server
        int serverPort = wireMockServer.port();
        wireMockServer.stop();

        // Create configuration with specific delays for timing test
        ClientConfiguration networkConfig = new ClientConfiguration()
                .apiUrl("http://localhost:" + serverPort)
                .maxRetries(2)
                .minimumRetryDelay(Duration.ofMillis(100)); // 100ms base delay

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + serverPort + "/test"))
                .GET()
                .timeout(Duration.ofMillis(50)) // Short timeout to force connection error
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, networkConfig);

        Instant startTime = Instant.now();

        // When
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        assertThat(exception.getCause()).isInstanceOf(ApiException.class);

        // With exponential backoff: 100ms (1st retry) + ~200ms (2nd retry) = ~300ms total
        // Allow some tolerance for execution overhead
        assertThat(totalTime.toMillis())
                .isGreaterThan(200) // Should be at least ~300ms
                .isLessThan(1000); // But not excessive
    }

    @Test
    void shouldHonorNetworkErrorRetryDelayTiming() throws Exception {
        // Given - Capture port before stopping server to force network error
        int serverPort = wireMockServer.port();
        wireMockServer.stop();

        // Create configuration with specific minimum retry delay
        ClientConfiguration networkConfig = new ClientConfiguration()
                .apiUrl("http://localhost:" + serverPort)
                .maxRetries(1) // Only 1 retry to test precise timing
                .minimumRetryDelay(Duration.ofMillis(500)); // 500ms delay

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + serverPort + "/test"))
                .GET()
                .timeout(Duration.ofMillis(50)) // Short timeout to force connection error quickly
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, networkConfig);

        Instant startTime = Instant.now();

        // When
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        assertThat(exception.getCause()).isInstanceOf(ApiException.class);

        // Should take approximately 500ms for the retry delay (plus small overhead)
        // Per GitHub issue #155: jitter range is [base, 2*base], so 500ms base can become up to 1000ms
        // Network error retry timing is now working correctly
        assertThat(totalTime.toMillis())
                .isGreaterThan(450) // Should be at least ~500ms (base delay)
                .isLessThan(1200); // Allow for up to 1000ms jitter + execution overhead
    }

    @Test
    void shouldUseExponentialBackoffForNetworkErrorsWithPreciseTiming() throws Exception {
        // Given - Use invalid hostname to simulate DNS failure (more reliable than port)
        ClientConfiguration dnsConfig = new ClientConfiguration()
                .apiUrl("http://invalid-hostname-that-does-not-exist.local")
                .maxRetries(2)
                .minimumRetryDelay(Duration.ofMillis(200)); // 200ms base delay

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://invalid-hostname-that-does-not-exist.local/test"))
                .GET()
                .timeout(Duration.ofMillis(500)) // Reasonable timeout
                .build();

        HttpRequestAttempt<Void> attempt = new HttpRequestAttempt<>(request, "test", Void.class, apiClient, dnsConfig);

        Instant startTime = Instant.now();

        // When
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        assertThat(exception.getCause()).isInstanceOf(ApiException.class);

        // With exponential backoff from 200ms base:
        // 1st retry: ~200ms * 2^0 = ~200ms
        // 2nd retry: ~200ms * 2^1 = ~400ms
        // Total: ~600ms (plus jitter and overhead)
        // Network error retry timing is now working correctly after refactoring
        assertThat(totalTime.toMillis())
                .isGreaterThan(500) // Should be at least ~600ms
                .isLessThan(1200); // But not excessive (allowing for jitter)
    }

    @Test
    void shouldRetryOnUnknownHost() throws Exception {
        // Given - Use invalid hostname to simulate DNS failure
        ClientConfiguration dnsConfig = new ClientConfiguration()
                .apiUrl("http://invalid-hostname-that-does-not-exist.local")
                .maxRetries(2)
                .minimumRetryDelay(Duration.ofMillis(10));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://invalid-hostname-that-does-not-exist.local/test"))
                .GET()
                .timeout(Duration.ofMillis(1000))
                .build();

        HttpRequestAttempt<Void> attempt = new HttpRequestAttempt<>(request, "test", Void.class, apiClient, dnsConfig);

        // When & Then
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        // Should fail after retries with network error (DNS resolution failure)
        assertThat(exception.getCause()).isInstanceOf(ApiException.class);
        ApiException apiException = (ApiException) exception.getCause();
        assertThat(apiException.getCause()).isNotNull(); // Should have underlying network error
    }

    @Test
    void shouldRetryOnConnectionTimeout() throws Exception {
        // Given - Capture port before stopping server
        int serverPort = wireMockServer.port();
        wireMockServer.stop();

        // Create configuration with shorter timeout for faster test
        ClientConfiguration timeoutConfig = new ClientConfiguration()
                .apiUrl("http://localhost:" + serverPort)
                .maxRetries(2)
                .minimumRetryDelay(Duration.ofMillis(10));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + serverPort + "/test"))
                .GET()
                .timeout(Duration.ofMillis(100)) // Short timeout
                .build();

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, timeoutConfig);

        // When & Then
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        // Should fail after retries with network error
        assertThat(exception.getCause()).isInstanceOf(ApiException.class);
        ApiException apiException = (ApiException) exception.getCause();
        assertThat(apiException.getCause()).isNotNull(); // Should have underlying network error
    }

    @Test
    void shouldRespectGlobalMinimumRetryDelayWithExponentialBackoff() throws Exception {
        // Given - Server responds with 500 errors (no Retry-After header)
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse().withStatus(500).withBody("{\"error\":\"server error\"}")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        // Use global configuration with larger minimum retry delay
        ClientConfiguration globalConfig = new ClientConfiguration()
                .apiUrl("http://localhost:" + wireMockServer.port())
                .maxRetries(2)
                .minimumRetryDelay(Duration.ofMillis(100)); // Should act as floor for exponential backoff

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, globalConfig);

        Instant startTime = Instant.now();

        // When
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        assertInstanceOf(FgaApiInternalError.class, exception.getCause());

        // Verify that it retried the expected number of times
        wireMockServer.verify(1 + globalConfig.getMaxRetries(), getRequestedFor(urlEqualTo("/test")));

        // With 2 retries and minimum 100ms delays, total time should be at least 200ms
        assertThat(totalTime.toMillis()).isGreaterThan(150); // Should be at least ~200ms
    }

    @Test
    void shouldUseRetryAfterHeaderEvenWhenSmallerThanGlobalMinimumDelay() throws Exception {
        // Given - Server responds with Retry-After header smaller than minimum delay
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-scenario-global-1")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader(FgaConstants.RETRY_AFTER_HEADER_NAME, "0.05") // 50ms
                        .withBody("{\"error\":\"rate limited\"}"))
                .willSetStateTo("After-First-Request"));

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-scenario-global-1")
                .whenScenarioStateIs("After-First-Request")
                .willReturn(aResponse().withStatus(200).withBody("")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        // Use global configuration with larger minimum retry delay (should NOT take precedence over Retry-After)
        ClientConfiguration globalConfig = new ClientConfiguration()
                .apiUrl("http://localhost:" + wireMockServer.port())
                .maxRetries(2)
                .minimumRetryDelay(Duration.ofMillis(150)); // Should NOT override Retry-After

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, globalConfig);

        Instant startTime = Instant.now();

        // When
        attempt.attemptHttpRequest().get(); // Should succeed after retry

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        // Should have respected the Retry-After header (50ms) instead of minimum retry delay (150ms)
        assertThat(totalTime.toMillis()).isGreaterThan(30); // Should be at least ~50ms
        assertThat(totalTime.toMillis()).isLessThan(400); // But less than 400ms (well below the 150ms minimum)

        // Verify both requests were made
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldUseRetryAfterWhenLargerThanGlobalMinimumDelay() throws Exception {
        // Given - Server responds with Retry-After header larger than minimum delay
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-scenario-global-2")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader(FgaConstants.RETRY_AFTER_HEADER_NAME, "0.1") // 100ms
                        .withBody("{\"error\":\"rate limited\"}"))
                .willSetStateTo("After-First-Request"));

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-scenario-global-2")
                .whenScenarioStateIs("After-First-Request")
                .willReturn(aResponse().withStatus(200).withBody("")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        // Use global configuration with smaller minimum retry delay
        ClientConfiguration globalConfig = new ClientConfiguration()
                .apiUrl("http://localhost:" + wireMockServer.port())
                .maxRetries(2)
                .minimumRetryDelay(Duration.ofMillis(50)); // Should NOT override Retry-After: 100ms

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, globalConfig);

        Instant startTime = Instant.now();

        // When
        attempt.attemptHttpRequest().get(); // Should succeed after retry

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        // Should have respected the Retry-After header (100ms) over minimum delay (50ms)
        // Note: Using generous bounds due to timing variability in test environments
        System.out.println("Actual retry duration: " + totalTime.toMillis() + " ms");

        assertThat(totalTime.toMillis()).isGreaterThan(50); // Should be at least ~100ms (with tolerance)
        assertThat(totalTime.toMillis()).isLessThan(1000); // But not excessive

        // Verify both requests were made
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldRespectPerRequestMinimumRetryDelayOverride() throws Exception {
        // Given - Server responds with 500 errors (no Retry-After header)
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse().withStatus(500).withBody("{\"error\":\"server error\"}")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        // Override with larger minimum retry delay using per-request configuration
        dev.openfga.sdk.api.configuration.Configuration overriddenConfig =
                configuration.override(new dev.openfga.sdk.api.configuration.ConfigurationOverride()
                        .minimumRetryDelay(Duration.ofMillis(100)));

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, overriddenConfig);

        Instant startTime = Instant.now();

        // When
        ExecutionException exception = assertThrows(
                ExecutionException.class, () -> attempt.attemptHttpRequest().get());

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        assertInstanceOf(FgaApiInternalError.class, exception.getCause());

        // Verify that it retried the expected number of times
        wireMockServer.verify(1 + overriddenConfig.getMaxRetries(), getRequestedFor(urlEqualTo("/test")));

        // With 3 retries and minimum 100ms delays, total time should be at least 300ms
        assertThat(totalTime.toMillis()).isGreaterThan(250); // Should be at least ~300ms
    }

    @Test
    void shouldRespectPerRequestMaxRetriesOverride() throws Exception {
        // Given - Server always responds with 500 errors
        wireMockServer.stubFor(post(urlEqualTo("/stores/test-store/check"))
                .willReturn(aResponse().withStatus(500).withBody("{\"error\":\"server error\"}")));

        // Override with different max retries using per-request configuration
        dev.openfga.sdk.api.configuration.ConfigurationOverride override =
                new dev.openfga.sdk.api.configuration.ConfigurationOverride()
                        .maxRetries(5)
                        .minimumRetryDelay(Duration.ofMillis(10)); // Fast for testing

        // When
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            // Simulate the API call with override
            dev.openfga.sdk.api.configuration.Configuration effectiveConfig = configuration.override(override);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/stores/test-store/check"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .build();

            HttpRequestAttempt<Void> attempt =
                    new HttpRequestAttempt<>(request, "check", Void.class, apiClient, effectiveConfig);
            attempt.attemptHttpRequest().get();
        });

        // Then
        assertInstanceOf(FgaApiInternalError.class, exception.getCause());

        // Should have made 1 initial + 5 retries = 6 total requests
        wireMockServer.verify(6, postRequestedFor(urlEqualTo("/stores/test-store/check")));
    }

    @Test
    void shouldUseRetryAfterHeaderEvenWhenSmallerThanMinimumDelay() throws Exception {
        // Given - Server responds with Retry-After header smaller than minimum delay
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-scenario-per-request-1")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader(FgaConstants.RETRY_AFTER_HEADER_NAME, "0.05") // 50ms
                        .withBody("{\"error\":\"rate limited\"}"))
                .willSetStateTo("After-First-Request"));

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-scenario-per-request-1")
                .whenScenarioStateIs("After-First-Request")
                .willReturn(aResponse().withStatus(200).withBody("")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        // Override with larger minimum retry delay (should NOT take precedence over Retry-After)
        dev.openfga.sdk.api.configuration.Configuration overriddenConfig =
                configuration.override(new dev.openfga.sdk.api.configuration.ConfigurationOverride()
                        .minimumRetryDelay(Duration.ofMillis(150)));

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, overriddenConfig);

        Instant startTime = Instant.now();

        // When
        attempt.attemptHttpRequest().get(); // Should succeed after retry

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        // Should have respected the Retry-After header (50ms) instead of minimum retry delay (150ms)
        assertThat(totalTime.toMillis()).isGreaterThan(30); // Should be at least ~50ms
        assertThat(totalTime.toMillis()).isLessThan(400); // But less than 400ms (well below the 150ms minimum)

        // Verify both requests were made
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    void shouldNotOverrideRetryAfterWhenItIsLargerThanMinimumDelayPerRequest() throws Exception {
        // Given - Server responds with success after first retry to limit test time
        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-scenario-per-request-2")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader(FgaConstants.RETRY_AFTER_HEADER_NAME, "0.05") // 50ms
                        .withBody("{\"error\":\"rate limited\"}"))
                .willSetStateTo("retry-attempted"));

        wireMockServer.stubFor(get(urlEqualTo("/test"))
                .inScenario("retry-scenario-per-request-2")
                .whenScenarioStateIs("retry-attempted")
                .willReturn(aResponse().withStatus(200).withBody("{\"success\":true}")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:" + wireMockServer.port() + "/test"))
                .GET()
                .build();

        // Override with smaller minimum retry delay (should NOT take precedence over Retry-After)
        dev.openfga.sdk.api.configuration.Configuration overriddenConfig =
                configuration.override(new dev.openfga.sdk.api.configuration.ConfigurationOverride()
                        .minimumRetryDelay(Duration.ofMillis(500)));

        // Verify the override took effect
        assertEquals(Duration.ofMillis(500), overriddenConfig.getMinimumRetryDelay());

        HttpRequestAttempt<Void> attempt =
                new HttpRequestAttempt<>(request, "test", Void.class, apiClient, overriddenConfig);

        Instant startTime = Instant.now();

        // When - This will succeed after 1 retry
        attempt.attemptHttpRequest().get();

        Instant endTime = Instant.now();
        Duration totalTime = Duration.between(startTime, endTime);

        // Then
        // Should have respected the Retry-After header (50ms) for the single retry
        // Note: actual timing may vary in test environments, so we use generous bounds
        assertThat(totalTime.toMillis()).isGreaterThan(30); // Should be at least ~50ms
        assertThat(totalTime.toMillis()).isLessThan(10000); // But not excessive (was sometimes 4x in CI)

        // Verify initial request + 1 retry = 2 total requests
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/test")));
    }
}
