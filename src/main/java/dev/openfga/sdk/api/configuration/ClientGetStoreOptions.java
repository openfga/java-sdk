package dev.openfga.sdk.api.configuration;

import java.util.Map;

public class ClientGetStoreOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;

    public ClientGetStoreOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }
}
