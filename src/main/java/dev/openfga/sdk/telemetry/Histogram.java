package dev.openfga.sdk.telemetry;

/**
 * Represents a histogram for telemetry data.
 */
public class Histogram {
    private final String name;
    private final String unit;
    private final String description;

    /**
     * Constructs a Histogram object with the specified name, unit, and description.
     *
     * @param name        the name of the histogram
     * @param unit        the unit of measurement for the histogram
     * @param description the description of the histogram
     */
    public Histogram(String name, String unit, String description) {
        this.name = name;
        this.unit = unit;
        this.description = description;
    }

    /**
     * Returns the name of the histogram.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the unit of measurement for the histogram.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns the description of the histogram.
     */
    public String getDescription() {
        return description;
    }
}
