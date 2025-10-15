package dev.openfga.sdk.telemetry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MetricTest {
    @Test
    void shouldCreateMetric() {
        // given
        String name = "testMetric";
        String description = "A metric for testing";

        // when
        Metric metric = new Metric(name, description);

        // then
        assertThat(metric.getName()).isEqualTo(name);
        assertThat(metric.getDescription()).isEqualTo(description);
    }
}
