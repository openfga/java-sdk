package dev.openfga.sdk.api.client.model;

import java.util.List;

public class ClientListRelationsRequest {
    private String user;
    private String _object;
    private List<String> relations;
    private List<ClientTupleKey> contextualTupleKeys;
    private Object context;

    public ClientListRelationsRequest user(String user) {
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

    public ClientListRelationsRequest _object(String _object) {
        this._object = _object;
        return this;
    }

    public String getObject() {
        return _object;
    }

    public ClientListRelationsRequest relations(List<String> relations) {
        this.relations = relations;
        return this;
    }

    /**
     * Get relations
     * @return relations
     **/
    public List<String> getRelations() {
        return relations;
    }

    public ClientListRelationsRequest contextualTupleKeys(List<ClientTupleKey> contextualTupleKeys) {
        this.contextualTupleKeys = contextualTupleKeys;
        return this;
    }

    public List<ClientTupleKey> getContextualTupleKeys() {
        return contextualTupleKeys;
    }

    public ClientListRelationsRequest context(Object context) {
        this.context = context;
        return this;
    }

    /**
     * Get context
     * @return context
     **/
    public Object getContext() {
        return context;
    }
}
