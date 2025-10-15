package dev.openfga.sdk.api.configuration;

import java.util.Map;

public class ClientListStoresOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private Integer pageSize;
    private String continuationToken;
    private String name;

    public ClientListStoresOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientListStoresOptions pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public ClientListStoresOptions continuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
        return this;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public ClientListStoresOptions name(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }
}
