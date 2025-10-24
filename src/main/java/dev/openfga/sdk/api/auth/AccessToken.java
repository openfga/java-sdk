package dev.openfga.sdk.api.auth;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.constants.FgaConstants;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

class AccessToken {
    private static final int TOKEN_EXPIRY_BUFFER_THRESHOLD_IN_SEC = FgaConstants.TOKEN_EXPIRY_THRESHOLD_BUFFER_IN_SEC;
    // We add some jitter so that token refreshes are less likely to collide
    private static final int TOKEN_EXPIRY_JITTER_IN_SEC = FgaConstants.TOKEN_EXPIRY_JITTER_IN_SEC;

    private Instant expiresAt;

    private final Random random = new Random();
    private String token;

    public boolean isValid() {
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
                .minusSeconds(random.nextInt(TOKEN_EXPIRY_JITTER_IN_SEC))
                .truncatedTo(ChronoUnit.SECONDS);

        return Instant.now().truncatedTo(ChronoUnit.SECONDS).isBefore(expiresWithLeeway);
    }

    public String getToken() {
        return token;
    }

    public void setExpiresAt(Instant expiresAt) {
        if (expiresAt != null) {
            // Truncate to seconds to zero out the milliseconds to keep comparison simpler
            this.expiresAt = expiresAt.truncatedTo(ChronoUnit.SECONDS);
        }
    }

    public void setToken(String token) {
        this.token = token;
    }
}
