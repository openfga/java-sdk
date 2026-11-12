package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.ConsistencyPreference;
import java.util.HashMap;
import java.util.Map;

public class ClientListRelationsOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String storeId;
    private Integer maxParallelRequests;
    private String authorizationModelId;
    private ConsistencyPreference consistency;

    public ClientListRelationsOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientListRelationsOptions maxParallelRequests(Integer maxParallelRequests) {
        this.maxParallelRequests = maxParallelRequests;
        return this;
    }

    public Integer getMaxParallelRequests() {
        return maxParallelRequests;
    }

    public ClientListRelationsOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public ClientListRelationsOptions consistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
        return this;
    }

    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    public ClientBatchCheckClientOptions asClientBatchCheckClientOptions() {
        return new ClientBatchCheckClientOptions()
                .authorizationModelId(authorizationModelId)
                .additionalHeaders(additionalHeaders != null ? new HashMap<>(additionalHeaders) : null)
                .maxParallelRequests(maxParallelRequests)
                .storeId(storeId)
                .consistency(consistency);
    }

    /**
     * Sets the store to send this request to, instead of the store in the client configuration.
     */
    public ClientListRelationsOptions storeId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }
}
