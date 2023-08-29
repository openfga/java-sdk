package dev.openfga.sdk.api.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Credentials Flow Response
 * <p>
 * {@see "https://auth0.com/docs/get-started/authentication-and-authorization-flow/client-credentials-flow"}
 */
@JsonPropertyOrder({
    CredentialsFlowResponse.JSON_PROPERTY_ACCESS_TOKEN,
    CredentialsFlowResponse.JSON_PROPERTY_SCOPE,
    CredentialsFlowResponse.JSON_PROPERTY_EXPIRES_IN,
    CredentialsFlowResponse.JSON_PROPERTY_TOKEN_TYPE
})
class CredentialsFlowResponse {
    public static final String JSON_PROPERTY_ACCESS_TOKEN = "access_token";
    private String accessToken;

    public static final String JSON_PROPERTY_SCOPE = "scope";
    private String scope;

    public static final String JSON_PROPERTY_EXPIRES_IN = "expires_in";
    private long expiresInSeconds;

    public static final String JSON_PROPERTY_TOKEN_TYPE = "token_type";
    private String tokenType;

    @JsonProperty(JSON_PROPERTY_ACCESS_TOKEN)
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty(JSON_PROPERTY_ACCESS_TOKEN)
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty(JSON_PROPERTY_SCOPE)
    public String getScope() {
        return scope;
    }

    @JsonProperty(JSON_PROPERTY_SCOPE)
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * The expiration time, in seconds.
     * <p>
     * By the convention of RFC 6749 section 5.1, an expires_in value from a response will be understood
     * as a value in seconds. {@see https://datatracker.ietf.org/doc/html/rfc6749#autoid-55}
     * @return The expiration time, from now, in seconds
     */
    @JsonProperty(JSON_PROPERTY_EXPIRES_IN)
    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    @JsonProperty(JSON_PROPERTY_EXPIRES_IN)
    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    @JsonProperty(JSON_PROPERTY_TOKEN_TYPE)
    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty(JSON_PROPERTY_TOKEN_TYPE)
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
