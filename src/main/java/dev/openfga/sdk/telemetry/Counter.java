package dev.openfga.sdk.telemetry;

/**
 * Represents a counter used for telemetry purposes.
 */
public class Counter extends Metric {
    /**
     * Constructs a new Counter with the specified name, unit, and description.
     *
     * @param name        the name of the counter
     * @param description the description of the counter
     */
    public Counter(String name, String description) {
        super(name, description);
    }
}
