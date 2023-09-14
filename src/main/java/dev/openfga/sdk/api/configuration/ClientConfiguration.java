package dev.openfga.sdk.api.configuration;

import java.util.Optional;

public class ClientConfiguration extends Configuration {
    private String authorizationModelId;

    public ClientConfiguration(String apiUrl) {
        super(apiUrl);
    }

    public ClientConfiguration(String apiUrl, String authorizationModelId) {
        super(apiUrl);
        this.authorizationModelId = authorizationModelId;
    }

    public ClientConfiguration(String apiUrl, Credentials credentials) {
        super(apiUrl, credentials);
    }

    public ClientConfiguration(String apiUrl, String authorizationModelId, Credentials credentials) {
        super(apiUrl, credentials);
        this.authorizationModelId = authorizationModelId;
    }

    /**
     * Set the Authorization Model ID.
     *
     * @param authorizationModelId The URL.
     * @return This object.
     */
    public Configuration authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    /**
     * Get the Authorization Model ID.
     *
     * @return The Authorization Model ID.
     */
    public Optional<String> getAuthorizationModelId() {
        return Optional.ofNullable(authorizationModelId);
    }
}
