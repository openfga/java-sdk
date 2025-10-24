package dev.openfga.sdk.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AccessTokenTest {

    private static Stream<Arguments> expTimeAndResults() {
        return Stream.of(
                Arguments.of("Expires in 1 hour should be valid", Instant.now().plus(1, ChronoUnit.HOURS), true),
                Arguments.of(
                        "Expires in 15 minutes should be valid", Instant.now().plus(15, ChronoUnit.MINUTES), true),
                Arguments.of("No expiry value should be valid", null, true),
                Arguments.of(
                        "Expired 1 hour ago should not be valid", Instant.now().minus(1, ChronoUnit.HOURS), false),
                Arguments.of(
                        "Expired 10 minutes ago should not be valid",
                        Instant.now().minus(10, ChronoUnit.MINUTES),
                        false),
                Arguments.of(
                        "Expired 5 minutes ago should not be valid",
                        Instant.now().minus(5, ChronoUnit.MINUTES),
                        false),
                Arguments.of(
                        "Expires in 5 minutes should not be valid",
                        Instant.now().plus(5, ChronoUnit.MINUTES),
                        false),
                Arguments.of(
                        "Expires in 1 minute should not be valid", Instant.now().plus(1, ChronoUnit.MINUTES), false),
                Arguments.of("Expires now should not be valid", Instant.now(), false));
    }

    @MethodSource("expTimeAndResults")
    @ParameterizedTest(name = "{0}")
    public void testTokenValid(String name, Instant exp, boolean valid) {
        AccessToken accessToken = new AccessToken();
        accessToken.setToken("token");
        accessToken.setExpiresAt(exp);
        assertEquals(valid, accessToken.isValid());
    }
}
