package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.ConsistencyPreference;
import java.util.Map;

public class ClientListUsersOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String storeId;
    private String authorizationModelId;
    private ConsistencyPreference consistency;

    public ClientListUsersOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientListUsersOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public ClientListUsersOptions consistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
        return this;
    }

    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    /**
     * Sets the store to send this request to, instead of the store in the client configuration.
     */
    public ClientListUsersOptions storeId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }
}
