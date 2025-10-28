package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.model.CheckError;

public class ClientBatchCheckSingleResponse {
    private final boolean allowed;
    private final ClientBatchCheckItem request;
    private final String correlationId;
    private final CheckError error;

    public ClientBatchCheckSingleResponse(
            boolean allowed, ClientBatchCheckItem request, String correlationId, CheckError error) {
        this.allowed = allowed;
        this.request = request;
        this.correlationId = correlationId;
        this.error = error;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public ClientBatchCheckItem getRequest() {
        return request;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public CheckError getError() {
        return error;
    }
}
