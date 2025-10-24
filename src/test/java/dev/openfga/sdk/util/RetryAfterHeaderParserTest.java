package dev.openfga.sdk.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RetryAfterHeaderParserTest {

    @Test
    void parseRetryAfter_withValidIntegerSeconds_shouldReturnDuration() {
        // Given
        String retryAfterValue = "30";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void parseRetryAfter_withMinimumValidSeconds_shouldReturnDuration() {
        // Given
        String retryAfterValue = "1";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(Duration.ofSeconds(1));
    }

    @Test
    void parseRetryAfter_withMaximumValidSeconds_shouldReturnDuration() {
        // Given
        String retryAfterValue = "1800"; // 30 minutes

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(Duration.ofSeconds(1800));
    }

    @Test
    void parseRetryAfter_withZeroSeconds_shouldReturnEmpty() {
        // Given
        String retryAfterValue = "0";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withTooLargeSeconds_shouldReturnEmpty() {
        // Given
        String retryAfterValue = "1801"; // More than 30 minutes

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withNegativeSeconds_shouldReturnEmpty() {
        // Given
        String retryAfterValue = "-5";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withValidHttpDate_shouldReturnDuration() {
        // Given
        Instant futureTime = Instant.now().plusSeconds(120); // 2 minutes from now
        String retryAfterValue =
                DateTimeFormatter.RFC_1123_DATE_TIME.format(futureTime.atZone(java.time.ZoneOffset.UTC));

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isPresent();
        // Allow some tolerance for execution time
        assertThat(result.get().getSeconds()).isBetween(115L, 125L);
    }

    @Test
    void parseRetryAfter_withPastHttpDate_shouldReturnEmpty() {
        // Given
        Instant pastTime = Instant.now().minusSeconds(60); // 1 minute ago
        String retryAfterValue = DateTimeFormatter.RFC_1123_DATE_TIME.format(pastTime.atZone(java.time.ZoneOffset.UTC));

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withTooFarFutureHttpDate_shouldReturnEmpty() {
        // Given
        Instant farFutureTime = Instant.now().plusSeconds(2000); // More than 30 minutes
        String retryAfterValue =
                DateTimeFormatter.RFC_1123_DATE_TIME.format(farFutureTime.atZone(java.time.ZoneOffset.UTC));

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withInvalidHttpDate_shouldReturnEmpty() {
        // Given
        String retryAfterValue = "Invalid Date Format";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withNullValue_shouldReturnEmpty() {
        // Given
        String retryAfterValue = null;

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withEmptyValue_shouldReturnEmpty() {
        // Given
        String retryAfterValue = "";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withWhitespaceValue_shouldReturnEmpty() {
        // Given
        String retryAfterValue = "   ";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void parseRetryAfter_withValueWithWhitespace_shouldTrimAndParse() {
        // Given
        String retryAfterValue = "  30  ";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void parseRetryAfter_withNonNumericValue_shouldReturnEmpty() {
        // Given
        String retryAfterValue = "not-a-number";

        // When
        Optional<Duration> result = RetryAfterHeaderParser.parseRetryAfter(retryAfterValue);

        // Then
        assertThat(result).isEmpty();
    }
}
