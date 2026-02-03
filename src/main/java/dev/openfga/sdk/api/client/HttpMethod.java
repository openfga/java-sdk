package dev.openfga.sdk.api.client;

/**
 * Enumeration of standard HTTP methods supported by the OpenFGA API.
 * This enum provides type safety and prevents invalid HTTP methods from being used.
 *
 * @since 0.8.0
 */
public enum HttpMethod {
    /**
     * HTTP GET method - used for retrieving resources.
     */
    GET,

    /**
     * HTTP POST method - used for creating resources or submitting data.
     */
    POST,

    /**
     * HTTP PUT method - used for updating or replacing resources.
     */
    PUT,

    /**
     * HTTP DELETE method - used for deleting resources.
     */
    DELETE,

    /**
     * HTTP PATCH method - used for partially updating resources.
     */
    PATCH,

    /**
     * HTTP HEAD method - used for retrieving resource metadata without the body.
     */
    HEAD,

    /**
     * HTTP OPTIONS method - used for describing communication options.
     */
    OPTIONS
}
