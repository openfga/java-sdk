package dev.openfga.sdk.util;

import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.HttpStatusCode;
import java.time.Duration;
import java.util.Optional;

/**
 * Utility class for determining retry behavior based on HTTP status codes.
 *
 * Implements simplified retry logic per GitHub issue #155:
 * - Retry on 429s for all requests
 * - Retry on 5xx errors (except 501) for all requests
 * - Honor Retry-After header when present, fallback to exponential backoff
 */
public class RetryStrategy {

    private RetryStrategy() {
        // Utility class - no instantiation
    }

    /**
     * Determines if a request should be retried based on the status code.
     *
     * Simplified Retry Logic per GitHub issue #155:
     * - 429 (Too Many Requests): Always retry for all requests
     * - 5xx errors (except 501): Always retry for all requests
     * - All other status codes: Do not retry
     *
     * @param statusCode The HTTP response status code
     * @return true if the request should be retried, false otherwise
     */
    public static boolean shouldRetry(int statusCode) {
        // Always retry 429 (Too Many Requests) for all requests
        if (statusCode == HttpStatusCode.TOO_MANY_REQUESTS) {
            return true;
        }

        // Always retry 5xx errors (except 501 Not Implemented) for all requests
        if (HttpStatusCode.isServerError(statusCode) && statusCode != HttpStatusCode.NOT_IMPLEMENTED) {
            return true;
        }

        return false;
    }

    /**
     * Calculates the appropriate retry delay based on the presence of Retry-After header and retry count.
     *
     * @param retryAfterDelay Optional delay from Retry-After header
     * @param retryCount Current retry attempt (0-based)
     * @param minimumRetryDelay Minimum delay to enforce (only used when no Retry-After header present)
     * @return Duration representing the delay before the next retry
     */
    public static Duration calculateRetryDelay(
            Optional<Duration> retryAfterDelay, int retryCount, Duration minimumRetryDelay) {
        // If Retry-After header is present, use it but enforce minimum delay floor
        if (retryAfterDelay.isPresent()) {
            Duration serverDelay = retryAfterDelay.get();
            // Clamp to minimum 1ms to prevent hot-loop retries and handle malformed server responses
            Duration minimumSafeDelay = Duration.ofMillis(1);
            return serverDelay.compareTo(minimumSafeDelay) < 0 ? minimumSafeDelay : serverDelay;
        }

        // Otherwise, use exponential backoff with jitter, respecting minimum retry delay
        Duration baseDelay = minimumRetryDelay != null ? minimumRetryDelay : FgaConstants.DEFAULT_MIN_WAIT_IN_MS;
        return ExponentialBackoff.calculateDelay(retryCount, baseDelay);
    }
}
