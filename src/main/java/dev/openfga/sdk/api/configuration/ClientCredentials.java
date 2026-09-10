package dev.openfga.sdk.api.configuration;

import static dev.openfga.sdk.util.Validation.assertParamExists;

import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.util.StringUtil;

public class ClientCredentials {
    private String clientId;
    private String clientSecret;
    private String apiTokenIssuer;
    private String apiAudience;
    private String scopes;
    private int tokenExpiryBufferSeconds = FgaConstants.TOKEN_EXPIRY_THRESHOLD_BUFFER_IN_SEC;
    private int tokenExpiryJitterSeconds = FgaConstants.TOKEN_EXPIRY_JITTER_IN_SEC;

    public ClientCredentials() {}

    public ClientCredentials clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public void assertValid() throws FgaInvalidParameterException {
        assertParamExists(clientId, "clientId", "ClientCredentials");
        assertParamExists(clientSecret, "clientSecret", "ClientCredentials");
        assertParamExists(apiTokenIssuer, "apiTokenIssuer", "ClientCredentials");
    }

    public String getClientId() {
        return this.clientId;
    }

    public ClientCredentials clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public ClientCredentials apiTokenIssuer(String apiTokenIssuer) {
        this.apiTokenIssuer = apiTokenIssuer;
        return this;
    }

    public String getApiTokenIssuer() {
        return this.apiTokenIssuer;
    }

    public ClientCredentials apiAudience(String apiAudience) {
        this.apiAudience = StringUtil.isNullOrWhitespace(apiAudience) ? null : apiAudience;
        return this;
    }

    public String getApiAudience() {
        return this.apiAudience;
    }

    public ClientCredentials scopes(String scopes) {
        this.scopes = StringUtil.isNullOrWhitespace(scopes) ? null : scopes;
        return this;
    }

    public String getScopes() {
        return this.scopes;
    }

    /**
     * Sets how many seconds before expiry a cached token requires refresh. Defaults to 300.
     * @throws IllegalArgumentException if seconds is negative.
     */
    public ClientCredentials tokenExpiryBufferSeconds(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("tokenExpiryBufferSeconds must be non-negative");
        }
        this.tokenExpiryBufferSeconds = seconds;
        return this;
    }

    public int getTokenExpiryBufferSeconds() {
        return this.tokenExpiryBufferSeconds;
    }

    /**
     * Sets the exclusive upper bound of additional random seconds subtracted on each expiry check.
     * Defaults to 300. Set to zero to disable jitter.
     * @throws IllegalArgumentException if seconds is negative.
     */
    public ClientCredentials tokenExpiryJitterSeconds(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("tokenExpiryJitterSeconds must be non-negative");
        }
        this.tokenExpiryJitterSeconds = seconds;
        return this;
    }

    public int getTokenExpiryJitterSeconds() {
        return this.tokenExpiryJitterSeconds;
    }
}
