package dev.openfga.sdk.api.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.openfga.sdk.errors.SdkSerializationException;
import java.io.IOException;
import org.openapitools.jackson.nullable.JsonNullableModule;

/** Serializes SDK values with Jackson 2. */
final class Jackson2JsonSerializer implements JsonSerializer {
    private final ObjectMapper objectMapper;

    Jackson2JsonSerializer() {
        this(createDefaultObjectMapper());
    }

    Jackson2JsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public byte[] writeValueAsBytes(Object value) throws SdkSerializationException {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (IOException error) {
            throw new SdkSerializationException("Cannot serialize JSON value", error);
        }
    }

    @Override
    public <T> T readValue(byte[] source, Class<T> type) throws SdkSerializationException {
        try {
            return objectMapper.readValue(source, type);
        } catch (IOException error) {
            throw new SdkSerializationException("Cannot deserialize JSON value", error);
        }
    }

    @Override
    public <T> T readValue(String source, Class<T> type) throws SdkSerializationException {
        try {
            return objectMapper.readValue(source, type);
        } catch (IOException error) {
            throw new SdkSerializationException("Cannot deserialize JSON value", error);
        }
    }

    @Override
    public <T> T readValue(byte[] source, SdkTypeToken<T> type) throws SdkSerializationException {
        try {
            return objectMapper.readValue(source, objectMapper.getTypeFactory().constructType(type.getType()));
        } catch (IOException error) {
            throw new SdkSerializationException("Cannot deserialize JSON value", error);
        }
    }

    @Override
    public <T> T readValue(String source, SdkTypeToken<T> type) throws SdkSerializationException {
        try {
            return objectMapper.readValue(source, objectMapper.getTypeFactory().constructType(type.getType()));
        } catch (IOException error) {
            throw new SdkSerializationException("Cannot deserialize JSON value", error);
        }
    }

    static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new JsonNullableModule());
        return objectMapper;
    }
}
