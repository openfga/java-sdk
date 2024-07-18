package dev.openfga.sdk.telemetry;

/**
 * The Histograms class represents a collection of predefined histograms for telemetry purposes.
 */
public class Histograms {
    /**
     * A histogram for measuring the duration of a request.
     */
    public static final Histogram REQUEST_DURATION = new Histogram(
            "fga-client.request.duration", "milliseconds", "How long it took for a request to be fulfilled.");

    /**
     * A histogram for measuring the duration of a query request.
     */
    public static final Histogram QUERY_DURATION =
            new Histogram("fga-client.query.duration", "milliseconds", "How long it took to perform a query request.");
}
