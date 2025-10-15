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
