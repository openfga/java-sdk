package dev.openfga.sdk.api.client.model;

import java.util.List;

public class ClientListObjectsRequest {
    private String user;
    private String relation;
    private String type;
    private List<ClientTupleKey> contextualTupleKeys;
    private Object context;

    public ClientListObjectsRequest user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Get user
     * @return user
     **/
    public String getUser() {
        return user;
    }

    public ClientListObjectsRequest relation(String relation) {
        this.relation = relation;
        return this;
    }

    /**
     * Get relation
     * @return relation
     **/
    public String getRelation() {
        return relation;
    }

    public ClientListObjectsRequest type(String type) {
        this.type = type;
        return this;
    }

    public String getType() {
        return type;
    }

    public ClientListObjectsRequest contextualTupleKeys(List<ClientTupleKey> contextualTupleKeys) {
        this.contextualTupleKeys = contextualTupleKeys;
        return this;
    }

    public List<ClientTupleKey> getContextualTupleKeys() {
        return contextualTupleKeys;
    }

    public ClientListObjectsRequest context(Object context) {
        this.context = context;
        return this;
    }

    public Object getContext() {
        return context;
    }
}
