package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.WriteRequestWrites;
import java.util.Map;

public class ClientWriteTuplesOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String storeId;
    private WriteRequestWrites.OnDuplicateEnum onDuplicate;

    public ClientWriteTuplesOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientWriteTuplesOptions onDuplicate(WriteRequestWrites.OnDuplicateEnum onDuplicate) {
        this.onDuplicate = onDuplicate;
        return this;
    }

    public WriteRequestWrites.OnDuplicateEnum getOnDuplicate() {
        return onDuplicate;
    }

    /**
     * Sets the store to send this request to, instead of the store in the client configuration.
     */
    public ClientWriteTuplesOptions storeId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }
}
