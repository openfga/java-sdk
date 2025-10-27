package dev.openfga.sdk.api.client.model;

import dev.openfga.sdk.api.model.TupleKeyWithoutCondition;
import dev.openfga.sdk.api.model.WriteRequestDeletes;
import java.util.Collection;
import java.util.stream.Collectors;

public class ClientTupleKeyWithoutCondition {
    private String user;
    private String relation;
    private String _object;

    public TupleKeyWithoutCondition asTupleKeyWithoutCondition() {
        return new TupleKeyWithoutCondition().user(user).relation(relation)._object(_object);
    }

    public static WriteRequestDeletes asWriteRequestDeletes(Collection<ClientTupleKeyWithoutCondition> tupleKeys) {
        return asWriteRequestDeletes(tupleKeys, null);
    }

    public static WriteRequestDeletes asWriteRequestDeletes(
            Collection<ClientTupleKeyWithoutCondition> tupleKeys, WriteRequestDeletes.OnMissingEnum onMissing) {
        WriteRequestDeletes deletes = new WriteRequestDeletes()
                .tupleKeys(tupleKeys.stream()
                        .map(ClientTupleKeyWithoutCondition::asTupleKeyWithoutCondition)
                        .collect(Collectors.toList()));
        if (onMissing != null) {
            deletes.onMissing(onMissing);
        }
        return deletes;
    }

    public ClientTupleKeyWithoutCondition _object(String _object) {
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

    public ClientTupleKeyWithoutCondition relation(String relation) {
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

    public ClientTupleKeyWithoutCondition user(String user) {
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

    /**
     * Adds a condition to the tuple key.
     * @param condition a {@link ClientRelationshipCondition}
     * @return a new {@link  ClientTupleKey} with this {@link ClientTupleKeyWithoutCondition}'s
     *         user, relation, and object, and the passed condition.
     */
    public ClientTupleKey condition(ClientRelationshipCondition condition) {
        return new ClientTupleKey()
                .user(user)
                .relation(relation)
                ._object(_object)
                .condition(condition);
    }
}
