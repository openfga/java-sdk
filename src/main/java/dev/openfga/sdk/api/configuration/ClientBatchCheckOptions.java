package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.ConsistencyPreference;
import java.util.Map;

public class ClientBatchCheckOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private Integer maxParallelRequests;
    private Integer maxBatchSize;
    private String authorizationModelId;
    private ConsistencyPreference consistency;

    public ClientBatchCheckOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientBatchCheckOptions maxParallelRequests(Integer maxParallelRequests) {
        this.maxParallelRequests = maxParallelRequests;
        return this;
    }

    public Integer getMaxParallelRequests() {
        return maxParallelRequests;
    }

    public ClientBatchCheckOptions maxBatchSize(Integer maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
        return this;
    }

    public Integer getMaxBatchSize() {
        return maxBatchSize;
    }

    public ClientBatchCheckOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public ClientBatchCheckOptions consistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
        return this;
    }

    public ConsistencyPreference getConsistency() {
        return consistency;
    }
}
