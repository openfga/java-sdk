package dev.openfga.sdk.telemetry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.TelemetryConfiguration;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetricsTest {

    private Metrics metrics;
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = mock(Configuration.class);
        metrics = new Metrics(configuration);

        when(configuration.getTelemetryConfiguration()).thenReturn(new TelemetryConfiguration());
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

    @Test
    void testMetricsNotSentIfNotConfigured() {
        Map<Attribute, Optional<Object>> attributes = new HashMap<>();
        attributes.put(Attributes.FGA_CLIENT_REQUEST_METHOD, Optional.empty());
        Map<Metric, Map<Attribute, Optional<Object>>> metrics = new HashMap<>();
        metrics.put(Histograms.QUERY_DURATION, attributes);
        TelemetryConfiguration telemetryConfiguration = new TelemetryConfiguration(metrics);

        Configuration config = new Configuration();
        config.telemetryConfiguration(telemetryConfiguration);

        Metrics configuredMetrics = new Metrics(config);

        DoubleHistogram requestDuration =
                configuredMetrics.getHistogram(Histograms.REQUEST_DURATION, 10.0, new HashMap<>());
        assertNull(requestDuration, "Unconfigured histograms should not be sent.");

        DoubleHistogram queryDuration =
                configuredMetrics.getHistogram(Histograms.QUERY_DURATION, 10.0, new HashMap<>());
        assertNotNull(queryDuration, "Configured histograms should be sent.");

        LongCounter credsRequestCounter =
                configuredMetrics.getCounter(Counters.CREDENTIALS_REQUEST, 10L, new HashMap<>());
        assertNull(credsRequestCounter, "Unconfigured counters should not be sent.");
    }

    @Test
    void testCountersNotSentIfNotConfigured() {
        Map<Attribute, Optional<Object>> attributes = new HashMap<>();
        attributes.put(Attributes.FGA_CLIENT_REQUEST_METHOD, Optional.empty());
        Map<Metric, Map<Attribute, Optional<Object>>> metrics = new HashMap<>();
        metrics.put(Counters.CREDENTIALS_REQUEST, attributes);
        TelemetryConfiguration telemetryConfiguration = new TelemetryConfiguration(metrics);

        Configuration config = new Configuration();
        config.telemetryConfiguration(telemetryConfiguration);

        Metrics configuredMetrics = new Metrics(config);

        DoubleHistogram requestDuration =
                configuredMetrics.getHistogram(Histograms.REQUEST_DURATION, 10.0, new HashMap<>());
        assertNull(requestDuration, "Unconfigured histograms should not be sent.");

        DoubleHistogram queryDuration =
                configuredMetrics.getHistogram(Histograms.QUERY_DURATION, 10.0, new HashMap<>());
        assertNull(queryDuration, "Unconfigured histograms should not be sent.");

        LongCounter credsCounter = configuredMetrics.getCounter(Counters.CREDENTIALS_REQUEST, 10L, new HashMap<>());
        assertNotNull(credsCounter, "Configured counter should be sent.");
    }

    @Test
    void testDefaultMetricsEnabled() {
        // Arrange
        Configuration config = new Configuration();

        // Act
        Metrics metrics = new Metrics(config);

        // Assert
        assertNotNull(
                metrics.getCounter(Counters.CREDENTIALS_REQUEST, 10L, new HashMap<>()), "The counter should be sent.");
        assertNotNull(
                metrics.getHistogram(Histograms.QUERY_DURATION, 10.0, new HashMap<>()),
                "The query duration histogram should be sent.");
        assertNotNull(
                metrics.getHistogram(Histograms.REQUEST_DURATION, 10.0, new HashMap<>()),
                "The request duration histogram should be sent.");
    }

    @Test
    void testMetricsWithNullMetricsConfig() {
        // Arrange
        TelemetryConfiguration telemetryConfiguration = new TelemetryConfiguration(null);
        Configuration config = new Configuration();
        config.telemetryConfiguration(telemetryConfiguration);

        // Act
        Metrics metrics = new Metrics(config);

        // Assert
        assertNull(
                metrics.getCounter(Counters.CREDENTIALS_REQUEST, 10L, new HashMap<>()),
                "The counter should not be sent.");
        assertNull(
                metrics.getHistogram(Histograms.QUERY_DURATION, 10.0, new HashMap<>()),
                "The query duration histogram should not be sent.");
        assertNull(
                metrics.getHistogram(Histograms.REQUEST_DURATION, 10.0, new HashMap<>()),
                "The request duration histogram should not be sent.");
    }
}
