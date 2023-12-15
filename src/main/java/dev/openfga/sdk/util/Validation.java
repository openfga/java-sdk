/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 0.1
 * Contact: community@openfga.dev
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

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
