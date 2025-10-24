package dev.openfga.sdk.api.client;

import dev.openfga.sdk.api.model.Assertion;
import dev.openfga.sdk.api.model.AssertionTupleKey;
import java.util.List;
import java.util.stream.Collectors;

public class ClientAssertion {
    private String user;
    private String relation;
    private String _object;
    private boolean expectation;

    public ClientAssertion user(String user) {
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

    public ClientAssertion relation(String relation) {
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

    public ClientAssertion _object(String _object) {
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

    public ClientAssertion expectation(boolean expectation) {
        this.expectation = expectation;
        return this;
    }

    public boolean getExpectation() {
        return expectation;
    }

    public Assertion asAssertion() {
        var tupleKey = new AssertionTupleKey().user(user).relation(relation)._object(_object);
        return new Assertion().tupleKey(tupleKey).expectation(expectation);
    }

    public static List<Assertion> asAssertions(List<ClientAssertion> assertions) {
        if (assertions == null || assertions.isEmpty()) {
            return List.of();
        }

        return assertions.stream().map(ClientAssertion::asAssertion).collect(Collectors.toList());
    }
}
