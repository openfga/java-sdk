package dev.openfga.sdk.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.client.Configuration;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class OAuth2Client {
    private HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
    private ObjectMapper mapper;
    private AuthToken authToken = new AuthToken();
    private AuthRequestBody authRequest;
    private String apiTokenIssuer;

    /**
     * Initializes a new instance of the <see cref="OAuth2Client" /> class
     *
     * @param credentialsConfig Configuration for the credentials
     * @param httpClientBuilder
     * <exception cref="NullReferenceException"></exception>
     */
    public OAuth2Client(
            ClientCredentials credentialsConfig, HttpClient.Builder httpClientBuilder, ObjectMapper mapper) {
        // TODO: Move to somewhere else. Do I need to make a builder?
        //        if (isNullOrWhitespace(credentialsConfig.getClientId())) {
        //            throw new FgaInvalidParameterException("OAuth2Client", "config.ClientId");
        //        }
        //
        //        if (isNullOrWhitespace(credentialsConfig.getClientSecret())) {
        //            throw new FgaInvalidParameterException("OAuth2Client", "config.ClientSecret");
        //        }

        this.httpClientBuilder = httpClientBuilder;
        this.mapper = mapper;
        this.apiTokenIssuer = credentialsConfig.getApiTokenIssuer();
        this.authRequest = new AuthRequestBody();
        this.authRequest.setClientId(credentialsConfig.getClientId());;
        this.authRequest.setClientSecret(credentialsConfig.getClientSecret());
        this.authRequest.setAudience(credentialsConfig.getApiAudience());
        this.authRequest.setGrantType("client_credentials");
    }

    /// <summary>
    /// Exchange client id and client secret for an access token, and handles token refresh
    /// </summary>
    /// <exception cref="NullReferenceException"></exception>
    /// <exception cref="Exception"></exception>
    private CompletableFuture<Void> exchangeTokenAsync() throws Exception {
        HttpClient client = httpClientBuilder.build();

        String body = mapper.writeValueAsString(authRequest);
        System.out.println(body);

        Configuration config = new Configuration().apiUrl("https://" + apiTokenIssuer);

        HttpRequest request =
                ApiClient.requestBuilder("POST", "/oauth/token", body.getBytes(StandardCharsets.UTF_8), config).build();

        HttpResponse<String> thing =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();

        System.out.println(thing.body());

        return CompletableFuture.supplyAsync(() -> null);
    }

    /// <summary>
    /// Gets the access token, and handles exchanging, rudimentary in memory caching and refreshing it when expired
    /// </summary>
    /// <returns></returns>
    /// <exception cref="InvalidOperationException"></exception>
    public CompletableFuture<String> getAccessTokenAsync() throws Exception {
        if (!authToken.isValid()) {
            exchangeTokenAsync().get();
        }

        return CompletableFuture.supplyAsync(authToken::getAccessToken);
    }
}
