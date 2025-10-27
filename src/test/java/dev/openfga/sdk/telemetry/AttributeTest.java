package dev.openfga.sdk.telemetry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AttributeTest {
    @Test
    void shouldGetName() {
        // given
        String attributeName = "testAttribute";
        Attribute attribute = new Attribute(attributeName);

        // when
        String result = attribute.getName();

        // then
        assertThat(result).isEqualTo(attributeName);
    }
}
