package dev.openfga.sdk.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpStatusCodeTest {

    @ParameterizedTest
    @CsvSource({"199, false", "200, true", "299, true", "300, false"})
    void isSuccessful_boundaryValues(int status, boolean expected) {
        assertThat(HttpStatusCode.isSuccessful(status)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"499, false", "500, true", "599, true", "600, false"})
    void isServerError_boundaryValues(int status, boolean expected) {
        assertThat(HttpStatusCode.isServerError(status)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "400, false",
        "429, true",
        "500, true",
        "501, false",
        "502, true",
        "503, true",
        "504, true",
        "599, true",
        "600, false"
    })
    void isRetryable(int status, boolean expected) {
        assertThat(HttpStatusCode.isRetryable(status)).isEqualTo(expected);
    }
}
