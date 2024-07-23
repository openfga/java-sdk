package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CounterTest {

    @Test
    void testCounterConstructor() {
        // Arrange
        String name = "testCounter";
        String description = "A counter for testing";

        // Act
        Counter counter = new Counter(name, description);

        // Assert
        assertEquals(name, counter.getName(), "The name should match the one provided in the constructor.");
        assertEquals(
                description,
                counter.getDescription(),
                "The description should match the one provided in the constructor.");
    }
}
