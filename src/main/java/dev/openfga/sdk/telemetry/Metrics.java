package dev.openfga.sdk.telemetry;

import io.opentelemetry.api.OpenTelemetry;
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

    public Metrics() {
        this.meter = OpenTelemetry.noop().getMeterProvider().get("openfga-sdk/0.5.0");
        this.counters = new HashMap<>();
        this.histograms = new HashMap<>();
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
     * @return The LongCounter metric instance.
     */
    public LongCounter getCounter(Counter counter, Long value, Map<Attribute, String> attributes) {
        if (!counters.containsKey(counter.getName())) {
            counters.put(
                    counter.getName(),
                    meter.counterBuilder(counter.getName())
                            .setDescription(counter.getDescription())
                            .setUnit(counter.getUnit())
                            .build());
        }

        LongCounter counterInstance = counters.get(counter.getName());

        if (value != null) {
            counterInstance.add(value, Attributes.prepare(attributes));
        }

        return counterInstance;
    }

    /**
     * Returns a DoubleHistogram metric instance.
     *
     * @param histogram  The Histogram enum representing the metric.
     * @param value      The value to be recorded in the histogram.
     * @param attributes A map of attributes associated with the metric.
     */
    public DoubleHistogram getHistogram(Histogram histogram, Double value, Map<Attribute, String> attributes) {
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
            histogramInstance.record(value, Attributes.prepare(attributes));
        }

        return histogramInstance;
    }

    /**
     * Returns a LongCounter metric instance, for publishing the number of times an access token is requested.
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
     * Returns a DoubleHistogram metric instance, for publishing the duration of requests.
     *
     * @param value      The value to be recorded in the histogram.
     * @param attributes A map of attributes associated with the metric.
     */
    public DoubleHistogram requestDuration(Double value, Map<Attribute, String> attributes) {
        return getHistogram(Histograms.REQUEST_DURATION, value, attributes);
    }

    /**
     * Returns a DoubleHistogram metric instance, for publishing the duration of queries.
     *
     * @param value      The value to be recorded in the histogram.
     * @param attributes A map of attributes associated with the metric.
     */
    public DoubleHistogram queryDuration(Double value, Map<Attribute, String> attributes) {
        return getHistogram(Histograms.QUERY_DURATION, value, attributes);
    }
}
