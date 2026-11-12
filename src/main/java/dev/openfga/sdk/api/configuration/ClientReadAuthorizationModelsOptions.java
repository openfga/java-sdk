package dev.openfga.sdk.api.configuration;

import java.util.Map;

public class ClientReadAuthorizationModelsOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String storeId;
    private Integer pageSize;
    private String continuationToken;

    public ClientReadAuthorizationModelsOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientReadAuthorizationModelsOptions pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public ClientReadAuthorizationModelsOptions continuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
        return this;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    /**
     * Sets the store to send this request to, instead of the store in the client configuration.
     */
    public ClientReadAuthorizationModelsOptions storeId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }
}
