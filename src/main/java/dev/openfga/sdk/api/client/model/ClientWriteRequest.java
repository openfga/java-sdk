package dev.openfga.sdk.api.client.model;

import java.util.List;

public class ClientWriteRequest {
    private List<ClientTupleKey> writes;
    private List<ClientTupleKeyWithoutCondition> deletes;

    public static ClientWriteRequest ofWrites(List<ClientTupleKey> writes) {
        return new ClientWriteRequest().writes(writes);
    }

    public ClientWriteRequest writes(List<ClientTupleKey> writes) {
        this.writes = writes;
        return this;
    }

    public List<ClientTupleKey> getWrites() {
        return writes;
    }

    public static ClientWriteRequest ofDeletes(List<ClientTupleKeyWithoutCondition> deletes) {
        return new ClientWriteRequest().deletes(deletes);
    }

    public ClientWriteRequest deletes(List<ClientTupleKeyWithoutCondition> deletes) {
        this.deletes = deletes;
        return this;
    }

    public List<ClientTupleKeyWithoutCondition> getDeletes() {
        return deletes;
    }
}
