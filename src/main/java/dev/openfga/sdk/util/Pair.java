package dev.openfga.sdk.util;

import static dev.openfga.sdk.util.StringUtil.*;

import java.util.Optional;

/**
 * A convenient class to hold name-value pairs used when creating elements of a query string.
 */
public class Pair {
    private final String name;
    private final String value;

    public Pair(String name, String value) {
        this.name = name != null ? name : EMPTY;
        this.value = value != null ? value : EMPTY;
    }

    /**
     * Get the name of the pair.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of the pair.
     */
    public String getValue() {
        return value;
    }

    /**
     * Return the pair as a query url encoded string pair.
     */
    public String asQueryStringPair() {
        return String.format("%s=%s", urlEncode(name), urlEncode(value));
    }

    /**
     * Return the {@link Optional#empty()} if the name or value is invalid, otherwise return the optional of {@link Pair}.
     */
    public static Optional<Pair> of(String name, Object value) {
        if (isNullOrWhitespace(name) || value == null) {
            return Optional.empty();
        }
        return Optional.of(new Pair(name, value.toString()));
    }
}
