package dev.openfga.api.client;

import static org.junit.jupiter.api.Assertions.*;

import dev.openfga.errors.*;
import org.junit.jupiter.api.Test;

class ConfigurationTest {
    @Test
    void apiUrl_nullDefaults() throws FgaInvalidParameterException {
        // Given
        String apiUrl = null;
        var config = new Configuration(apiUrl);

        // When
        config.assertValid();

        // Then
        assertEquals("http://localhost:8080", config.getApiUrl());
    }

    @Test
    void apiUrl_emptyStringDefaults() throws FgaInvalidParameterException {
        // Given
        String apiUrl = "";
        var config = new Configuration(apiUrl);

        // When
        config.assertValid();

        // Then
        assertEquals("http://localhost:8080", config.getApiUrl());
    }

    @Test
    void apiUrl_whitespaceStringDefaults() throws FgaInvalidParameterException {
        // Given
        String apiUrl = " \t\r\n";
        var config = new Configuration(apiUrl);

        // When
        config.assertValid();

        // Then
        assertEquals("http://localhost:8080", config.getApiUrl());
    }

    @Test
    void apiUrl_stringNoProtocolFails() {
        String apiUrl = "localhost:8080";
        FgaInvalidParameterException e = assertThrows(FgaInvalidParameterException.class, () -> {
            var config = new Configuration(apiUrl);
            config.assertValid();
        });
        assertEquals("Required parameter apiUrl was invalid when calling Configuration.", e.getMessage());
    }

    @Test
    void apiUrl_stringInvalidProtocolFails() {
        String apiUrl = "zzz://localhost:8080";
        FgaInvalidParameterException e = assertThrows(FgaInvalidParameterException.class, () -> {
            var config = new Configuration(apiUrl);
            config.assertValid();
        });
        assertEquals("Required parameter apiUrl was invalid when calling Configuration.", e.getMessage());
    }

    @Test
    void apiUrl_stringNoHostFails() {
        String apiUrl = "http://";
        FgaInvalidParameterException e = assertThrows(FgaInvalidParameterException.class, () -> {
            var config = new Configuration(apiUrl);
            config.assertValid();
        });
        assertEquals("Required parameter apiUrl was invalid when calling Configuration.", e.getMessage());
    }

    @Test
    void apiUrl_stringBadPortFails() {
        String apiUrl = "http://localshost:abcd";
        FgaInvalidParameterException e = assertThrows(FgaInvalidParameterException.class, () -> {
            var config = new Configuration(apiUrl);
            config.assertValid();
        });
        assertEquals("Required parameter apiUrl was invalid when calling Configuration.", e.getMessage());
    }
}
