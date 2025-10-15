package dev.openfga.sdk.telemetry;

/**
 * Represents an attribute in telemetry data.
 */
public class Attribute {
    private final String name;

    /**
     * Constructs a new Attribute object with the specified name.
     *
     * @param name the name of the attribute
     */
    public Attribute(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the attribute.
     *
     * @return the name of the attribute
     */
    public String getName() {
        return name;
    }
}
