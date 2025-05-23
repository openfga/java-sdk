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

package dev.openfga.sdk.telemetry;

/**
 * The Histograms class represents a collection of predefined histograms for telemetry purposes.
 */
public class Histograms {
    /**
     * A histogram for measuring the total time (in milliseconds) it took for the request to complete, including the time it took to send the request and receive the response.
     */
    public static final Histogram REQUEST_DURATION = new Histogram(
            "fga-client.request.duration",
            "The total time (in milliseconds) it took for the request to complete, including the time it took to send the request and receive the response.");

    /**
     * A histogram for measuring the total time it took (in milliseconds) for the FGA server to process and evaluate the request.
     */
    public static final Histogram QUERY_DURATION = new Histogram(
            "fga-client.query.duration",
            "The total time it took (in milliseconds) for the FGA server to process and evaluate the request.");

    private Histograms() {} // Instantiation prevented.
}
