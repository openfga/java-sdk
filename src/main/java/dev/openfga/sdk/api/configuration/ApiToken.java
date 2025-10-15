package dev.openfga.sdk.api.configuration;

/**
 * A static API token. In OAuth2 terms, this indicates an "access token"
 * that will be used to authenticate a request.
 */
public class ApiToken {
    private String token;

    public ApiToken(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
