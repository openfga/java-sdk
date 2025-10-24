package dev.openfga.sdk.api.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.util.StringUtil;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;
import org.openapitools.jackson.nullable.JsonNullableModule;

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
    private ObjectMapper mapper;
    private Consumer<HttpRequest.Builder> interceptor;
    private Consumer<HttpResponse<InputStream>> responseInterceptor;
    private Consumer<HttpResponse<String>> asyncResponseInterceptor;

    /**
     * Create an instance of ApiClient.
     */
    public ApiClient() {
        this.builder = createDefaultHttpClientBuilder();
        this.mapper = createDefaultObjectMapper();
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
        this.mapper = createDefaultObjectMapper();
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
     */
    public ApiClient(HttpClient.Builder builder, ObjectMapper mapper) {
        this.builder = builder;
        this.mapper = mapper;
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

    protected ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new JsonNullableModule());
        return mapper;
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
     * Set a custom {@link ObjectMapper} to serialize and deserialize the request
     * and response bodies.
     *
     * @param mapper Custom object mapper.
     * @return This object.
     */
    public ApiClient setObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    /**
     * Get current {@link ObjectMapper}.
     *
     * @return the current object mapper.
     */
    public ObjectMapper getObjectMapper() {
        return mapper;
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
}
