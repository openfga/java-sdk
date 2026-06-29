package dev.openfga.sdk.api.client;

import static org.assertj.core.api.Assertions.assertThat;

import dev.openfga.sdk.api.model.Assertion;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClientAssertionTest {

    @Test
    void asAssertion_convertsAllFields() {
        ClientAssertion client = new ClientAssertion()
                .user("user:anne")
                .relation("viewer")
                ._object("document:budget")
                .expectation(true);

        Assertion result = client.asAssertion();

        assertThat(result.getTupleKey().getUser()).isEqualTo("user:anne");
        assertThat(result.getTupleKey().getRelation()).isEqualTo("viewer");
        assertThat(result.getTupleKey().getObject()).isEqualTo("document:budget");
        assertThat(result.getExpectation()).isTrue();
    }

    @Test
    void asAssertion_falseExpectation() {
        ClientAssertion client = new ClientAssertion()
                .user("user:bob")
                .relation("editor")
                ._object("document:budget")
                .expectation(false);

        assertThat(client.asAssertion().getExpectation()).isFalse();
    }

    @Test
    void asAssertions_withNullList_returnsEmpty() {
        assertThat(ClientAssertion.asAssertions(null)).isEmpty();
    }

    @Test
    void asAssertions_withEmptyList_returnsEmpty() {
        assertThat(ClientAssertion.asAssertions(List.of())).isEmpty();
    }

    @Test
    void asAssertions_convertsEachItem() {
        List<ClientAssertion> input = List.of(
                new ClientAssertion()
                        .user("user:anne")
                        .relation("viewer")
                        ._object("doc:1")
                        .expectation(true),
                new ClientAssertion()
                        .user("user:bob")
                        .relation("editor")
                        ._object("doc:2")
                        .expectation(false));

        List<Assertion> result = ClientAssertion.asAssertions(input);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTupleKey().getUser()).isEqualTo("user:anne");
        assertThat(result.get(0).getExpectation()).isTrue();
        assertThat(result.get(1).getTupleKey().getUser()).isEqualTo("user:bob");
        assertThat(result.get(1).getExpectation()).isFalse();
    }
}
