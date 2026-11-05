package dev.openfga.sdk.api.configuration;

import java.util.Map;

public class ClientWriteAuthorizationModelOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String storeId;

    public ClientWriteAuthorizationModelOptions additionalHeaders(Map<String, String> additionalHeaders) {
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
    public ClientWriteAuthorizationModelOptions storeId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }
}
