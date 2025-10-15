package dev.openfga.sdk.telemetry;

/**
 * Represents a histogram for telemetry data.
 */
public class Histogram extends Metric {
    private final String unit;

    /**
     * Constructs a Histogram object with the specified name, unit, and description.
     *
     * @param name        the name of the histogram
     * @param unit        the unit of measurement for the histogram
     * @param description the description of the histogram
     */
    public Histogram(String name, String unit, String description) {
        super(name, description);
        this.unit = unit;
    }

    /**
     * Constructs a Histogram object with the specified name and description. The unit of measurement is set to "milliseconds" by default.
     *
     * @param name        the name of the histogram
     * @param description the description of the histogram
     */
    public Histogram(String name, String description) {
        super(name, description);
        this.unit = "milliseconds";
    }

    /**
     * Returns the unit of measurement for the histogram.
     */
    public String getUnit() {
        return unit;
    }
}
