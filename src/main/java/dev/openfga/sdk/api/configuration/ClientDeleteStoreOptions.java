package dev.openfga.sdk.api.configuration;

import java.util.Map;

public class ClientDeleteStoreOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;

    public ClientDeleteStoreOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }
}
