package dev.openfga.sdk.api.auth;

import static dev.openfga.util.StringUtil.isNullOrWhitespace;

import java.time.Instant;
import java.util.Random;

public class AuthToken {
    // TODO: Check where these comes from
    private static final int TOKEN_EXPIRY_BUFFER_THRESHOLD_IN_SEC = 300;
    private static final int TOKEN_EXPIRY_JITTER_IN_SEC =
            300; // We add some jitter so that token refreshes are less likely to collide

    private final Random random = new Random();
    private Instant expiresAt;

    private String accessToken;

    public boolean isValid() {
        return !isNullOrWhitespace(accessToken)
                && (expiresAt == null
                        || expiresAt.isBefore(Instant.now()
                                .plusSeconds(TOKEN_EXPIRY_BUFFER_THRESHOLD_IN_SEC)
                                .plusSeconds(random.nextLong() % TOKEN_EXPIRY_JITTER_IN_SEC)));
    }

    public String getAccessToken() {
        return accessToken;
    }
}
