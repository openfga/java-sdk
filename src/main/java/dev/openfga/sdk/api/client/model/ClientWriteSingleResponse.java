package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.model.TupleKey;

public class ClientWriteSingleResponse {
    private final TupleKey tupleKey;
    private final ClientWriteStatus status;
    private final Exception error;

    public ClientWriteSingleResponse(TupleKey tupleKey, ClientWriteStatus status) {
        this(tupleKey, status, null);
    }

    public ClientWriteSingleResponse(TupleKey tupleKey, ClientWriteStatus status, Exception error) {
        this.tupleKey = tupleKey;
        this.status = status;
        this.error = error;
    }

    public TupleKey getTupleKey() {
        return tupleKey;
    }

    public ClientWriteStatus getStatus() {
        return status;
    }

    public Exception getError() {
        return error;
    }
}
