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

package dev.openfga.sdk.api.configuration;

import java.util.Map;

public class ClientWriteOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String authorizationModelId;
    private Boolean disableTransactions = false;
    private int transactionChunkSize;

    public ClientWriteOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientWriteOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public ClientWriteOptions disableTransactions(boolean disableTransactions) {
        this.disableTransactions = disableTransactions;
        return this;
    }

    public boolean disableTransactions() {
        return disableTransactions != null && disableTransactions;
    }

    public ClientWriteOptions transactionChunkSize(int transactionChunkSize) {
        this.transactionChunkSize = transactionChunkSize;
        return this;
    }

    public int getTransactionChunkSize() {
        return transactionChunkSize > 0 ? transactionChunkSize : 1;
    }
}
