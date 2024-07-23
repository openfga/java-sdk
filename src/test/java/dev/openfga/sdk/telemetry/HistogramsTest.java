package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HistogramsTest {

    @Test
    void testRequestDurationHistogram() {
        // Arrange
        String expectedName = "fga-client.request.duration";
        String expectedDescription =
                "The total time (in milliseconds) it took for the request to complete, including the time it took to send the request and receive the response.";

        // Act
        Histogram histogram = Histograms.REQUEST_DURATION;

        // Assert
        assertEquals(expectedName, histogram.getName(), "The name should match the expected value.");
        assertEquals("milliseconds", histogram.getUnit(), "The default unit should be 'milliseconds'.");
        assertEquals(
                expectedDescription, histogram.getDescription(), "The description should match the expected value.");
    }

    @Test
    void testQueryDurationHistogram() {
        // Arrange
        String expectedName = "fga-client.query.duration";
        String expectedDescription =
                "The total time it took (in milliseconds) for the FGA server to process and evaluate the request.";

        // Act
        Histogram histogram = Histograms.QUERY_DURATION;

        // Assert
        assertEquals(expectedName, histogram.getName(), "The name should match the expected value.");
        assertEquals("milliseconds", histogram.getUnit(), "The default unit should be 'milliseconds'.");
        assertEquals(
                expectedDescription, histogram.getDescription(), "The description should match the expected value.");
    }
}
