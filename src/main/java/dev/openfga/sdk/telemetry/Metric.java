package dev.openfga.sdk.telemetry;

public class Metric {
    private final String name;
    private final String description;

    /**
     * Constructs a new metric with the specified name and description.
     *
     * @param name        the name of the counter
     * @param description the description of the counter
     */
    public Metric(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the name of the metric.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the metric.
     */
    public String getDescription() {
        return description;
    }
}
