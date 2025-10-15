package dev.openfga.sdk.telemetry;

import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.TelemetryConfiguration;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import java.util.HashMap;
import java.util.Map;

/**
 * The Metrics class provides methods for creating and publishing metrics using OpenTelemetry.
 */
public class Metrics {
    private final Meter meter;
    private final Map<String, LongCounter> counters;
    private final Map<String, DoubleHistogram> histograms;
    private final Configuration configuration;

    public Metrics() {
        this(new Configuration());
    }

    public Metrics(Configuration configuration) {
        this.meter = GlobalOpenTelemetry.get().getMeterProvider().get("openfga-sdk");
        this.counters = new HashMap<>();
        this.histograms = new HashMap<>();
        this.configuration = configuration;
        if (this.configuration.getTelemetryConfiguration() == null) {
            this.configuration.telemetryConfiguration(new TelemetryConfiguration());
        }
    }

    /**
     * Returns the Meter associated with this Metrics session.
     *
     * @return The Meter object.
     */
    public Meter getMeter() {
        return meter;
    }

    /**
     * Returns a LongCounter metric instance.
     *
     * @param counter    The Counter enum representing the metric.
     * @param value      The value to be added to the counter.
     * @param attributes A map of attributes associated with the metric.
     *
     * @return The LongCounter metric instance, if the counter was configured. Otherwise, null.
     */
    public LongCounter getCounter(Counter counter, Long value, Map<Attribute, String> attributes) {
        if (configuration.getTelemetryConfiguration().metrics() == null
                || !configuration.getTelemetryConfiguration().metrics().containsKey(counter)) {
            return null;
        }
        if (!counters.containsKey(counter.getName())) {
            counters.put(
                    counter.getName(),
                    meter.counterBuilder(counter.getName())
                            .setDescription(counter.getDescription())
                            .build());
        }

        LongCounter counterInstance = counters.get(counter.getName());

        if (value != null) {
            counterInstance.add(value, Attributes.prepare(attributes, counter, configuration));
        }

        return counterInstance;
    }

    /**
     * Returns a DoubleHistogram metric instance.
     *
     * @param histogram  The Histogram enum representing the metric.
     * @param value      The value to be recorded in the histogram.
     * @param attributes A map of attributes associated with the metric.
     *
     * @return the DoubleHistogram instance, if the histogram was configured. Otherwise, null.
     */
    public DoubleHistogram getHistogram(Histogram histogram, Double value, Map<Attribute, String> attributes) {
        if (configuration.getTelemetryConfiguration().metrics() == null
                || !configuration.getTelemetryConfiguration().metrics().containsKey(histogram)) {
            return null;
        }

        if (!histograms.containsKey(histogram.getName())) {
            histograms.put(
                    histogram.getName(),
                    meter.histogramBuilder(histogram.getName())
                            .setDescription(histogram.getDescription())
                            .setUnit(histogram.getUnit())
                            .build());
        }

        DoubleHistogram histogramInstance = histograms.get(histogram.getName());

        if (value != null) {
            histogramInstance.record(value, Attributes.prepare(attributes, histogram, configuration));
        }

        return histogramInstance;
    }

    /**
     * Returns a LongCounter counter for tracking the number of times an access token is requested through ClientCredentials.
     *
     * @param value      The value to be added to the counter.
     * @param attributes A map of attributes associated with the metric.
     *
     * @return The LongCounter metric instance for credentials request.
     */
    public LongCounter credentialsRequest(Long value, Map<Attribute, String> attributes) {
        return getCounter(Counters.CREDENTIALS_REQUEST, value, attributes);
    }

    /**
     * Returns a DoubleHistogram histogram for measuring the total roundtrip time it took to process a request, including the time it took to send the request and receive the response.
     *
     * @param value      The value to be recorded in the histogram.
     * @param attributes A map of attributes associated with the metric.
     */
    public DoubleHistogram requestDuration(Double value, Map<Attribute, String> attributes) {
        return getHistogram(Histograms.REQUEST_DURATION, value, attributes);
    }

    /**
     * Returns a DoubleHistogram for measuring how long the FGA server took to process and evaluate a request.
     *
     * @param value      The value to be recorded in the histogram.
     * @param attributes A map of attributes associated with the metric.
     */
    public DoubleHistogram queryDuration(Double value, Map<Attribute, String> attributes) {
        return getHistogram(Histograms.QUERY_DURATION, value, attributes);
    }
}
