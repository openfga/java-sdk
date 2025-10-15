package dev.openfga.sdk.api.client.model;

import java.time.OffsetDateTime;

public class ClientReadChangesRequest {
    private String type;
    private OffsetDateTime startTime;

    public ClientReadChangesRequest type(String type) {
        this.type = type;
        return this;
    }

    public ClientReadChangesRequest startTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getType() {
        return type;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }
}
