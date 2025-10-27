package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.model.CreateStoreResponse;
import java.util.List;
import java.util.Map;

public class ClientCreateStoreResponse extends CreateStoreResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientCreateStoreResponse(ApiResponse<CreateStoreResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        CreateStoreResponse response = apiResponse.getData();
        this.setName(response.getName());
        this.setId(response.getId());
        this.setCreatedAt(response.getCreatedAt());
        this.setUpdatedAt(response.getUpdatedAt());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getRawResponse() {
        return rawResponse;
    }
}
