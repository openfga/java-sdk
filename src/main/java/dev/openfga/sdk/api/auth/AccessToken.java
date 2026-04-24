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
record AccessToken(String token, Instant expiresAt) {
    private static final int TOKEN_EXPIRY_BUFFER_THRESHOLD_IN_SEC = FgaConstants.TOKEN_EXPIRY_THRESHOLD_BUFFER_IN_SEC;
    // We add some jitter so that token refreshes are less likely to collide
    private static final int TOKEN_EXPIRY_JITTER_IN_SEC = FgaConstants.TOKEN_EXPIRY_JITTER_IN_SEC;

    static final AccessToken EMPTY = new AccessToken(null, null);

    AccessToken {
        expiresAt = expiresAt != null ? expiresAt.truncatedTo(ChronoUnit.SECONDS) : null;
    }

    boolean isValid() {
        if (isNullOrWhitespace(token)) {
            return false;
        }

        // Is expiry is null then the token will not expire so should be considered always valid
        if (expiresAt == null) {
            return true;
        }

        // A token should be considered valid until 5 minutes before the expiry with some jitter
        // to account for multiple calls to `isValid` at the same time and prevent multiple refresh calls
        Instant expiresWithLeeway = expiresAt
                .minusSeconds(TOKEN_EXPIRY_BUFFER_THRESHOLD_IN_SEC)
                .minusSeconds(ThreadLocalRandom.current().nextInt(TOKEN_EXPIRY_JITTER_IN_SEC))
                .truncatedTo(ChronoUnit.SECONDS);

        return Instant.now().truncatedTo(ChronoUnit.SECONDS).isBefore(expiresWithLeeway);
    }
}
