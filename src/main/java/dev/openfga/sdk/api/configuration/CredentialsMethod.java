package dev.openfga.sdk.api.configuration;

/**
 * Mutually exclusive methods for delivering credentials.
 */
public enum CredentialsMethod {
    /**
     * No credentials.
     */
    NONE,

    /**
     * A static API token. In OAuth2 terms, this indicates an "access token"
     * that will be used to make a request. When used as part of {@link Configuration}
     * then an {@link ApiToken} should also be defined.
     */
    API_TOKEN,

    /**
     * OAuth2 client credentials that can be used to acquire an OAuth2 access
     * token. When used as part of {@link Configuration} then a
     * {@link ClientCredentials} should also be defined.
     */
    CLIENT_CREDENTIALS;
}
