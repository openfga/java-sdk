package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import dev.openfga.sdk.api.configuration.Configuration;
import org.junit.jupiter.api.Test;

class TelemetryTest {

    @Test
    void testMetricsInitialization() {
        // Arrange
        Configuration configuration = mock(Configuration.class);
        Telemetry telemetry = new Telemetry(configuration);

        // Act
        Metrics firstCall = telemetry.metrics();
        Metrics secondCall = telemetry.metrics();

        // Assert
        assertNotNull(firstCall, "The Metrics object should not be null after initialization.");
        assertSame(firstCall, secondCall, "The same Metrics object should be returned on subsequent calls.");
    }
}
