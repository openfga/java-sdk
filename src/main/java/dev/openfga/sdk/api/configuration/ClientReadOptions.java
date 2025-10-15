package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.ConsistencyPreference;
import java.util.Map;

public class ClientReadOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private Integer pageSize;
    private String continuationToken;
    private ConsistencyPreference consistency;

    public ClientReadOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientReadOptions pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public ClientReadOptions continuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
        return this;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public ClientReadOptions consistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
        return this;
    }

    public ConsistencyPreference getConsistency() {
        return consistency;
    }
}
