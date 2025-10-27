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
