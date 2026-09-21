package dev.openfga.sdk.api.auth;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Immutable snapshot of an access token and its expiry time. The snapshot is valid if the token is non-empty
 * and the current time is before the expiry time minus a buffer to ensure that callers receive a valid token
 * even if there is some clock skew or delay between retrieval and use.
 */
record AccessToken(String token, Instant expiresAt) {
    static final AccessToken EMPTY = new AccessToken(null, null);

    AccessToken {
        expiresAt = expiresAt != null ? expiresAt.truncatedTo(ChronoUnit.SECONDS) : null;
    }

    boolean isValid(int bufferSeconds, int jitterSeconds) {
        if (isNullOrWhitespace(token)) {
            return false;
        }

        // Is expiry is null then the token will not expire so should be considered always valid
        if (expiresAt == null) {
            return true;
        }

        // Refresh before expiry, with optional jitter to spread refreshes across clients.
        Instant expiresWithLeeway = expiresAt
                .minusSeconds(bufferSeconds)
                .minusSeconds(
                        jitterSeconds == 0 ? 0 : ThreadLocalRandom.current().nextInt(jitterSeconds))
                .truncatedTo(ChronoUnit.SECONDS);

        return Instant.now().truncatedTo(ChronoUnit.SECONDS).isBefore(expiresWithLeeway);
    }
}
