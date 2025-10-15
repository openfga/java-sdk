package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.model.WriteAuthorizationModelResponse;
import java.util.List;
import java.util.Map;

public class ClientWriteAuthorizationModelResponse extends WriteAuthorizationModelResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientWriteAuthorizationModelResponse(ApiResponse<WriteAuthorizationModelResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        WriteAuthorizationModelResponse response = apiResponse.getData();
        this.setAuthorizationModelId(response.getAuthorizationModelId());
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
