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

public class ClientBatchCheckRequest {
    private List<ClientBatchCheckItem> checks;

    public static ClientBatchCheckRequest ofChecks(List<ClientBatchCheckItem> checks) {
        return new ClientBatchCheckRequest().checks(checks);
    }

    public ClientBatchCheckRequest checks(List<ClientBatchCheckItem> checks) {
        this.checks = checks;
        return this;
    }

    public List<ClientBatchCheckItem> getChecks() {
        return checks;
    }
}
