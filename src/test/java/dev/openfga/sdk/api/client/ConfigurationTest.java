package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.*;

import dev.openfga.sdk.errors.*;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ConfigurationTest {
    private static final String DEFAULT_API_URL = "http://localhost:8080";
    private static final String DEFAULT_USER_AGENT = "openfga-sdk java/0.0.1";
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);

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
        // Given
        String apiUrl = "localhost:8080";

        // When
        FgaInvalidParameterException e = assertThrows(FgaInvalidParameterException.class, () -> {
            var config = new Configuration(apiUrl);
            config.assertValid();
        });

        // Then
        assertEquals("Required parameter apiUrl was invalid when calling Configuration.", e.getMessage());
    }

    @Test
    void apiUrl_stringInvalidProtocolFails() {
        // Given
        String apiUrl = "zzz://localhost:8080";

        // When
        FgaInvalidParameterException e = assertThrows(FgaInvalidParameterException.class, () -> {
            var config = new Configuration(apiUrl);
            config.assertValid();
        });

        // Then
        assertEquals("Required parameter apiUrl was invalid when calling Configuration.", e.getMessage());
    }

    @Test
    void apiUrl_stringNoHostFails() {
        // Given
        String apiUrl = "http://";

        // When
        FgaInvalidParameterException e = assertThrows(FgaInvalidParameterException.class, () -> {
            var config = new Configuration(apiUrl);
            config.assertValid();
        });

        // Then
        assertEquals("Required parameter apiUrl was invalid when calling Configuration.", e.getMessage());
    }

    @Test
    void apiUrl_stringBadPortFails() {
        // Given
        String apiUrl = "http://localhost:abcd";

        // When
        FgaInvalidParameterException e = assertThrows(FgaInvalidParameterException.class, () -> {
            var config = new Configuration(apiUrl);
            config.assertValid();
        });

        // Then
        assertEquals("Required parameter apiUrl was invalid when calling Configuration.", e.getMessage());
    }

    @Test
    void defaults() {
        // Given
        Configuration config = new Configuration();

        // NOTE: Failures in this test indicate that default values in Configuration have changed. Changing
        // the defaults of Configuration can be a suprising and breaking change for consumers.

        // Then
        assertEquals(DEFAULT_API_URL, config.getApiUrl());
        assertEquals(DEFAULT_USER_AGENT, config.getUserAgent());
        assertEquals(DEFAULT_READ_TIMEOUT, config.getReadTimeout());
        assertEquals(DEFAULT_CONNECT_TIMEOUT, config.getConnectTimeout());
    }

    @Test
    void override_apiUrl() {
        // Given
        Configuration original = new Configuration();
        ConfigurationOverride configOverride = new ConfigurationOverride().apiUrl("https://override.url");

        // When
        Configuration result = original.override(configOverride);

        // Then
        assertEquals("https://override.url", result.getApiUrl());
        assertEquals(DEFAULT_API_URL, original.getApiUrl(), "The Configuration's default apiUrl should be unmodified.");
    }

    @Test
    void override_userAgent() {
        // Given
        Configuration original = new Configuration();
        ConfigurationOverride configOverride = new ConfigurationOverride().userAgent("override-agent");

        // When
        Configuration result = original.override(configOverride);

        // Then
        assertEquals("override-agent", result.getUserAgent());
        assertEquals(
                DEFAULT_USER_AGENT,
                original.getUserAgent(),
                "The Configuration's default userAgent should be unmodified.");
    }

    @Test
    void override_readTimeout() {
        // Given
        Configuration original = new Configuration();
        ConfigurationOverride configOverride = new ConfigurationOverride().readTimeout(Duration.ofDays(7));

        // When
        Configuration result = original.override(configOverride);

        // Then
        assertEquals(Duration.ofDays(7), result.getReadTimeout());
        assertEquals(
                DEFAULT_READ_TIMEOUT,
                original.getReadTimeout(),
                "The Configuration's default readTimeout should be unmodified.");
    }

    @Test
    void override_connectTimeout() {
        // Given
        Configuration original = new Configuration();
        ConfigurationOverride configOverride = new ConfigurationOverride().connectTimeout(Duration.ofDays(7));

        // When
        Configuration result = original.override(configOverride);

        // Then
        assertEquals(Duration.ofDays(7), result.getConnectTimeout());
        assertEquals(
                DEFAULT_CONNECT_TIMEOUT,
                original.getConnectTimeout(),
                "The Configuration's default connectTimeout should be unmodified.");
    }
}
