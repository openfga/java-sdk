package dev.openfga.sdk.api.client;

import dev.openfga.sdk.errors.SdkSerializationException;

/** Serializes SDK request and response values without exposing a JSON library. */
public interface JsonSerializer {
    /** Creates the SDK default serializer. */
    static JsonSerializer createDefault() {
        return new Jackson2JsonSerializer();
    }

    byte[] writeValueAsBytes(Object value) throws SdkSerializationException;

    <T> T readValue(byte[] source, Class<T> type) throws SdkSerializationException;

    <T> T readValue(String source, Class<T> type) throws SdkSerializationException;

    <T> T readValue(byte[] source, SdkTypeToken<T> type) throws SdkSerializationException;

    <T> T readValue(String source, SdkTypeToken<T> type) throws SdkSerializationException;
}
