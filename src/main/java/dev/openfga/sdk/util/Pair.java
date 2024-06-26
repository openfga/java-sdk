/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 1.x
 * Contact: community@openfga.dev
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package dev.openfga.sdk.util;

import static dev.openfga.sdk.api.client.ApiClient.urlEncode;
import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import java.util.Optional;

public class Pair {
    private String name = "";
    private String value = "";

    public Pair(String name, String value) {
        setName(name);
        setValue(value);
    }

    public static Optional<Pair> of(String name, Object value) {
        if (isNullOrWhitespace(name) || value == null) {
            return Optional.empty();
        }
        return Optional.of(new Pair(name, value.toString()));
    }

    public String asQueryStringPair() {
        return urlEncode(name) + "=" + urlEncode(value);
    }

    private void setName(String name) {
        if (!isValidString(name)) {
            return;
        }

        this.name = name;
    }

    private void setValue(String value) {
        if (!isValidString(value)) {
            return;
        }

        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    private boolean isValidString(String arg) {
        if (arg == null) {
            return false;
        }

        return true;
    }
}
