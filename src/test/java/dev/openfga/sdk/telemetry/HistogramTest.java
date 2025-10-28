package dev.openfga.sdk.telemetry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HistogramTest {
    @Test
    void shouldCreateHistogramWithUnit() {
        // given
        String name = "testHistogram";
        String unit = "seconds";
        String description = "A histogram for testing";

        // when
        Histogram histogram = new Histogram(name, unit, description);

        // then
        assertThat(histogram.getName()).isEqualTo(name);
        assertThat(histogram.getDescription()).isEqualTo(description);
        assertThat(histogram.getUnit()).isEqualTo(unit);
    }

    @Test
    void shouldCreateHistogramWithDefaultMillisecondsUnit() {
        // given
        String name = "testHistogram";
        String description = "A histogram for testing";

        // when
        Histogram histogram = new Histogram(name, description);

        // then
        assertThat(histogram.getName()).isEqualTo(name);
        assertThat(histogram.getDescription()).isEqualTo(description);
        assertThat(histogram.getUnit()).isEqualTo("milliseconds");
    }
}
