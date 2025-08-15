package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import dev.openfga.sdk.telemetry.Counters;
import dev.openfga.sdk.telemetry.Histograms;
import dev.openfga.sdk.telemetry.Metric;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Configures the telemetry settings for the SDK.
 */
public class TelemetryConfiguration {
    private Map<Metric, Map<Attribute, Optional<Object>>> metrics = new HashMap<>();

    private static final Map<Attribute, Optional<Object>> defaultAttributes = Map.ofEntries(
            Map.entry(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, Optional.empty()),
            Map.entry(Attributes.FGA_CLIENT_REQUEST_METHOD, Optional.empty()),
            Map.entry(Attributes.FGA_CLIENT_REQUEST_MODEL_ID, Optional.empty()),
            Map.entry(Attributes.FGA_CLIENT_REQUEST_STORE_ID, Optional.empty()),
            Map.entry(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID, Optional.empty()),
            Map.entry(Attributes.HTTP_HOST, Optional.empty()),
            Map.entry(Attributes.HTTP_REQUEST_METHOD, Optional.empty()),
            Map.entry(Attributes.HTTP_REQUEST_RESEND_COUNT, Optional.empty()),
            Map.entry(Attributes.HTTP_RESPONSE_STATUS_CODE, Optional.empty()),
            Map.entry(Attributes.URL_FULL, Optional.empty()),
            Map.entry(Attributes.URL_SCHEME, Optional.empty()),
            Map.entry(Attributes.USER_AGENT, Optional.empty()));

    /**
     * Constructs a TelemetryConfiguration object with the the metrics and attributes to send by default.
     */
    public TelemetryConfiguration() {
        metrics.put(Counters.CREDENTIALS_REQUEST, defaultAttributes);
        metrics.put(Histograms.QUERY_DURATION, defaultAttributes);
        metrics.put(Histograms.REQUEST_DURATION, defaultAttributes);
    }

    /**
     * Constructs a TelemetryConfiguration object with the specified metrics.
     * @param metrics the metrics to send
     */
    public TelemetryConfiguration(Map<Metric, Map<Attribute, Optional<Object>>> metrics) {
        this.metrics = metrics;
    }

    /**
     * Sets the metrics to send.
     * @param metrics the metrics to send
     * @return this TelemetryConfiguration object
     */
    public TelemetryConfiguration metrics(Map<Metric, Map<Attribute, Optional<Object>>> metrics) {
        this.metrics = metrics;
        return this;
    }

    /**
     * @return the metrics to send.
     */
    public Map<Metric, Map<Attribute, Optional<Object>>> metrics() {
        return metrics;
    }

    /**
     * @return the default attributes to send.
     */
    public static Map<Attribute, Optional<Object>> defaultAttributes() {
        return defaultAttributes;
    }
}
