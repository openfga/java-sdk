package dev.openfga.sdk.api.configuration;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;
import static dev.openfga.sdk.util.Validation.assertParamExists;

import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configurations for an api client.
 */
public class Configuration implements BaseConfiguration {
    /**
     * @deprecated This constant will be made private in a future release.
     * Default minimum retry delay of 100ms.
     * This value is used as the default base delay for exponential backoff calculations.
     */
    @Deprecated
    public static final Duration DEFAULT_MINIMUM_RETRY_DELAY = FgaConstants.DEFAULT_MIN_WAIT_IN_MS;

    public static final String VERSION = FgaConstants.SDK_VERSION;

    private String apiUrl;
    private Credentials credentials;
    private String userAgent;
    private Duration readTimeout;
    private Duration connectTimeout;
    private int maxRetries;
    private Duration minimumRetryDelay;
    private Map<String, String> defaultHeaders;
    private TelemetryConfiguration telemetryConfiguration;

    public Configuration() {
        this.apiUrl = FgaConstants.DEFAULT_API_URL;
        this.userAgent = FgaConstants.USER_AGENT;
        this.readTimeout = FgaConstants.DEFAULT_REQUEST_TIMEOUT_IN_MS;
        this.connectTimeout = FgaConstants.DEFAULT_CONNECTION_TIMEOUT_IN_MS;
        this.maxRetries = FgaConstants.DEFAULT_MAX_RETRY;
        this.minimumRetryDelay = FgaConstants.DEFAULT_MIN_WAIT_IN_MS;
    }

    /**
     * Assert that the configuration is valid.
     */
    public void assertValid() throws FgaInvalidParameterException {
        // If apiUrl is null/empty/whitespace it will resolve to
        // FgaConstants.DEFAULT_API_URL when getApiUrl is called.
        if (!isNullOrWhitespace(apiUrl)) {
            URI uri;

            try {
                uri = URI.create(apiUrl);
                URL _url = uri.toURL();
            } catch (MalformedURLException | IllegalArgumentException cause) {
                throw new FgaInvalidParameterException("apiUrl", "Configuration", cause);
            }

            assertParamExists(uri.getScheme(), "scheme", "Configuration");
            assertParamExists(uri.getHost(), "hostname", "Configuration");
        }

        if (credentials != null) {
            credentials.assertValid();
        }
    }

    /**
     * Construct a new {@link Configuration} with any non-null values of a {@link ConfigurationOverride} and remaining values from this {@link Configuration}.
     *
     * @param configurationOverride The values to override
     * @return A new {@link Configuration} with values of this Configuration mixed with non-null values of configurationOverride
     */
    public Configuration override(ConfigurationOverride configurationOverride) {
        Configuration result = new Configuration();

        String overrideApiUrl = configurationOverride.getApiUrl();
        result.apiUrl(overrideApiUrl != null ? overrideApiUrl : apiUrl);

        Credentials overrideCredentials = configurationOverride.getCredentials();
        result.credentials(overrideCredentials != null ? overrideCredentials : credentials);

        String overrideUserAgent = configurationOverride.getUserAgent();
        result.userAgent(overrideUserAgent != null ? overrideUserAgent : userAgent);

        Duration overrideReadTimeout = configurationOverride.getReadTimeout();
        result.readTimeout(overrideReadTimeout != null ? overrideReadTimeout : readTimeout);

        Duration overrideConnectTimeout = configurationOverride.getConnectTimeout();
        result.connectTimeout(overrideConnectTimeout != null ? overrideConnectTimeout : connectTimeout);

        Integer overrideMaxRetries = configurationOverride.getMaxRetries();
        result.maxRetries(overrideMaxRetries != null ? overrideMaxRetries : maxRetries);

        Duration overrideMinimumRetryDelay = configurationOverride.getMinimumRetryDelay();
        result.minimumRetryDelay(overrideMinimumRetryDelay != null ? overrideMinimumRetryDelay : minimumRetryDelay);

        Map<String, String> headers = new HashMap<>();
        if (defaultHeaders != null) {
            headers.putAll(defaultHeaders);
        }
        Map<String, String> additionalHeaders = configurationOverride.getAdditionalHeaders();
        if (additionalHeaders != null) {
            additionalHeaders.forEach((header, value) -> {
                if (value == null) {
                    headers.remove(header);
                } else {
                    headers.put(header, value);
                }
            });
        }
        result.defaultHeaders(headers);

        TelemetryConfiguration overrideTelemetryConfiguration = configurationOverride.getTelemetryConfiguration();
        result.telemetryConfiguration =
                overrideTelemetryConfiguration != null ? overrideTelemetryConfiguration : telemetryConfiguration;

        return result;
    }

    /**
     * Set the API URL for the http client.
     *
     * @param apiUrl The URL.
     * @return This object.
     */
    public Configuration apiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        return this;
    }

    /**
     * Get the API URL that was set.
     *
     * @return The url.
     */
    @Override
    public String getApiUrl() {
        if (isNullOrWhitespace(apiUrl)) {
            return FgaConstants.DEFAULT_API_URL;
        }

        return apiUrl;
    }

    /**
     * Set the user agent.
     *
     * <p>Within the context of a single request, a "User-Agent" header from either
     * {@link Configuration#defaultHeaders(Map)} or {@link ConfigurationOverride#additionalHeaders(Map)}
     * will take precedence over this value.</p>
     *
     * @param userAgent The user agent.
     * @return This object.
     */
    public Configuration userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * Get the user agent.
     *
     * @return The user agent.
     */
    @Override
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Set the credentials.
     *
     * @param credentials The credentials.
     * @return This object.
     */
    public Configuration credentials(Credentials credentials) {
        this.credentials = credentials;
        return this;
    }

    /**
     * Get the credentials.
     *
     * @return The credentials.
     */
    public Credentials getCredentials() {
        if (this.credentials == null) {
            return new Credentials();
        }

        return credentials;
    }

    /**
     * Set the read timeout for the http client.
     *
     * <p>This is the value used by default for each request, though it can be
     * overridden on a per-request basis with a request interceptor.</p>
     *
     * @param readTimeout The read timeout used by default by the http client.
     *                    Setting this value to null resets the timeout to an
     *                    effectively infinite value.
     * @return This object.
     */
    public Configuration readTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Get the read timeout that was set.
     *
     * @return The read timeout, or null if no timeout was set. Null represents
     * an infinite wait time.
     */
    @Override
    public Duration getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets the connect timeout (in milliseconds) for the http client.
     *
     * <p> In the case where a new connection needs to be established, if
     * the connection cannot be established within the given {@code
     * duration}, then {@link HttpClient#send(HttpRequest, BodyHandler)
     * HttpClient::send} throws an {@link HttpConnectTimeoutException}, or
     * {@link HttpClient#sendAsync(HttpRequest, BodyHandler)
     * HttpClient::sendAsync} completes exceptionally with an
     * {@code HttpConnectTimeoutException}. If a new connection does not
     * need to be established, for example if a connection can be reused
     * from a previous request, then this timeout duration has no effect.
     *
     * @param connectTimeout connection timeout in milliseconds
     * @return This object.
     */
    public Configuration connectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * Get connection timeout (in milliseconds).
     *
     * @return Timeout in milliseconds
     */
    @Override
    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Configuration maxRetries(int maxRetries) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries must be non-negative");
        }
        if (maxRetries > FgaConstants.RETRY_MAX_ALLOWED_NUMBER) {
            throw new IllegalArgumentException("maxRetries cannot exceed " + FgaConstants.RETRY_MAX_ALLOWED_NUMBER
                    + " (maximum allowable retries)");
        }
        this.maxRetries = maxRetries;
        return this;
    }

    @Override
    public Integer getMaxRetries() {
        return maxRetries;
    }

    /**
     * Sets the minimum delay to wait before retrying a failed request.
     *
     * @param minimumRetryDelay The minimum delay. Must be non-null and non-negative.
     * @return This Configuration instance for method chaining.
     * @throws IllegalArgumentException if minimumRetryDelay is null or negative.
     */
    public Configuration minimumRetryDelay(Duration minimumRetryDelay) {
        if (minimumRetryDelay == null) {
            throw new IllegalArgumentException("minimumRetryDelay cannot be null");
        }
        if (minimumRetryDelay.isNegative()) {
            throw new IllegalArgumentException("minimumRetryDelay cannot be negative");
        }
        this.minimumRetryDelay = minimumRetryDelay;
        return this;
    }

    /**
     * Gets the minimum delay to wait before retrying a failed request.
     *
     * @return The minimum retry delay. Never null.
     */
    @Override
    public Duration getMinimumRetryDelay() {
        return minimumRetryDelay;
    }

    public Configuration defaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public Map<String, String> getDefaultHeaders() {
        if (this.defaultHeaders == null) {
            this.defaultHeaders = Map.of();
        }
        return this.defaultHeaders;
    }

    public TelemetryConfiguration getTelemetryConfiguration() {
        return telemetryConfiguration;
    }

    public Configuration telemetryConfiguration(TelemetryConfiguration telemetryConfiguration) {
        this.telemetryConfiguration = telemetryConfiguration;
        return this;
    }
}
