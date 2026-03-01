package dev.openfga.sdk.telemetry;

import dev.openfga.sdk.api.configuration.Configuration;

/**
 * The Telemetry class provides access to telemetry-related functionality.
 */
public class Telemetry {
    private final Configuration configuration;
    private volatile Metrics metrics;

    public Telemetry(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Returns a Metrics singleton for collecting telemetry data.
     * If the Metrics singleton has not previously been initialized, it will be created.
     * This method is thread-safe via double-checked locking.
     */
    public Metrics metrics() {
        Metrics result = metrics;
        if (result == null) {
            synchronized (this) {
                result = metrics;
                if (result == null) {
                    result = new Metrics(configuration);
                    metrics = result;
                }
            }
        }
        return result;
    }
}
