package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.model.ExpandResponse;
import java.util.List;
import java.util.Map;

public class ClientExpandResponse extends ExpandResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientExpandResponse(ApiResponse<ExpandResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        ExpandResponse response = apiResponse.getData();
        this.setTree(response.getTree());
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
