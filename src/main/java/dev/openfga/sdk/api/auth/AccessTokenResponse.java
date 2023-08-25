package dev.openfga.sdk.api.auth;

/**
 * Credentials Flow Response
 * <p>
 * {@see "https://auth0.com/docs/get-started/authentication-and-authorization-flow/client-credentials-flow"}
 */
class AccessTokenResponse {
    /// <summary>
    /// Time period after which the token will expire (in ms)
    /// </summary>
    private long expiresIn;

    /// <summary>
    /// Token Type
    /// </summary>
    private String TokenType;

    /// <summary>
    /// Access token to use
    /// </summary>
    private String AccessToken;
}
