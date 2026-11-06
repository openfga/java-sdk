package dev.openfga.sdk.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AccessTokenTest {

    private static final int DEFAULT_BUFFER = 300;
    private static final int DEFAULT_JITTER = 300;

    private static Stream<Arguments> expTimeAndResults() {
        Instant now = Instant.now();
        return Stream.of(
                // Default timing (300s buffer, up to 300s jitter)
                Arguments.of(
                        "Expires in 1 hour should be valid",
                        now.plus(1, ChronoUnit.HOURS),
                        DEFAULT_BUFFER,
                        DEFAULT_JITTER,
                        true),
                Arguments.of(
                        "Expires in 15 minutes should be valid",
                        now.plus(15, ChronoUnit.MINUTES),
                        DEFAULT_BUFFER,
                        DEFAULT_JITTER,
                        true),
                Arguments.of("No expiry value should be valid", null, DEFAULT_BUFFER, DEFAULT_JITTER, true),
                Arguments.of(
                        "Expired 1 hour ago should not be valid",
                        now.minus(1, ChronoUnit.HOURS),
                        DEFAULT_BUFFER,
                        DEFAULT_JITTER,
                        false),
                Arguments.of(
                        "Expired 10 minutes ago should not be valid",
                        now.minus(10, ChronoUnit.MINUTES),
                        DEFAULT_BUFFER,
                        DEFAULT_JITTER,
                        false),
                Arguments.of(
                        "Expired 5 minutes ago should not be valid",
                        now.minus(5, ChronoUnit.MINUTES),
                        DEFAULT_BUFFER,
                        DEFAULT_JITTER,
                        false),
                Arguments.of(
                        "Expires in 5 minutes should not be valid",
                        now.plus(5, ChronoUnit.MINUTES),
                        DEFAULT_BUFFER,
                        DEFAULT_JITTER,
                        false),
                Arguments.of(
                        "Expires in 1 minute should not be valid",
                        now.plus(1, ChronoUnit.MINUTES),
                        DEFAULT_BUFFER,
                        DEFAULT_JITTER,
                        false),
                Arguments.of("Expires now should not be valid", now, DEFAULT_BUFFER, DEFAULT_JITTER, false),
                // Non-default timing
                Arguments.of(
                        "Expires in 5 minutes with 400s buffer should not be valid",
                        now.plus(5, ChronoUnit.MINUTES),
                        400,
                        0,
                        false),
                Arguments.of(
                        "Expires in 5 minutes with 30s buffer should be valid",
                        now.plus(5, ChronoUnit.MINUTES),
                        30,
                        0,
                        true),
                Arguments.of(
                        "Expires in 5 minutes with no buffer and 300s jitter should be valid",
                        now.plus(5, ChronoUnit.MINUTES),
                        0,
                        300,
                        true),
                Arguments.of(
                        "Expires in 1 minute with no buffer or jitter should be valid",
                        now.plus(1, ChronoUnit.MINUTES),
                        0,
                        0,
                        true),
                Arguments.of("Expires now with no buffer or jitter should not be valid", now, 0, 0, false));
    }

    @MethodSource("expTimeAndResults")
    @ParameterizedTest(name = "{0}")
    void testTokenValid(String name, Instant exp, int bufferSeconds, int jitterSeconds, boolean valid) {
        AccessToken snapshot = new AccessToken("token", exp);
        assertEquals(valid, snapshot.isValid(bufferSeconds, jitterSeconds));
    }
}
