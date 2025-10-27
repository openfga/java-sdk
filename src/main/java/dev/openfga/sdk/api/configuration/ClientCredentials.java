package dev.openfga.sdk.api.configuration;

import static dev.openfga.sdk.util.Validation.assertParamExists;

import dev.openfga.sdk.errors.FgaInvalidParameterException;

public class ClientCredentials {
    private String clientId;
    private String clientSecret;
    private String apiTokenIssuer;
    private String apiAudience;
    private String scopes;

    public ClientCredentials() {}

    public ClientCredentials clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public void assertValid() throws FgaInvalidParameterException {
        assertParamExists(clientId, "clientId", "ClientCredentials");
        assertParamExists(clientSecret, "clientSecret", "ClientCredentials");
        assertParamExists(apiTokenIssuer, "apiTokenIssuer", "ClientCredentials");
        assertParamExists(apiAudience, "apiAudience", "ClientCredentials");
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
        this.apiAudience = apiAudience;
        return this;
    }

    public String getApiAudience() {
        return this.apiAudience;
    }

    public ClientCredentials scopes(String scopes) {
        this.scopes = scopes;
        return this;
    }

    public String getScopes() {
        return this.scopes;
    }
}
