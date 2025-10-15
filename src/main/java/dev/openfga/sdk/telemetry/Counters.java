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

    private Counters() {} // Instantiation prevented.
}
