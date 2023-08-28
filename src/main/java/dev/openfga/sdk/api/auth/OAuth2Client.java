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
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final AuthToken authToken = new AuthToken();
    private final AuthRequestBody authRequest;
    private final String apiTokenIssuer;

    /**
     * Initializes a new instance of the <see cref="OAuth2Client" /> class
     *
     * @param credentialsConfig Configuration for the credentials
     * @param httpClient
     * <exception cref="NullReferenceException"></exception>
     */
    public OAuth2Client(ClientCredentials credentialsConfig, HttpClient httpClient, ObjectMapper mapper) {
        // TODO: Move to somewhere else. Do I need to make a builder?
        //        if (isNullOrWhitespace(credentialsConfig.getClientId())) {
        //            throw new FgaInvalidParameterException("OAuth2Client", "config.ClientId");
        //        }
        //
        //        if (isNullOrWhitespace(credentialsConfig.getClientSecret())) {
        //            throw new FgaInvalidParameterException("OAuth2Client", "config.ClientSecret");
        //        }

        this.httpClient = httpClient;
        this.mapper = mapper;
        this.apiTokenIssuer = credentialsConfig.getApiTokenIssuer();
        this.authRequest = new AuthRequestBody();
        this.authRequest.setClientId(credentialsConfig.getClientId());
        ;
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
        String body = mapper.writeValueAsString(authRequest);
        System.out.printf("!!!DEBUG!!! request body: %s\n", body);
        System.out.flush();

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        Configuration config = new Configuration().apiUrl("https://" + apiTokenIssuer);

        HttpRequest request = ApiClient.requestBuilder("POST", "/oauth/token", bodyBytes, config)
                .build();

        HttpResponse<String> response = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .get();

        System.out.printf("!!!DEBUG!!! response body: %s\n", response.body());
        System.out.flush();

        return CompletableFuture.completedFuture(null);
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

        String accessToken = authToken.getAccessToken();

        return CompletableFuture.completedFuture(accessToken);
    }
}
