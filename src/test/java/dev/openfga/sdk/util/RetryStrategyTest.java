package dev.openfga.sdk.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RetryStrategyTest {

    @Test
    void calculateRetryDelay_withRetryAfterHeader_shouldUseRetryAfterValue() {
        // Given
        Optional<Duration> retryAfterDelay = Optional.of(Duration.ofMillis(5000));
        int retryCount = 1;
        Duration minimumRetryDelay = Duration.ofMillis(100);

        // When
        Duration result = RetryStrategy.calculateRetryDelay(retryAfterDelay, retryCount, minimumRetryDelay);

        // Then
        assertThat(result).isEqualTo(Duration.ofMillis(5000));
    }

    @Test
    void calculateRetryDelay_withRetryAfterSmallerThanMinimum_shouldUseRetryAfter() {
        // Given
        Optional<Duration> retryAfterDelay = Optional.of(Duration.ofMillis(50));
        int retryCount = 1;
        Duration minimumRetryDelay = Duration.ofMillis(200);

        // When
        Duration result = RetryStrategy.calculateRetryDelay(retryAfterDelay, retryCount, minimumRetryDelay);

        // Then
        assertThat(result).isEqualTo(Duration.ofMillis(50));
    }

    @Test
    void calculateRetryDelay_withoutRetryAfter_shouldUseMinimumAsBaseDelay() {
        // Given
        Optional<Duration> retryAfterDelay = Optional.empty();
        int retryCount = 1;
        Duration minimumRetryDelay = Duration.ofMillis(500);

        // When
        Duration result = RetryStrategy.calculateRetryDelay(retryAfterDelay, retryCount, minimumRetryDelay);

        // Then
        // For retry count 1 with 500ms base: 2^1 * 500ms = 1000ms base
        // With jitter: between 1000ms and 2000ms
        assertThat(result.toMillis()).isBetween(1000L, 2000L);
    }

    @Test
    void calculateRetryDelay_withNullMinimum_shouldUseDefaultBase() {
        // Given
        Optional<Duration> retryAfterDelay = Optional.empty();
        int retryCount = 1;
        Duration minimumRetryDelay = null;

        // When
        Duration result = RetryStrategy.calculateRetryDelay(retryAfterDelay, retryCount, minimumRetryDelay);

        // Then
        // Should use default 100ms base delay
        // For retry count 1: 2^1 * 100ms = 200ms base
        // With jitter: between 200ms and 400ms
        assertThat(result.toMillis()).isBetween(200L, 400L);
    }

    @Test
    void shouldRetry_with429_shouldReturnTrue() {
        assertThat(RetryStrategy.shouldRetry(429)).isTrue();
    }

    @Test
    void shouldRetry_with500_shouldReturnTrue() {
        assertThat(RetryStrategy.shouldRetry(500)).isTrue();
    }

    @Test
    void shouldRetry_with502_shouldReturnTrue() {
        assertThat(RetryStrategy.shouldRetry(502)).isTrue();
    }

    @Test
    void shouldRetry_with503_shouldReturnTrue() {
        assertThat(RetryStrategy.shouldRetry(503)).isTrue();
    }

    @Test
    void shouldRetry_with504_shouldReturnTrue() {
        assertThat(RetryStrategy.shouldRetry(504)).isTrue();
    }

    @Test
    void shouldRetry_with400_shouldReturnFalse() {
        assertThat(RetryStrategy.shouldRetry(400)).isFalse();
    }

    @Test
    void shouldRetry_with404_shouldReturnFalse() {
        assertThat(RetryStrategy.shouldRetry(404)).isFalse();
    }

    @Test
    void shouldRetry_with501_shouldReturnFalse() {
        assertThat(RetryStrategy.shouldRetry(501)).isFalse();
    }

    @Test
    void calculateRetryDelay_withZeroRetryAfter_shouldEnforceMinimumDelay() {
        // Given - Server sends Retry-After: 0 (problematic!)
        Optional<Duration> retryAfterDelay = Optional.of(Duration.ZERO);
        int retryCount = 1;
        Duration minimumRetryDelay = Duration.ofMillis(100);

        // When
        Duration result = RetryStrategy.calculateRetryDelay(retryAfterDelay, retryCount, minimumRetryDelay);

        // Then - Should enforce minimum delay to prevent hot-loop retries
        // Current code will FAIL this test by returning Duration.ZERO
        assertThat(result.toMillis()).isGreaterThanOrEqualTo(1); // At least 1ms to prevent hot-loops
    }

    @Test
    void calculateRetryDelay_withNegativeRetryAfter_shouldEnforceMinimumDelay() {
        // Given - Server sends malformed negative delay (very problematic!)
        Optional<Duration> retryAfterDelay = Optional.of(Duration.ofMillis(-500));
        int retryCount = 1;
        Duration minimumRetryDelay = Duration.ofMillis(100);

        // When
        Duration result = RetryStrategy.calculateRetryDelay(retryAfterDelay, retryCount, minimumRetryDelay);

        // Then - Should enforce minimum delay to handle malformed server responses
        // Current code will FAIL this test by returning negative duration
        assertThat(result.toMillis()).isGreaterThanOrEqualTo(1); // At least 1ms for safety
    }

    @Test
    void calculateRetryDelay_withVerySmallRetryAfter_shouldEnforceMinimumDelay() {
        // Given - Server sends extremely small delay (could cause near-hot-loop)
        Optional<Duration> retryAfterDelay = Optional.of(Duration.ofNanos(500)); // 0.0005ms
        int retryCount = 1;
        Duration minimumRetryDelay = Duration.ofMillis(100);

        // When
        Duration result = RetryStrategy.calculateRetryDelay(retryAfterDelay, retryCount, minimumRetryDelay);

        // Then - Should enforce reasonable minimum delay
        // Current code will FAIL this test by returning tiny delay
        assertThat(result.toMillis()).isGreaterThanOrEqualTo(1); // At least 1ms for system stability
    }
}
