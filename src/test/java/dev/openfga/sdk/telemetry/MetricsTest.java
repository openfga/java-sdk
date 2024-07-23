package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import dev.openfga.sdk.api.configuration.Configuration;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetricsTest {

    private Metrics metrics;
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = mock(Configuration.class);
        metrics = new Metrics(configuration);
    }

    @Test
    void testConstructorWithConfiguration() {
        // Act
        Metrics metricsWithConfig = new Metrics(configuration);

        // Assert
        assertNotNull(metricsWithConfig.getMeter(), "The Meter object should not be null.");
    }

    @Test
    void testConstructorWithoutConfiguration() {
        // Act
        Metrics metricsWithoutConfig = new Metrics();

        // Assert
        assertNotNull(metricsWithoutConfig.getMeter(), "The Meter object should not be null.");
    }

    @Test
    void testGetMeter() {
        // Act
        Meter meter = metrics.getMeter();

        // Assert
        assertNotNull(meter, "The Meter object should not be null.");
    }

    @Test
    void testGetCounter() {
        // Arrange
        Counter counter = Counters.CREDENTIALS_REQUEST;
        Long value = 10L;
        Map<Attribute, String> attributes = new HashMap<>();

        // Act
        LongCounter longCounter = metrics.getCounter(counter, value, attributes);

        // Assert
        assertNotNull(longCounter, "The LongCounter object should not be null.");
    }

    @Test
    void testGetHistogram() {
        // Arrange
        Histogram histogram = Histograms.REQUEST_DURATION;
        Double value = 100.0;
        Map<Attribute, String> attributes = new HashMap<>();

        // Act
        DoubleHistogram doubleHistogram = metrics.getHistogram(histogram, value, attributes);

        // Assert
        assertNotNull(doubleHistogram, "The DoubleHistogram object should not be null.");
    }

    @Test
    void testCredentialsRequest() {
        // Arrange
        Long value = 5L;
        Map<Attribute, String> attributes = new HashMap<>();

        // Act
        LongCounter longCounter = metrics.credentialsRequest(value, attributes);

        // Assert
        assertNotNull(longCounter, "The LongCounter object should not be null.");
    }

    @Test
    void testRequestDuration() {
        // Arrange
        Double value = 200.0;
        Map<Attribute, String> attributes = new HashMap<>();

        // Act
        DoubleHistogram doubleHistogram = metrics.requestDuration(value, attributes);

        // Assert
        assertNotNull(doubleHistogram, "The DoubleHistogram object should not be null.");
    }

    @Test
    void testQueryDuration() {
        // Arrange
        Double value = 150.0;
        Map<Attribute, String> attributes = new HashMap<>();

        // Act
        DoubleHistogram doubleHistogram = metrics.queryDuration(value, attributes);

        // Assert
        assertNotNull(doubleHistogram, "The DoubleHistogram object should not be null.");
    }
}
