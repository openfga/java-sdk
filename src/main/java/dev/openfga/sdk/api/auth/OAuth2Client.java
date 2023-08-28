package dev.openfga.sdk.api.auth;

import static dev.openfga.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.api.auth.AuthRequestBody;
import dev.openfga.sdk.api.auth.AuthToken;
import dev.openfga.sdk.api.auth.ClientCredentials;
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.api.client.ApiException;
import dev.openfga.sdk.api.client.Configuration;
import dev.openfga.sdk.api.model.CheckRequest;
import dev.openfga.sdk.errors.FgaInvalidParameterException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class OAuth2Client {
    private HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
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
    public OAuth2Client(ClientCredentials credentialsConfig, HttpClient.Builder httpClientBuilder) {
        // TODO: Move to somewhere else. Do I need to make a builder?
//        if (isNullOrWhitespace(credentialsConfig.getClientId())) {
//            throw new FgaInvalidParameterException("OAuth2Client", "config.ClientId");
//        }
//
//        if (isNullOrWhitespace(credentialsConfig.getClientSecret())) {
//            throw new FgaInvalidParameterException("OAuth2Client", "config.ClientSecret");
//        }

        this.httpClientBuilder = httpClientBuilder;
        this.apiTokenIssuer = credentialsConfig.getApiTokenIssuer();
        this.authRequest = new AuthRequestBody();
        this.authRequest.setClientId(credentialsConfig.getClientId());
        this.authRequest.setClientSecret(credentialsConfig.getClientSecret());
        this.authRequest.setAudience(credentialsConfig.getApiAudience());
        this.authRequest.setGrantType("client_credentials");
    }

    /// <summary>
    /// Exchange client id and client secret for an access token, and handles token refresh
    /// </summary>
    /// <exception cref="NullReferenceException"></exception>
    /// <exception cref="Exception"></exception>
    private CompletableFuture<Void> exchangeTokenAsync() {
        HttpClient client = httpClientBuilder.build();


        HttpRequest request = HttpRequest.newBuilder()
                .POST()
                .build();
        client.send(request, handler);
        //            var requestBuilder = new RequestBuilder {
        //                Method = HttpMethod.Post,
        //                        BasePath = $"https://{this._apiTokenIssuer}",
        //                        PathTemplate = "/oauth/token",
        //                        Body = Utils.CreateJsonStringContent(this._authRequest)
        //            };
        //
        //            var accessTokenResponse = await _httpClient.SendRequestAsync<AccessTokenResponse>(
        //                    requestBuilder,
        //                    null,
        //                    "ExchangeTokenAsync");
        //
        //            authToken = new AuthToken() {
        //                AccessToken = accessTokenResponse.AccessToken,
        //                ExpiresAt = DateTime.Now + TimeSpan.FromSeconds(accessTokenResponse.ExpiresIn)
        //            };
        return CompletableFuture.supplyAsync(() -> null);
    }

    private HttpRequest.Builder exchangeTokenRequestBuilder(String storeId, CheckRequest body, Configuration configuration)
            throws ApiException, FgaInvalidParameterException {
        // verify the required parameter 'storeId' is set
        if (storeId == null) {
            throw new ApiException(400, "Missing the required parameter 'storeId' when calling check");
        }
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(400, "Missing the required parameter 'body' when calling check");
        }

        // verify the Configuration is valid
        configuration.assertValid();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        String localVarPath = "/stores/{store_id}/check".replace("{store_id}", ApiClient.urlEncode(storeId.toString()));

        requestBuilder.uri(URI.create(configuration.getApiUrl() + localVarPath));

        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Accept", "application/json");

        try {
            byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(body);
            requestBuilder.method("POST", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
        } catch (IOException e) {
            throw new ApiException(e);
        }
        Duration readTimeout = configuration.getReadTimeout();
        if (readTimeout != null) {
            requestBuilder.timeout(readTimeout);
        }
        if (memberVarInterceptor != null) {
            memberVarInterceptor.accept(requestBuilder);
        }
        return requestBuilder;
    }

    /// <summary>
    /// Gets the access token, and handles exchanging, rudimentary in memory caching and refreshing it when expired
    /// </summary>
    /// <returns></returns>
    /// <exception cref="InvalidOperationException"></exception>
    public CompletableFuture<Void> getAccessTokenAsync() {
        //            // If we already have an access token in memory
        //            if (_authToken.IsValid()) {
        //                return _authToken.AccessToken!;
        //            }
        //
        //            await ExchangeTokenAsync();
        //
        //            return _authToken.AccessToken ?? throw new InvalidOperationException();
        return CompletableFuture.supplyAsync(() -> null);
    }
}
