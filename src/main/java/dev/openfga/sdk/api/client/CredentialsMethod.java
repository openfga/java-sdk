/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 0.1
 * Contact: community@openfga.dev
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package dev.openfga.sdk.api.client;

/**
 *
 */
public enum CredentialsMethod {
    NONE,
    API_BEARER_TOKEN,
    CLIENT_CREDENTIALS;

    private ApiBearerToken apiBearerToken;
    private ClientCredentials clientCredentials;

    public static CredentialsMethod none() {
        return NONE;
    }

    public static CredentialsMethod apiBearerToken(ApiBearerToken apiBearerToken) {
        CredentialsMethod it = API_BEARER_TOKEN;
        it.apiBearerToken = apiBearerToken;
        return it;
    }

    public static CredentialsMethod clientCredentials(ClientCredentials clientCredentials) {
        CredentialsMethod it = CLIENT_CREDENTIALS;
        it.clientCredentials = clientCredentials;
        return it;
    }
}
