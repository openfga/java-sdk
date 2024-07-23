package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AttributeTest {

    @Test
    void testGetName() {
        // Arrange
        String attributeName = "testAttribute";
        Attribute attribute = new Attribute(attributeName);

        // Act
        String result = attribute.getName();

        // Assert
        assertEquals(attributeName, result, "The name should match the one provided in the constructor.");
    }
}
