package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.ConsistencyPreference;
import java.util.HashMap;
import java.util.Map;

public class ClientBatchCheckClientOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private Integer maxParallelRequests;
    private String authorizationModelId;
    private ConsistencyPreference consistency;

    public ClientBatchCheckClientOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientBatchCheckClientOptions maxParallelRequests(Integer maxParallelRequests) {
        this.maxParallelRequests = maxParallelRequests;
        return this;
    }

    public Integer getMaxParallelRequests() {
        return maxParallelRequests;
    }

    public ClientBatchCheckClientOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public ClientBatchCheckClientOptions consistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
        return this;
    }

    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    public ClientCheckOptions asClientCheckOptions() {
        return new ClientCheckOptions()
                .additionalHeaders(additionalHeaders != null ? new HashMap<>(additionalHeaders) : null)
                .authorizationModelId(authorizationModelId)
                .consistency(consistency);
    }
}
