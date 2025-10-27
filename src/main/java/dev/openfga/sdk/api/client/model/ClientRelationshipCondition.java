package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.model.RelationshipCondition;

public class ClientRelationshipCondition {
    private String name;
    private Object context;

    public ClientRelationshipCondition name(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClientRelationshipCondition context(Object context) {
        this.context = context;
        return this;
    }

    public Object getContext() {
        return context;
    }

    public RelationshipCondition asRelationshipCondition() {
        return new RelationshipCondition().name(name).context(context);
    }
}
