package dev.openfga.sdk.util;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Utility class for parsing and validating Retry-After header values according to RFC 9110.
 *
 * The Retry-After header can contain either:
 * 1. An integer representing seconds to wait
 * 2. An HTTP-date representing when to retry
 *
 * This parser validates that the delay is between 1 second and 1800 seconds (30 minutes).
 */
public class RetryAfterHeaderParser {

    private static final int MIN_RETRY_AFTER_SECONDS = 1;
    private static final int MAX_RETRY_AFTER_SECONDS = 1800; // 30 minutes

    private RetryAfterHeaderParser() {
        // Utility class - no instantiation
    }

    /**
     * Parses a Retry-After header value and returns the delay duration if valid.
     *
     * @param retryAfterValue The value of the Retry-After header
     * @return Optional containing the delay duration if valid, empty otherwise
     */
    public static Optional<Duration> parseRetryAfter(String retryAfterValue) {
        if (retryAfterValue == null || retryAfterValue.trim().isEmpty()) {
            return Optional.empty();
        }

        String trimmedValue = retryAfterValue.trim();

        // Try parsing as integer (seconds)
        Optional<Duration> integerResult = parseAsInteger(trimmedValue);
        if (integerResult.isPresent()) {
            return integerResult;
        }

        // Try parsing as HTTP-date
        return parseAsHttpDate(trimmedValue);
    }

    /**
     * Attempts to parse the value as an integer representing seconds.
     */
    private static Optional<Duration> parseAsInteger(String value) {
        try {
            long seconds = Long.parseLong(value);

            // Validate range: must be between 1 and 1800 seconds
            if (seconds >= MIN_RETRY_AFTER_SECONDS && seconds <= MAX_RETRY_AFTER_SECONDS) {
                return Optional.of(Duration.ofSeconds(seconds));
            }

            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to parse the value as an HTTP-date.
     * Supports RFC 1123 format as specified in RFC 9110.
     */
    private static Optional<Duration> parseAsHttpDate(String value) {
        try {
            // Parse HTTP-date in RFC 1123 format (e.g., "Sun, 06 Nov 1994 08:49:37 GMT")
            Instant retryTime = Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(value));
            Instant now = Instant.now();

            // Calculate duration from now
            Duration duration = Duration.between(now, retryTime);

            // Validate range: must be between 1 and 1800 seconds from now
            long seconds = duration.getSeconds();
            if (seconds >= MIN_RETRY_AFTER_SECONDS && seconds <= MAX_RETRY_AFTER_SECONDS) {
                return Optional.of(duration);
            }

            return Optional.empty();
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
