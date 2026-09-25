package dev.openfga.sdk.api.client;

import dev.openfga.sdk.errors.SdkSerializationException;

/**
 * Serializes SDK request and response values without exposing a JSON library.
 * Implementations must support SDK model annotations and wrap JSON processing
 * failures in {@link SdkSerializationException}, preserving the original cause.
 */
public interface JsonSerializer {
    /** Creates the SDK default serializer. */
    static JsonSerializer createDefault() {
        return new Jackson3JsonSerializer();
    }

    /**
     * Encodes a request value as UTF-8 JSON.
     *
     * @param value Request value to encode.
     * @return Encoded JSON bytes.
     * @throws SdkSerializationException if the value cannot be encoded.
     */
    byte[] writeValueAsBytes(Object value) throws SdkSerializationException;

    /**
     * Decodes a UTF-8 JSON response into a non-generic type.
     *
     * @param source JSON response bytes.
     * @param type Response class.
     * @return Decoded response.
     * @throws SdkSerializationException if the JSON is invalid or cannot map to the type.
     */
    <T> T readValue(byte[] source, Class<T> type) throws SdkSerializationException;

    /**
     * Decodes JSON text into a non-generic response type.
     *
     * @param source JSON response text.
     * @param type Response class.
     * @return Decoded response.
     * @throws SdkSerializationException if the JSON is invalid or cannot map to the type.
     */
    <T> T readValue(String source, Class<T> type) throws SdkSerializationException;

    /**
     * Decodes a UTF-8 JSON response while preserving generic type arguments.
     *
     * @param source JSON response bytes.
     * @param type Response type, including nested type arguments.
     * @return Decoded response.
     * @throws SdkSerializationException if the JSON is invalid or cannot map to the type.
     */
    <T> T readValue(byte[] source, SdkTypeToken<T> type) throws SdkSerializationException;

    /**
     * Decodes JSON text while preserving generic type arguments, including stream results.
     *
     * @param source JSON response text.
     * @param type Response type, including nested type arguments.
     * @return Decoded response.
     * @throws SdkSerializationException if the JSON is invalid or cannot map to the type.
     */
    <T> T readValue(String source, SdkTypeToken<T> type) throws SdkSerializationException;
}
