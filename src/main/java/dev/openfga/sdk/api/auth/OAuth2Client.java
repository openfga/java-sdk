package dev.openfga.sdk.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class OAuth2Client {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final AuthToken authToken = new AuthToken();
    private final CredentialsFlowRequest authRequest;
    private final String apiTokenIssuer;

    /**
     * Initializes a new instance of the {@link OAuth2Client} class
     *
     * @param clientCredentials Credentials that can be used to retrieve an access token
     * @param httpClient Http client
     */
    public OAuth2Client(ClientCredentials clientCredentials, HttpClient httpClient, ObjectMapper mapper) throws FgaInvalidParameterException {
        clientCredentials.assertValid();

        this.httpClient = httpClient;
        this.mapper = mapper;
        this.apiTokenIssuer = clientCredentials.getApiTokenIssuer();
        this.authRequest = new CredentialsFlowRequest();
        this.authRequest.setClientId(clientCredentials.getClientId());
        this.authRequest.setClientSecret(clientCredentials.getClientSecret());
        this.authRequest.setAudience(clientCredentials.getApiAudience());
        this.authRequest.setGrantType("client_credentials");
    }

    /**
     * Gets an access token, handling exchange when necessary. The access token is naively cached in memory until it
     * expires.
     *
     * @return An access token in a {@link CompletableFuture}
     */
    public CompletableFuture<String> getAccessToken() throws FgaInvalidParameterException, ApiException {
        if (!authToken.isValid()) {
            return exchangeToken().thenCompose(response -> {
                authToken.setAccessToken(response.getAccessToken());
                authToken.setExpiresAt(Instant.now().plusSeconds(response.getExpiresInSeconds()));
                return CompletableFuture.completedFuture(authToken.getAccessToken());
            });
        }

        return CompletableFuture.completedFuture(authToken.getAccessToken());
    }

    /**
     * Exchange a client id and client secret for an access token.
     * @return The credentials flow response
     */
    private CompletableFuture<CredentialsFlowResponse> exchangeToken()
            throws ApiException, FgaInvalidParameterException {
        try {
            byte[] body = mapper.writeValueAsBytes(authRequest);

            Configuration config = new Configuration().apiUrl("https://" + apiTokenIssuer);

            HttpRequest request = ApiClient.requestBuilder("POST", "/oauth/token", body, config)
                    .build();

            return httpClient
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenCompose(httpResponse -> {
                        if (httpResponse.statusCode() / 100 != 2) {
                            return CompletableFuture.failedFuture(getApiException("exchangeToken", httpResponse));
                        }
                        try {
                            CredentialsFlowResponse response =
                                    mapper.readValue(httpResponse.body(), CredentialsFlowResponse.class);
                            return CompletableFuture.completedFuture(response);
                        } catch (Exception e) {
                            return CompletableFuture.failedFuture(e);
                        }
                    });
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    private ApiException getApiException(String operationId, HttpResponse<String> response) {
        String message = formatExceptionMessage(operationId, response.statusCode(), response.body());
        return new ApiException(response.statusCode(), message, response.headers(), response.body());
    }

    private String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
    }
}
