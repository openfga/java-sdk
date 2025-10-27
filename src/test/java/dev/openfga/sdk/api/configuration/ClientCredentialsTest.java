package dev.openfga.sdk.api.configuration;

import static org.junit.jupiter.api.Assertions.*;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ClientCredentialsTest {
    private static final List<String> INVALID_IDENTIFIERS = Arrays.asList(null, "", "\t\r\n");
    private static final String VALID_CLIENT_ID = "client";
    private static final String VALID_CLIENT_SECRET = "secret";
    private static final String VALID_API_TOKEN_ISSUER = "tokenissuer.fga.example";
    private static final String VALID_API_AUDIENCE = "audience";

    @Test
    public void assertValid_allValid() throws FgaInvalidParameterException {
        // When
        ClientCredentials creds = new ClientCredentials()
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .apiTokenIssuer(VALID_API_TOKEN_ISSUER)
                .apiAudience(VALID_API_AUDIENCE);

        // Then
        assertEquals(VALID_CLIENT_ID, creds.getClientId());
        assertEquals(VALID_CLIENT_SECRET, creds.getClientSecret());
        assertEquals(VALID_API_TOKEN_ISSUER, creds.getApiTokenIssuer());
        assertEquals(VALID_API_AUDIENCE, creds.getApiAudience());
    }

    @Test
    public void assertValid_invalidClientId() {
        INVALID_IDENTIFIERS.stream()
                // Given
                .map(invalid -> new ClientCredentials()
                        .clientId(invalid)
                        .clientSecret(VALID_CLIENT_SECRET)
                        .apiTokenIssuer(VALID_API_TOKEN_ISSUER)
                        .apiAudience(VALID_API_AUDIENCE))
                // When
                .map(creds -> assertThrows(FgaInvalidParameterException.class, creds::assertValid))
                // Then
                .forEach(exception -> assertEquals(
                        "Required parameter clientId was invalid when calling ClientCredentials.",
                        exception.getMessage()));
    }

    @Test
    public void assertValid_invalidClientSecret() {
        INVALID_IDENTIFIERS.stream()
                // Given
                .map(invalid -> new ClientCredentials()
                        .clientId(VALID_CLIENT_ID)
                        .clientSecret(invalid)
                        .apiTokenIssuer(VALID_API_TOKEN_ISSUER)
                        .apiAudience(VALID_API_AUDIENCE))
                // When
                .map(creds -> assertThrows(FgaInvalidParameterException.class, creds::assertValid))
                // Then
                .forEach(exception -> assertEquals(
                        "Required parameter clientSecret was invalid when calling ClientCredentials.",
                        exception.getMessage()));
    }

    @Test
    public void assertValid_invalidApiTokenIssuer() {
        INVALID_IDENTIFIERS.stream()
                // Given
                .map(invalid -> new ClientCredentials()
                        .clientId(VALID_CLIENT_ID)
                        .clientSecret(VALID_CLIENT_SECRET)
                        .apiTokenIssuer(invalid)
                        .apiAudience(VALID_API_AUDIENCE))
                // When
                .map(creds -> assertThrows(FgaInvalidParameterException.class, creds::assertValid))
                // Then
                .forEach(exception -> assertEquals(
                        "Required parameter apiTokenIssuer was invalid when calling ClientCredentials.",
                        exception.getMessage()));
    }
}
