package dev.openfga.sdk.api.auth;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.constants.FgaConstants;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Immutable snapshot of an access token and its expiry time. The snapshot is valid if the token is non-empty
 * and the current time is before the expiry time minus a buffer to ensure that callers receive a valid token
 * even if there is some clock skew or delay between retrieval and use.
 */
record TokenSnapshot(String token, Instant expiresAt) {
    private static final int EXPIRY_BUFFER_SECS = FgaConstants.TOKEN_EXPIRY_THRESHOLD_BUFFER_IN_SEC;
    private static final int EXPIRY_JITTER_SECS = FgaConstants.TOKEN_EXPIRY_JITTER_IN_SEC;

    static final TokenSnapshot EMPTY = new TokenSnapshot(null, null);

    TokenSnapshot {
        expiresAt = expiresAt != null ? expiresAt.truncatedTo(ChronoUnit.SECONDS) : null;
    }

    boolean isValid() {
        if (isNullOrWhitespace(token)) {
            return false;
        }
        if (expiresAt == null) {
            return true;
        }
        Instant expiresWithLeeway = expiresAt
                .minusSeconds(EXPIRY_BUFFER_SECS)
                .minusSeconds(ThreadLocalRandom.current().nextInt(EXPIRY_JITTER_SECS))
                .truncatedTo(ChronoUnit.SECONDS);

        return Instant.now().truncatedTo(ChronoUnit.SECONDS).isBefore(expiresWithLeeway);
    }
}
