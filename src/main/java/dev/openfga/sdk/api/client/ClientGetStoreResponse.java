/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 0.1
 * Contact: community@openfga.dev
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package dev.openfga.sdk.api.client;

import dev.openfga.sdk.api.model.GetStoreResponse;
import java.util.List;
import java.util.Map;

public class ClientGetStoreResponse extends GetStoreResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String rawResponse;

    public ClientGetStoreResponse(ApiResponse<GetStoreResponse> apiResponse) {
        this.statusCode = apiResponse.getStatusCode();
        this.headers = apiResponse.getHeaders();
        this.rawResponse = apiResponse.getRawResponse();
        GetStoreResponse response = apiResponse.getData();
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