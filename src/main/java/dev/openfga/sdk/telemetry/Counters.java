package dev.openfga.sdk.telemetry;

/**
 * The Counters class represents telemetry counters used in the OpenFGA SDK.
 */
public class Counters {
    /**
     * The CREDENTIALS_REQUEST counter represents the number of times an access token is requested.
     */
    public static final Counter CREDENTIALS_REQUEST = new Counter(
            "fga-client.credentials.request", "milliseconds", "The number of times an access token is requested.");
}
