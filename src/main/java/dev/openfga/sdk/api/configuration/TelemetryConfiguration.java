package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import dev.openfga.sdk.telemetry.Counters;
import dev.openfga.sdk.telemetry.Histograms;
import dev.openfga.sdk.telemetry.Metric;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TelemetryConfiguration {
    private Map<Metric, Map<Attribute, Optional<Object>>> metrics = new HashMap<>();

    public TelemetryConfiguration() {
        Map<Attribute, Optional<Object>> defaultAttributes = new HashMap<>();
        defaultAttributes.put(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID, Optional.empty());
        defaultAttributes.put(Attributes.FGA_CLIENT_REQUEST_METHOD, Optional.empty());
        defaultAttributes.put(Attributes.FGA_CLIENT_REQUEST_MODEL_ID, Optional.empty());
        defaultAttributes.put(Attributes.FGA_CLIENT_REQUEST_STORE_ID, Optional.empty());
        defaultAttributes.put(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID, Optional.empty());
        defaultAttributes.put(Attributes.HTTP_HOST, Optional.empty());
        defaultAttributes.put(Attributes.HTTP_REQUEST_METHOD, Optional.empty());
        defaultAttributes.put(Attributes.HTTP_REQUEST_RESEND_COUNT, Optional.empty());
        defaultAttributes.put(Attributes.HTTP_RESPONSE_STATUS_CODE, Optional.empty());
        defaultAttributes.put(Attributes.URL_FULL, Optional.empty());
        defaultAttributes.put(Attributes.URL_SCHEME, Optional.empty());
        defaultAttributes.put(Attributes.USER_AGENT, Optional.empty());

        metrics.put(Counters.CREDENTIALS_REQUEST, defaultAttributes);
        metrics.put(Histograms.QUERY_DURATION, defaultAttributes);
        metrics.put(Histograms.REQUEST_DURATION, defaultAttributes);
    }

    public TelemetryConfiguration metrics(Map<Metric, Map<Attribute, Optional<Object>>> metrics) {
        this.metrics = metrics;
        return this;
    }

    public Map<Metric, Map<Attribute, Optional<Object>>> metrics() {
        return metrics;
    }
}
