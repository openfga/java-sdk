package dev.openfga.sdk.api.auth;

import static dev.openfga.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.api.client.ClientCredentials;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

public class Oauth2Client {
    private HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
    private AuthToken authToken = new AuthToken();
    private AuthRequestBody authRequest;
    private String apiTokenIssuer;

    /// <summary>
    /// Initializes a new instance of the <see cref="OAuth2Client" /> class
    /// </summary>
    /// <param name="credentialsConfig"></param>
    /// <param name="httpClient"></param>
    /// <exception cref="NullReferenceException"></exception>
    public OAuth2Client(ClientCredentials credentialsConfig, HttpClient.Builder httpClientBuilder) {
        if (isNullOrWhitespace(credentialsConfig.getClientId())) {
            throw new FgaInvalidParameterException("OAuth2Client", "config.ClientId");
        }

        if (isNullOrWhitespace(credentialsConfig.getClientSecret())) {
            throw new FgaInvalidParameterException("OAuth2Client", "config.ClientSecret");
        }

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
    private CompletableFuture<Void> exchangeTokenAsync(CancellationToken cancellationToken) {
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
        //                    "ExchangeTokenAsync",
        //                    cancellationToken);
        //
        //            authToken = new AuthToken() {
        //                AccessToken = accessTokenResponse.AccessToken,
        //                ExpiresAt = DateTime.Now + TimeSpan.FromSeconds(accessTokenResponse.ExpiresIn)
        //            };
        return CompletableFuture.supplyAsync(() -> null);
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
