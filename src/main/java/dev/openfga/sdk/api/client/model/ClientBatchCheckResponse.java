package dev.openfga.sdk.api.client.model;

import java.util.List;

public class ClientBatchCheckResponse {
    private final List<ClientBatchCheckSingleResponse> result;

    public ClientBatchCheckResponse(List<ClientBatchCheckSingleResponse> result) {
        this.result = result;
    }

    public List<ClientBatchCheckSingleResponse> getResult() {
        return result;
    }
}
