package dev.openfga.sdk.telemetry;

import static org.assertj.core.api.Assertions.assertThat;

import dev.openfga.sdk.api.configuration.Configuration;
import org.junit.jupiter.api.Test;

class TelemetryTest {
    @Test
    void shouldBeASingletonMetricsInitialization() {
        // given
        Telemetry telemetry = new Telemetry(new Configuration());

        // when
        Metrics firstCall = telemetry.metrics();
        Metrics secondCall = telemetry.metrics();

        // then
        assertThat(firstCall).isNotNull().isSameAs(secondCall);
    }
}
