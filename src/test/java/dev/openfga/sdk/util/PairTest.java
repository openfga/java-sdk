package dev.openfga.sdk.util;

import static dev.openfga.sdk.util.StringUtil.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class PairTest {
    @Test
    void shouldCreatePair() {
        // when
        Pair pair = new Pair("name", "value");

        // then
        assertThat(pair.getName()).isEqualTo("name");
        assertThat(pair.getValue()).isEqualTo("value");
    }

    @Test
    void shouldCreatePairWithEmptyNameAndValueWhenNameAndValueAreNull() {
        // when
        Pair pair = new Pair(null, null);

        // then
        assertThat(pair.getName()).isEmpty();
        assertThat(pair.getValue()).isEmpty();
    }

    @Test
    void shouldUrlEncodeNameAndValue() {
        // given
        Pair pair = new Pair("name_with_+_char", "value_with_=_char");

        // when
        String queryStringPair = pair.asQueryStringPair();

        // then
        assertThat(queryStringPair).isEqualTo("name_with_%2B_char=value_with_%3D_char");
    }

    @Test
    void shouldReturnEmptyOptionalWhenNameIsNullOrWhitespace() {
        // when
        Optional<Pair> pair = Pair.of(EMPTY, "value");

        // then
        assertThat(pair).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenValueIsNull() {
        // when
        Optional<Pair> pair = Pair.of("name", null);

        // then
        assertThat(pair).isEmpty();
    }

    @Test
    void shouldReturnOptionalWithPair() {
        // when
        Optional<Pair> pair = Pair.of("name", "value");

        // then
        assertThat(pair).isPresent().hasValueSatisfying(p -> {
            assertThat(p.getName()).isEqualTo("name");
            assertThat(p.getValue()).isEqualTo("value");
        });
    }
}
