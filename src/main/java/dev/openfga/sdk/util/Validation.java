package dev.openfga.sdk.util;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.errors.FgaInvalidParameterException;

public class Validation {
    private Validation() {} // Instantiation prevented for utility class.

    public static void assertParamExists(Object obj, String name, String context) throws FgaInvalidParameterException {
        if (obj == null || obj instanceof String && isNullOrWhitespace((String) obj)) {
            throw new FgaInvalidParameterException(name, context);
        }
    }
}
