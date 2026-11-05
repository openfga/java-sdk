package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.WriteRequestDeletes;
import java.util.Map;

public class ClientDeleteTuplesOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String storeId;
    private WriteRequestDeletes.OnMissingEnum onMissing;

    public ClientDeleteTuplesOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientDeleteTuplesOptions onMissing(WriteRequestDeletes.OnMissingEnum onMissing) {
        this.onMissing = onMissing;
        return this;
    }

    public WriteRequestDeletes.OnMissingEnum getOnMissing() {
        return onMissing;
    }

    /**
     * Sets the store to send this request to, instead of the store in the client configuration.
     */
    public ClientDeleteTuplesOptions storeId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }
}
