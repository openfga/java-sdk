package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CountersTest {

    @Test
    void testCredentialsRequestCounter() {
        // Arrange
        String expectedName = "fga-client.credentials.request";
        String expectedDescription =
                "The total number of times new access tokens have been requested using ClientCredentials.";

        // Act
        Counter counter = Counters.CREDENTIALS_REQUEST;

        // Assert
        assertEquals(expectedName, counter.getName(), "The name should match the expected value.");
        assertEquals(expectedDescription, counter.getDescription(), "The description should match the expected value.");
    }
}
