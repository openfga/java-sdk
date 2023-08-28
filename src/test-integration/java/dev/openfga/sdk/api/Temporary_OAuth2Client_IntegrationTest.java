package dev.openfga.sdk.api;

import dev.openfga.sdk.api.auth.ClientCredentials;
import dev.openfga.sdk.api.auth.OAuth2Client;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutionException;

public class Temporary_OAuth2Client_IntegrationTest {
    private static final String FGA_SERVER_URL = "https://api.us1.fga.dev";
    private static final String FGA_STORE_ID = "01H8M76TB3P7EWC6T298WTJX2D";
    // private static final String FGA_MODEL_ID = "YOUR_MODEL_ID" // Optionally, you can specify a model id to target, which can improve latency
    private static final String FGA_API_TOKEN_ISSUER = "fga.us.auth0.com";
    private static final String FGA_API_AUDIENCE = "https://api.us1.fga.dev/";
    private static final String FGA_CLIENT_ID = "P99BJ2XKlB1N8NdIPzN5Ew8mBegEz0FJ";
    private static final String FGA_CLIENT_SECRET = "Zuq7CkAZhutbH4KFqqcN5_cpi_mBpyiIaVIzDmLiBkohngFvvbnNx0I1Sgi8X5EZ";

    @Test
    public void auth() throws ExecutionException, InterruptedException {
        ClientCredentials config = new ClientCredentials()
                .apiAudience(FGA_API_AUDIENCE)
                .apiTokenIssuer(FGA_API_TOKEN_ISSUER)
                .clientId(FGA_CLIENT_ID)
                .clientSecret(FGA_CLIENT_SECRET);

        OAuth2Client authClient = new OAuth2Client(config, HttpClient.newBuilder());

        authClient.getAccessTokenAsync().get();
    }
}
