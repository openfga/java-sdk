package dev.openfga.sdk.api.client.model;

import java.util.List;

public class ClientBatchCheckItem {
    private String user;
    private String relation;
    private String _object;
    private List<ClientTupleKey> contextualTuples;
    private Object context;
    private String correlationId;

    public ClientBatchCheckItem user(String user) {
        this.user = user;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ClientBatchCheckItem relation(String relation) {
        this.relation = relation;
        return this;
    }

    public String getRelation() {
        return relation;
    }

    public ClientBatchCheckItem _object(String _object) {
        this._object = _object;
        return this;
    }

    public String getObject() {
        return _object;
    }

    public ClientBatchCheckItem contextualTuples(List<ClientTupleKey> contextualTuples) {
        this.contextualTuples = contextualTuples;
        return this;
    }

    public List<ClientTupleKey> getContextualTuples() {
        return contextualTuples;
    }

    public ClientBatchCheckItem context(Object context) {
        this.context = context;
        return this;
    }

    public Object getContext() {
        return context;
    }

    public ClientBatchCheckItem correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
