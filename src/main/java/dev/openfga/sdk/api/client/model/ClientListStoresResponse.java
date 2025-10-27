package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.model.ListStoresResponse;
import java.util.List;
import java.util.Map;

public class ClientListStoresResponse extends ListStoresResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientListStoresResponse(ApiResponse<ListStoresResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        ListStoresResponse response = apiResponse.getData();
        this.setStores(response.getStores());
        this.setContinuationToken(response.getContinuationToken());
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
