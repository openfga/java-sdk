package dev.openfga.sdk.api.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Authentication request.
 */
@JsonPropertyOrder({
    CredentialsFlowRequest.JSON_PROPERTY_CLIENT_ID,
    CredentialsFlowRequest.JSON_PROPERTY_CLIENT_SECRET,
    CredentialsFlowRequest.JSON_PROPERTY_AUDIENCE,
    CredentialsFlowRequest.JSON_PROPERTY_GRANT_TYPE
})
class CredentialsFlowRequest {
    public static final String JSON_PROPERTY_CLIENT_ID = "client_id";
    private String clientId;

    public static final String JSON_PROPERTY_CLIENT_SECRET = "client_secret";
    private String clientSecret;

    public static final String JSON_PROPERTY_AUDIENCE = "audience";
    private String audience;

    public static final String JSON_PROPERTY_GRANT_TYPE = "grant_type";
    private String grantType;

    @JsonCreator
    public CredentialsFlowRequest() {}

    @JsonProperty(JSON_PROPERTY_CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_PROPERTY_CLIENT_ID)
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty(JSON_PROPERTY_CLIENT_SECRET)
    public String getClientSecret() {
        return clientSecret;
    }

    @JsonProperty(JSON_PROPERTY_CLIENT_SECRET)
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @JsonProperty(JSON_PROPERTY_AUDIENCE)
    public String getAudience() {
        return audience;
    }

    @JsonProperty(JSON_PROPERTY_AUDIENCE)
    public void setAudience(String audience) {
        this.audience = audience;
    }

    @JsonProperty(JSON_PROPERTY_GRANT_TYPE)
    public String getGrantType() {
        return grantType;
    }

    @JsonProperty(JSON_PROPERTY_GRANT_TYPE)
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
