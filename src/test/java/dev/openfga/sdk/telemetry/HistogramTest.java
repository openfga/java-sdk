package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HistogramTest {

    @Test
    void testHistogramConstructorWithUnit() {
        // Arrange
        String name = "testHistogram";
        String unit = "seconds";
        String description = "A histogram for testing";

        // Act
        Histogram histogram = new Histogram(name, unit, description);

        // Assert
        assertEquals(name, histogram.getName(), "The name should match the one provided in the constructor.");
        assertEquals(unit, histogram.getUnit(), "The unit should match the one provided in the constructor.");
        assertEquals(
                description,
                histogram.getDescription(),
                "The description should match the one provided in the constructor.");
    }

    @Test
    void testHistogramConstructorWithoutUnit() {
        // Arrange
        String name = "testHistogram";
        String description = "A histogram for testing";

        // Act
        Histogram histogram = new Histogram(name, description);

        // Assert
        assertEquals(name, histogram.getName(), "The name should match the one provided in the constructor.");
        assertEquals("milliseconds", histogram.getUnit(), "The default unit should be 'milliseconds'.");
        assertEquals(
                description,
                histogram.getDescription(),
                "The description should match the one provided in the constructor.");
    }
}
