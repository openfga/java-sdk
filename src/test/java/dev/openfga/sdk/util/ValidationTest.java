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

import static dev.openfga.sdk.util.Validation.assertParamExists;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import org.junit.jupiter.api.Test;

class ValidationTest {
    @Test
    void shouldDoNotThrowFgaInvalidParameterException() {
        // when
        ThrowingCallable throwingCallable = () -> assertParamExists("some-store-id", "storeId", "batchCheck");

        // then
        assertThatCode(throwingCallable).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowFgaInvalidParameterExceptionWhenObjectIsNull() {
        // when
        ThrowingCallable throwingCallable = () -> assertParamExists(null, "storeId", "batchCheck");

        // then
        assertThatCode(throwingCallable)
                .isInstanceOf(FgaInvalidParameterException.class)
                .hasMessage("Required parameter storeId was invalid when calling batchCheck.");
    }

    @Test
    void shouldThrowFgaInvalidParameterExceptionWhenObjectIsEmpty() {
        // when
        ThrowingCallable throwingCallable = () -> assertParamExists("", "storeId", "batchCheck");

        // then
        assertThatCode(throwingCallable)
                .isInstanceOf(FgaInvalidParameterException.class)
                .hasMessage("Required parameter storeId was invalid when calling batchCheck.");
    }
}
