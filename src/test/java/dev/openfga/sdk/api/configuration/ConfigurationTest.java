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

package dev.openfga.sdk.api.configuration;

import static org.junit.jupiter.api.Assertions.*;

import dev.openfga.sdk.errors.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConfigurationTest {
    private static final String DEFAULT_API_URL = "http://localhost:8080";
    private static final String DEFAULT_USER_AGENT = "openfga-sdk java/0.8.3";
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Map<String, String> DEFAULT_HEADERS = Map.of();

    @Test
    void apiUrl_nullDefaults() throws FgaInvalidParameterException {
        // Given
        String apiUrl = null;
        var config = new Configuration().apiUrl(apiUrl);

        // When
        config.assertValid();

        // Then
        assertEquals("http://localhost:8080", config.getApiUrl());
    }

    @Test
    void apiUrl_emptyStringDefaults() throws FgaInvalidParameterException {
        // Given
        String apiUrl = "";
        var config = new Configuration().apiUrl(apiUrl);

        // When
        config.assertValid();

        // Then
        assertEquals("http://localhost:8080", config.getApiUrl());
    }

    @Test
    void apiUrl_whitespaceStringDefaults() throws FgaInvalidParameterException {
        // Given
        String apiUrl = " \t\r\n";
        var config = new Configuration().apiUrl(apiUrl);

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
            var config = new Configuration().apiUrl(apiUrl);
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
            var config = new Configuration().apiUrl(apiUrl);
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
            var config = new Configuration().apiUrl(apiUrl);
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
            var config = new Configuration().apiUrl(apiUrl);
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
        // the defaults of Configuration can be a surprising and breaking change for consumers.

        // Then
        assertEquals(DEFAULT_API_URL, config.getApiUrl());
        assertEquals(DEFAULT_USER_AGENT, config.getUserAgent());
        assertEquals(DEFAULT_READ_TIMEOUT, config.getReadTimeout());
        assertEquals(DEFAULT_CONNECT_TIMEOUT, config.getConnectTimeout());
        assertEquals(DEFAULT_HEADERS, config.getDefaultHeaders());
    }

    @Test
    void override_addHeader() {
        // Given
        var originalHeaders = Map.of("Original-Header", "from original");
        var original = new Configuration().defaultHeaders(originalHeaders);

        var overrideHeaders = Map.of("Override-Header", "from override");
        var override = new ConfigurationOverride().additionalHeaders(overrideHeaders);

        // When
        Configuration result = original.override(override);

        // Then
        assertEquals("from original", result.getDefaultHeaders().get("Original-Header"));
        assertEquals("from override", result.getDefaultHeaders().get("Override-Header"));
        assertEquals(
                2,
                result.getDefaultHeaders().size(),
                "Resulting configuration should have one header from the original configuration and one header from the override.");
        assertEquals(1, originalHeaders.size(), "Original headers should not be modified.");
        assertEquals(1, overrideHeaders.size(), "Override headers should not be modified.");
    }

    @Test
    void override_overwriteHeader() {
        // Given
        var originalHeaders = Map.of("Header-To-Overwrite", "from original");
        var original = new Configuration().defaultHeaders(originalHeaders);

        var overrideHeaders = Map.of("Header-To-Overwrite", "from override");
        var override = new ConfigurationOverride().additionalHeaders(overrideHeaders);

        // When
        Configuration result = original.override(override);

        // Then
        assertEquals("from override", result.getDefaultHeaders().get("Header-To-Overwrite"));

        assertEquals(
                "from original",
                originalHeaders.get("Header-To-Overwrite"),
                "Original headers should not be modified.");
        assertEquals(1, result.getDefaultHeaders().size(), "Original headers should not be modified.");
        assertEquals(1, originalHeaders.size(), "Original headers should not be modified.");
        assertEquals(1, overrideHeaders.size(), "Override headers should not be modified.");
    }

    @Test
    void override_unsetHeader() {
        // Given
        var originalHeaders = Map.of("Header-To-Unset", "from original");
        var original = new Configuration().defaultHeaders(originalHeaders);

        Map<String, String> overrideHeaders = new HashMap<>();
        overrideHeaders.put("Header-To-Unset", null);
        var override = new ConfigurationOverride().additionalHeaders(overrideHeaders);

        // When
        Configuration result = original.override(override);

        // Then
        assertEquals(0, result.getDefaultHeaders().size());

        assertEquals(
                "from original", originalHeaders.get("Header-To-Unset"), "Original headers should not be modified.");
        assertEquals(1, originalHeaders.size(), "Original headers should not be modified.");
        assertEquals(1, overrideHeaders.size(), "Override headers should not be modified.");
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
