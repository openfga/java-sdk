package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.errors.FgaInvalidParameterException;

public class Credentials {
    private CredentialsMethod credentialsMethod;
    private ApiToken apiToken;
    private ClientCredentials clientCredentials;

    public Credentials() {
        this.credentialsMethod = CredentialsMethod.NONE;
    }

    public Credentials(ApiToken apiToken) {
        this.credentialsMethod = CredentialsMethod.API_TOKEN;
        this.apiToken = apiToken;
    }

    public Credentials(ClientCredentials clientCredentials) {
        this.credentialsMethod = CredentialsMethod.CLIENT_CREDENTIALS;
        this.clientCredentials = clientCredentials;
    }

    public void assertValid() throws FgaInvalidParameterException {
        if (credentialsMethod == CredentialsMethod.API_TOKEN && apiToken == null) {
            throw new FgaInvalidParameterException("apiToken", "Credentials");
        }

        if (credentialsMethod == CredentialsMethod.CLIENT_CREDENTIALS && clientCredentials == null) {
            throw new FgaInvalidParameterException("clientCredentials", "Credentials");
        }
    }

    public void setCredentialsMethod(CredentialsMethod credentialsMethod) {
        this.credentialsMethod = credentialsMethod;
    }

    public CredentialsMethod getCredentialsMethod() {
        return credentialsMethod;
    }

    public void setApiToken(ApiToken apiToken) {
        this.apiToken = apiToken;
    }

    public ApiToken getApiToken() {
        return apiToken;
    }

    public void setClientCredentials(ClientCredentials clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    public ClientCredentials getClientCredentials() {
        return clientCredentials;
    }
}
