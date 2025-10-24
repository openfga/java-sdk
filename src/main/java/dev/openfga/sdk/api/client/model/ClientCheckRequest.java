package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.model.CheckRequest;
import dev.openfga.sdk.api.model.CheckRequestTupleKey;
import java.util.List;

public class ClientCheckRequest {
    private String user;
    private String relation;
    private String _object;
    private Object context;
    private List<ClientTupleKey> contextualTuples;

    public CheckRequest asCheckRequest() {
        var checkRequest = new CheckRequest()
                .tupleKey(
                        new CheckRequestTupleKey().user(user).relation(relation)._object(_object))
                .context(context);
        if (contextualTuples != null && !contextualTuples.isEmpty()) {
            checkRequest.contextualTuples(ClientTupleKey.asContextualTupleKeys(contextualTuples));
        }
        return checkRequest;
    }

    public ClientCheckRequest _object(String _object) {
        this._object = _object;
        return this;
    }

    /**
     * Get _object
     * @return _object
     **/
    public String getObject() {
        return _object;
    }

    public ClientCheckRequest relation(String relation) {
        this.relation = relation;
        return this;
    }

    /**
     * Get context
     * @return context
     **/
    public Object getContext() {
        return context;
    }

    public ClientCheckRequest context(Object context) {
        this.context = context;
        return this;
    }

    /**
     * Get relation
     * @return relation
     **/
    public String getRelation() {
        return relation;
    }

    public ClientCheckRequest user(String user) {
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

    public ClientCheckRequest contextualTuples(List<ClientTupleKey> contextualTuples) {
        this.contextualTuples = contextualTuples;
        return this;
    }

    public List<ClientTupleKey> getContextualTuples() {
        return contextualTuples;
    }
}
