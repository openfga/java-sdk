package dev.openfga.sdk.telemetry;

/**
 * The Counters class represents telemetry counters used in the OpenFGA SDK.
 */
public class Counters {
    /**
     * The CREDENTIALS_REQUEST counter represents the number of times an access token is requested.
     */
    public static final Counter CREDENTIALS_REQUEST = new Counter(
            "fga-client.credentials.request",
            "The total number of times new access tokens have been requested using ClientCredentials.");

    /**
     * The REQUEST_COUNT counter represents the total number of HTTP requests made by the SDK.
     * This counter is emitted once per underlying HTTP request.
     * Note: This counter is disabled by default and must be explicitly enabled in TelemetryConfiguration.
     */
    public static final Counter REQUEST_COUNT =
            new Counter("fga-client.request.count", "The total number of HTTP requests made to the FGA server.");

    private Counters() {} // Instantiation prevented.
}
