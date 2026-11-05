package dev.openfga.sdk.api.configuration;

import java.util.Map;

public class ClientReadLatestAuthorizationModelOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String storeId;

    public ClientReadLatestAuthorizationModelOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    /**
     * Sets the store to send this request to, instead of the store in the client configuration.
     */
    public ClientReadLatestAuthorizationModelOptions storeId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }
}
