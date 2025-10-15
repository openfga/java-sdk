package dev.openfga.sdk.telemetry;

import dev.openfga.sdk.api.configuration.Configuration;

/**
 * The Telemetry class provides access to telemetry-related functionality.
 */
public class Telemetry {
    private Configuration configuration = null;
    private Metrics metrics = null;

    public Telemetry(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Returns a Metrics singleton for collecting telemetry data.
     * If the Metrics singleton has not previously been initialized, it will be created.
     */
    public Metrics metrics() {
        if (metrics == null) {
            metrics = new Metrics(configuration);
        }

        return metrics;
    }
}
