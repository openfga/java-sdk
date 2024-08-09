package dev.openfga.sdk.api.configuration;

import static org.junit.jupiter.api.Assertions.*;

import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import dev.openfga.sdk.telemetry.Counters;
import dev.openfga.sdk.telemetry.Histograms;
import dev.openfga.sdk.telemetry.Metric;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TelemetryConfigurationTest {

    @Test
    void testSetAndGetMetrics() {
        // Arrange
        TelemetryConfiguration telemetryConfiguration = new TelemetryConfiguration();
        Map<Metric, Map<Attribute, Optional<Object>>> metrics = new HashMap<>();
        Metric metric = new Metric("testMetric", "A metric for testing");
        Map<Attribute, Optional<Object>> attributes = new HashMap<>();
        Attribute attribute = new Attribute("testAttribute");
        attributes.put(attribute, Optional.of("testValue"));
        metrics.put(metric, attributes);

        // Act
        telemetryConfiguration.metrics(metrics);

        // Assert
        assertEquals(metrics, telemetryConfiguration.metrics(), "The metrics map should match the one set.");
    }

    @Test
    void testDefaultMetrics() {
        // Arrange
        TelemetryConfiguration telemetryConfiguration = new TelemetryConfiguration();
        Map<Metric, Map<Attribute, Optional<Object>>> metrics = telemetryConfiguration.metrics();

        // Assert
        assertNotNull(metrics, "The metrics map should not be null.");
        assertFalse(metrics.isEmpty(), "The metrics map should not be empty.");
        assertTrue(
                metrics.containsKey(Counters.CREDENTIALS_REQUEST),
                "The metrics map should contain the CREDENTIALS_REQUEST counter.");
        assertTrue(
                metrics.containsKey(Histograms.QUERY_DURATION),
                "The metrics map should contain the QUERY_DURATION histogram.");
        assertTrue(
                metrics.containsKey(Histograms.REQUEST_DURATION),
                "The metrics map should contain the REQUEST_DURATION histogram.");

        Map<Attribute, Optional<Object>> defaultAttributes = metrics.get(Counters.CREDENTIALS_REQUEST);
        assertNotNull(defaultAttributes, "The default attributes map should not be null.");
        assertFalse(defaultAttributes.isEmpty(), "The default attributes map should not be empty.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID),
                "The default attributes map should contain the FGA_CLIENT_REQUEST_CLIENT_ID attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_REQUEST_METHOD),
                "The default attributes map should contain the FGA_CLIENT_REQUEST_METHOD attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_REQUEST_MODEL_ID),
                "The default attributes map should contain the FGA_CLIENT_REQUEST_MODEL_ID attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_REQUEST_STORE_ID),
                "The default attributes map should contain the FGA_CLIENT_REQUEST_STORE_ID attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID),
                "The default attributes map should contain the FGA_CLIENT_RESPONSE_MODEL_ID attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.HTTP_HOST),
                "The default attributes map should contain the HTTP_HOST attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.HTTP_REQUEST_RESEND_COUNT),
                "The default attributes map should contain the HTTP_REQUEST_RESEND_COUNT attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.HTTP_RESPONSE_STATUS_CODE),
                "The default attributes map should contain the HTTP_RESPONSE_STATUS_CODE attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.URL_FULL),
                "The default attributes map should contain the URL_FULL attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.URL_SCHEME),
                "The default attributes map should contain the URL_SCHEME attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.USER_AGENT),
                "The default attributes map should contain the USER_AGENT attribute.");

        defaultAttributes = metrics.get(Histograms.QUERY_DURATION);
        assertNotNull(defaultAttributes, "The default attributes map should not be null.");
        assertFalse(defaultAttributes.isEmpty(), "The default attributes map should not be empty.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID),
                "The default attributes map should contain the FGA_CLIENT_REQUEST_CLIENT_ID attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_REQUEST_METHOD),
                "The default attributes map should contain the FGA_CLIENT_REQUEST_METHOD attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_REQUEST_MODEL_ID),
                "The default attributes map should contain the FGA_CLIENT_REQUEST_MODEL_ID attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_REQUEST_STORE_ID),
                "The default attributes map should contain the FGA_CLIENT_REQUEST_STORE_ID attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID),
                "The default attributes map should contain the FGA_CLIENT_RESPONSE_MODEL_ID attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.HTTP_HOST),
                "The default attributes map should contain the HTTP_HOST attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.HTTP_REQUEST_RESEND_COUNT),
                "The default attributes map should contain the HTTP_REQUEST_RESEND_COUNT attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.HTTP_RESPONSE_STATUS_CODE),
                "The default attributes map should contain the HTTP_RESPONSE_STATUS_CODE attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.URL_FULL),
                "The default attributes map should contain the URL_FULL attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.URL_SCHEME),
                "The default attributes map should contain the URL_SCHEME attribute.");
        assertTrue(
                defaultAttributes.containsKey(Attributes.USER_AGENT),
                "The default attributes map should contain the USER_AGENT attribute.");
    }

    @Test
    void testOverridingDefaultMetrics() {
        // Arrange
        TelemetryConfiguration telemetryConfiguration = new TelemetryConfiguration();
        Map<Metric, Map<Attribute, Optional<Object>>> metrics = telemetryConfiguration.metrics();
        Metric metric = new Metric("testMetric", "A metric for testing");
        Map<Attribute, Optional<Object>> attributes = new HashMap<>();
        Attribute attribute = new Attribute("testAttribute");
        attributes.put(attribute, Optional.of("testValue"));
        metrics.put(metric, attributes);

        // Assert
        assertTrue(metrics.containsKey(metric), "The metrics map should contain the testMetric metric.");
        assertEquals(
                attributes,
                metrics.get(metric),
                "The attributes map for the testMetric metric should match the one set.");
    }
}
