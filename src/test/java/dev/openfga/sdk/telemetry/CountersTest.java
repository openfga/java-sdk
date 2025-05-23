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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CountersTest {
    @Test
    void shouldCreateCredentialsRequestCounter() {
        // given
        String expectedName = "fga-client.credentials.request";
        String expectedDescription =
                "The total number of times new access tokens have been requested using ClientCredentials.";

        // when
        Counter counter = Counters.CREDENTIALS_REQUEST;

        // then
        assertThat(counter.getName()).isEqualTo(expectedName);
        assertThat(counter.getDescription()).isEqualTo(expectedDescription);
    }
}
