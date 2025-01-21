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

package dev.openfga.sdk.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets AuthErrorCode
 */
public enum AuthErrorCode {
    NO_AUTH_ERROR("no_auth_error"),

    AUTH_FAILED_INVALID_SUBJECT("auth_failed_invalid_subject"),

    AUTH_FAILED_INVALID_AUDIENCE("auth_failed_invalid_audience"),

    AUTH_FAILED_INVALID_ISSUER("auth_failed_invalid_issuer"),

    INVALID_CLAIMS("invalid_claims"),

    AUTH_FAILED_INVALID_BEARER_TOKEN("auth_failed_invalid_bearer_token"),

    BEARER_TOKEN_MISSING("bearer_token_missing"),

    UNAUTHENTICATED("unauthenticated"),

    FORBIDDEN("forbidden"),

    UNKNOWN_DEFAULT_OPEN_API("unknown_default_open_api");

    private String value;

    AuthErrorCode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static AuthErrorCode fromValue(String value) {
        for (AuthErrorCode b : AuthErrorCode.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        return UNKNOWN_DEFAULT_OPEN_API;
    }

    /**
     * Convert the instance into URL query string.
     *
     * @param prefix prefix of the query string
     * @return URL query string
     */
    public String toUrlQueryString(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        return String.format("%s=%s", prefix, this.toString());
    }
}
