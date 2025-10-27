package dev.openfga.sdk.telemetry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CounterTest {
    @Test
    void shouldCreateCounter() {
        // given
        String name = "testCounter";
        String description = "A counter for testing";

        // when
        Counter counter = new Counter(name, description);

        // then
        assertThat(counter.getName()).isEqualTo(name);
        assertThat(counter.getDescription()).isEqualTo(description);
    }
}
