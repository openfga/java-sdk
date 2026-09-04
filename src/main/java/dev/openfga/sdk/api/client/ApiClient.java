package dev.openfga.sdk.api.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.auth.OAuth2Client;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.configuration.CredentialsMethod;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.util.StringUtil;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Configuration and utility class for API clients.
 *
 * <p>This class can be constructed and modified, then used to instantiate the
 * various API classes. The API classes use the settings in this class to
 * configure themselves, but otherwise do not store a link to this class.</p>
 *
 * <p>This class is mutable and not synchronized, so it is not thread-safe.
 * The API classes generated from this are immutable and thread-safe.</p>
 *
 * <p>The setter methods of this class return the current object to facilitate
 * a fluent style of configuration.</p>
 */
public class ApiClient {

    private HttpClient.Builder builder;
    private HttpClient client;
    private JsonSerializer jsonSerializer;
    private Consumer<HttpRequest.Builder> interceptor;
    private Consumer<HttpResponse<InputStream>> responseInterceptor;
    private Consumer<HttpResponse<String>> asyncResponseInterceptor;
    private final ConcurrentMap<CredentialsCacheKey, OAuth2Client> oAuth2Clients = new ConcurrentHashMap<>();

    /**
     * Create an instance of ApiClient.
     */
    public ApiClient() {
        this.builder = createDefaultHttpClientBuilder();
        this.jsonSerializer = JsonSerializer.createDefault();
        this.client = this.builder.build();
        interceptor = null;
        responseInterceptor = null;
        asyncResponseInterceptor = null;
    }

    /**
     * Create an instance of ApiClient.
     * <p>
     * In other contexts, note that any settings in a {@link Configuration}
     * will take precedence over equivalent settings in the
     * {@link HttpClient.Builder} here.
     *
     * @param builder Http client builder.
     */
    public ApiClient(HttpClient.Builder builder) {
        this.builder = builder;
        this.jsonSerializer = JsonSerializer.createDefault();
        this.client = this.builder.build();
        interceptor = null;
        responseInterceptor = null;
        asyncResponseInterceptor = null;
    }

    /**
     * Create an instance of ApiClient.
     * <p>
     * In other contexts, note that any settings in a {@link Configuration}
     * will take precedence over equivalent settings in the
     * {@link HttpClient.Builder} here.
     *
     * @param builder Http client builder.
     * @param mapper Object mapper.
     * @deprecated Use {@link #ApiClient(HttpClient.Builder, JsonSerializer)}.
     */
    @Deprecated(since = "0.11.0")
    public ApiClient(HttpClient.Builder builder, ObjectMapper mapper) {
        this(builder, new Jackson2JsonSerializer(mapper));
    }

    /**
     * Create an instance of ApiClient.
     *
     * @param builder Http client builder.
     * @param jsonSerializer JSON serializer.
     */
    public ApiClient(HttpClient.Builder builder, JsonSerializer jsonSerializer) {
        this.builder = builder;
        this.jsonSerializer = Objects.requireNonNull(jsonSerializer, "JsonSerializer cannot be null");
        this.client = this.builder.build();
        interceptor = null;
        responseInterceptor = null;
        asyncResponseInterceptor = null;
    }

    public static HttpRequest.Builder requestBuilder(String method, String path, Configuration configuration)
            throws FgaInvalidParameterException {
        return requestBuilder(method, path, HttpRequest.BodyPublishers.noBody(), configuration);
    }

    public static HttpRequest.Builder requestBuilder(
            String method, String path, byte[] body, Configuration configuration) throws FgaInvalidParameterException {
        HttpRequest.Builder builder =
                requestBuilder(method, path, HttpRequest.BodyPublishers.ofByteArray(body), configuration);
        builder.header("content-type", "application/json");
        return builder;
    }

    /**
     * Creates a {@link HttpRequest.Builder} for a {@code x-www-form-urlencoded} request.
     * @param method the HTTP method to be make.
     * @param path the URL path.
     * @param body the request body. It must be URL-encoded.
     * @param configuration the client configuration.
     * @return a configured builder.
     * @throws FgaInvalidParameterException
     */
    public static HttpRequest.Builder formRequestBuilder(
            String method, String path, String body, Configuration configuration) throws FgaInvalidParameterException {
        HttpRequest.Builder builder =
                requestBuilder(method, path, HttpRequest.BodyPublishers.ofString(body), configuration);
        builder.header("content-type", "application/x-www-form-urlencoded");
        return builder;
    }

    private static HttpRequest.Builder requestBuilder(
            String method, String path, HttpRequest.BodyPublisher bodyPublisher, Configuration configuration)
            throws FgaInvalidParameterException {
        // verify the Configuration is valid
        configuration.assertValid();

        HttpRequest.Builder builder = HttpRequest.newBuilder();

        builder.uri(URI.create(configuration.getApiUrl() + path));

        builder.header("accept", "application/json");

        builder.method(method, bodyPublisher);

        Duration readTimeout = configuration.getReadTimeout();
        if (readTimeout != null) {
            builder.timeout(readTimeout);
        }

        return builder;
    }

    /**
     * URL encode a string in the UTF-8 encoding.
     *
     * @param s String to encode.
     * @return URL-encoded representation of the input string.
     * @deprecated in favor of {@link StringUtil#urlEncode(String)}
     */
    @Deprecated(forRemoval = true, since = "0.8.2")
    public static String urlEncode(String s) {
        return URLEncoder.encode(s, UTF_8).replaceAll("\\+", "%20");
    }

    protected String getDefaultBaseUri() {
        return "http://localhost";
    }

    protected HttpClient.Builder createDefaultHttpClientBuilder() {
        return HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1);
    }

    /**
     * Set a custom {@link HttpClient.Builder} object to use when creating the
     * {@link HttpClient} that is used by the API client.
     * <p>
     * In other contexts, note that any settings in a {@link Configuration}
     * will take precedence over equivalent settings in the
     * {@link HttpClient.Builder} here.
     *
     * @param builder Custom client builder.
     * @return This object.
     */
    public ApiClient setHttpClientBuilder(HttpClient.Builder builder) {
        this.builder = builder;
        this.client = this.builder.build();
        return this;
    }

    /**
     * Get an {@link HttpClient} based on the current {@link HttpClient.Builder}.
     *
     * <p>The returned object is immutable and thread-safe.</p>
     *
     * @return The HTTP client.
     */
    public HttpClient getHttpClient() {
        return client;
    }

    /**
     * Get the current {@link HttpClient.Builder}.
     *
     * <p>The returned object is immutable and thread-safe.</p>
     *
     * @return The HTTP client.
     */
    public HttpClient.Builder getHttpClientBuilder() {
        return builder;
    }

    /**
     * Set a custom {@link ObjectMapper} for request and response bodies.
     *
     * @param mapper Custom object mapper.
     * @return This object.
     * @deprecated Use {@link #setJsonSerializer(JsonSerializer)}.
     */
    @Deprecated(since = "0.11.0")
    public ApiClient setObjectMapper(ObjectMapper mapper) {
        return setJsonSerializer(new Jackson2JsonSerializer(mapper));
    }

    /**
     * Get the current Jackson 2 object mapper.
     *
     * @return Current Jackson 2 object mapper.
     * @throws UnsupportedOperationException if the active serializer does not use Jackson 2.
     * @deprecated Use {@link #getJsonSerializer()}.
     */
    @Deprecated(since = "0.11.0")
    public ObjectMapper getObjectMapper() {
        if (jsonSerializer instanceof Jackson2JsonSerializer) {
            return ((Jackson2JsonSerializer) jsonSerializer).getObjectMapper();
        }
        throw new UnsupportedOperationException("The active JSON serializer does not use Jackson 2");
    }

    /** Set the serializer for request and response bodies. */
    public ApiClient setJsonSerializer(JsonSerializer jsonSerializer) {
        this.jsonSerializer = Objects.requireNonNull(jsonSerializer, "JsonSerializer cannot be null");
        return this;
    }

    /** Get the serializer for request and response bodies. */
    public JsonSerializer getJsonSerializer() {
        return jsonSerializer;
    }

    /**
     * Set a custom request interceptor.
     *
     * <p>A request interceptor is a mechanism for altering each request before it
     * is sent. After the request has been fully configured but not yet built, the
     * request builder is passed into this function for further modification,
     * after which it is sent out.</p>
     *
     * <p>This is useful for altering the requests in a custom manner, such as
     * adding headers. It could also be used for logging and monitoring.</p>
     *
     * @param interceptor A function invoked before creating each request. A value
     *                    of null resets the interceptor to a no-op.
     * @return This object.
     */
    public ApiClient setRequestInterceptor(Consumer<HttpRequest.Builder> interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    /**
     * Add a custom request interceptor. This interceptor will be run after any
     * other interceptor(s) already in place.
     *
     * <p>For details on request interceptors, see {@link ApiClient#setRequestInterceptor(Consumer)}</p>
     *
     * @param interceptor A function invoked before creating each request. A value
     *                    of null resets the interceptor to a no-op.
     */
    public void addRequestInterceptor(Consumer<HttpRequest.Builder> interceptor) {
        this.interceptor = this.interceptor != null ? this.interceptor.andThen(interceptor) : interceptor;
    }

    /**
     * Get the custom interceptor.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpRequest.Builder> getRequestInterceptor() {
        return interceptor;
    }

    /**
     * Set a custom response interceptor.
     *
     * <p>This is useful for logging, monitoring or extraction of header variables</p>
     *
     * @param interceptor A function invoked before creating each request. A value
     *                    of null resets the interceptor to a no-op.
     * @return This object.
     */
    public ApiClient setResponseInterceptor(Consumer<HttpResponse<InputStream>> interceptor) {
        this.responseInterceptor = interceptor;
        return this;
    }

    /**
     * Get the custom response interceptor.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpResponse<InputStream>> getResponseInterceptor() {
        return responseInterceptor;
    }

    /**
     * Set a custom async response interceptor. Use this interceptor when asyncNative is set to 'true'.
     *
     * <p>This is useful for logging, monitoring or extraction of header variables</p>
     *
     * @param interceptor A function invoked before creating each request. A value
     *                    of null resets the interceptor to a no-op.
     * @return This object.
     */
    public ApiClient setAsyncResponseInterceptor(Consumer<HttpResponse<String>> interceptor) {
        this.asyncResponseInterceptor = interceptor;
        return this;
    }

    /**
     * Get the custom async response interceptor. Use this interceptor when asyncNative is set to 'true'.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpResponse<String>> getAsyncResponseInterceptor() {
        return asyncResponseInterceptor;
    }

    /**
     * Applies the {@code Authorization: Bearer <token>} header to the request builder based on the
     * supplied configuration's {@link Credentials}. This is the single entry point for attaching
     * auth to outbound requests across the SDK — every request builder should delegate here.
     *
     * <ul>
     *   <li>{@link CredentialsMethod#NONE}: no header is applied.</li>
     *   <li>{@link CredentialsMethod#API_TOKEN}: the static API token from the configuration is used.</li>
     *   <li>{@link CredentialsMethod#CLIENT_CREDENTIALS}: an {@link OAuth2Client} performs the
     *       client-credentials exchange and caches the token on this {@code ApiClient} until expiry.
     *       The client is lazily created from {@code configuration} on first use.</li>
     * </ul>
     *
     * @param requestBuilder the request builder to mutate.
     * @param configuration  the configuration that supplies credentials.
     * @throws ApiException                 if CLIENT_CREDENTIALS token exchange fails.
     * @throws FgaInvalidParameterException if the configuration is invalid when lazily creating
     *                                      an {@link OAuth2Client}.
     */
    public void applyAuthHeader(HttpRequest.Builder requestBuilder, Configuration configuration)
            throws ApiException, FgaInvalidParameterException {

        Credentials credentials = configuration.getCredentials();
        if (credentials == null) {
            return;
        }

        CredentialsMethod method = credentials.getCredentialsMethod();
        if (method == null || method == CredentialsMethod.NONE) {
            return;
        }

        String accessToken;
        switch (method) {
            case API_TOKEN:
                accessToken = credentials.getApiToken().getToken();
                break;
            case CLIENT_CREDENTIALS:
                try {
                    accessToken =
                            ensureOAuth2Client(configuration).getAccessToken().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ApiException(e);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof ApiException) {
                        throw (ApiException) cause;
                    }
                    throw new ApiException(cause != null ? cause : e);
                }
                break;
            default:
                throw new IllegalStateException("Unknown credentials method: " + method);
        }

        requestBuilder.setHeader("Authorization", "Bearer " + accessToken);
    }

    private OAuth2Client ensureOAuth2Client(Configuration configuration) throws FgaInvalidParameterException {
        ClientCredentials cc = configuration.getCredentials().getClientCredentials();
        CredentialsCacheKey key = new CredentialsCacheKey(cc);
        OAuth2Client existing = oAuth2Clients.get(key);
        if (existing != null) {
            return existing;
        }
        OAuth2Client created = new OAuth2Client(configuration, this);
        OAuth2Client prior = oAuth2Clients.putIfAbsent(key, created);
        return prior != null ? prior : created;
    }

    private static final class CredentialsCacheKey {
        private final String clientId;
        private final byte[] clientSecretHash;
        private final String apiTokenIssuer;
        private final String apiAudience;
        private final String scopes;

        CredentialsCacheKey(ClientCredentials cc) {
            this.clientId = cc.getClientId();
            this.clientSecretHash = sha256(cc.getClientSecret());
            this.apiTokenIssuer = cc.getApiTokenIssuer();
            this.apiAudience = cc.getApiAudience();
            this.scopes = cc.getScopes();
        }

        private static byte[] sha256(String value) {
            try {
                return MessageDigest.getInstance("SHA-256").digest(value == null ? new byte[0] : value.getBytes(UTF_8));
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 not available", e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CredentialsCacheKey)) return false;
            CredentialsCacheKey that = (CredentialsCacheKey) o;
            return Objects.equals(clientId, that.clientId)
                    && Arrays.equals(clientSecretHash, that.clientSecretHash)
                    && Objects.equals(apiTokenIssuer, that.apiTokenIssuer)
                    && Objects.equals(apiAudience, that.apiAudience)
                    && Objects.equals(scopes, that.scopes);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(clientId, apiTokenIssuer, apiAudience, scopes);
            result = 31 * result + Arrays.hashCode(clientSecretHash);
            return result;
        }
    }
}
