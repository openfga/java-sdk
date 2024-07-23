package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetricTest {

    @Test
    void testMetricConstructor() {
        // Arrange
        String name = "testMetric";
        String description = "A metric for testing";

        // Act
        Metric metric = new Metric(name, description);

        // Assert
        assertEquals(name, metric.getName(), "The name should match the one provided in the constructor.");
        assertEquals(
                description,
                metric.getDescription(),
                "The description should match the one provided in the constructor.");
    }
}
