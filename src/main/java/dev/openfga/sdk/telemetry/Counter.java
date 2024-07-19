package dev.openfga.sdk.telemetry;

/**
 * Represents a counter used for telemetry purposes.
 */
public class Counter {
    private final String name;
    private final String unit;
    private final String description;

    /**
     * Constructs a new Counter with the specified name, unit, and description.
     *
     * @param name        the name of the counter
     * @param unit        the unit of measurement for the counter
     * @param description the description of the counter
     */
    public Counter(String name, String unit, String description) {
        this.name = name;
        this.unit = unit;
        this.description = description;
    }

    /**
     * Returns the name of the counter.
     *
     * @return the name of the counter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the unit of measurement for the counter.
     *
     * @return the unit of measurement for the counter
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns the description of the counter.
     *
     * @return the description of the counter
     */
    public String getDescription() {
        return description;
    }
}
