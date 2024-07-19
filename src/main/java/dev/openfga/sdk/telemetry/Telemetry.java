package dev.openfga.sdk.telemetry;

/**
 * The Telemetry class provides access to telemetry-related functionality.
 */
public class Telemetry {
    private Metrics metrics = null;

    /**
     * Returns the Metrics object for collecting telemetry data.
     * If the Metrics object has not been initialized, it will be created.
     */
    public Metrics metrics() {
        if (metrics == null) {
            metrics = new Metrics();
        }

        return metrics;
    }
}
