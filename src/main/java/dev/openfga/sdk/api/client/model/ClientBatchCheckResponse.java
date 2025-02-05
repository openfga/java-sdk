/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 1.x
 * Contact: community@openfga.dev
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

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
