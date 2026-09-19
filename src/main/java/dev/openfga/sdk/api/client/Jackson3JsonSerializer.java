package dev.openfga.sdk.api.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.openfga.sdk.errors.SdkSerializationException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

/** Serializes SDK values with Jackson 3. */
final class Jackson3JsonSerializer implements JsonSerializer {
    private final JsonMapper objectMapper = JsonMapper.builderWithJackson2Defaults()
            .changeDefaultPropertyInclusion(inclusion ->
                    JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .enable(EnumFeature.READ_ENUMS_USING_TO_STRING)
            .build();

    @Override
    public byte[] writeValueAsBytes(Object value) throws SdkSerializationException {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JacksonException error) {
            throw new SdkSerializationException("Cannot serialize JSON value", error);
        }
    }

    @Override
    public <T> T readValue(byte[] source, Class<T> type) throws SdkSerializationException {
        try {
            return objectMapper.readValue(source, type);
        } catch (JacksonException error) {
            throw new SdkSerializationException("Cannot deserialize JSON value", error);
        }
    }

    @Override
    public <T> T readValue(String source, Class<T> type) throws SdkSerializationException {
        try {
            return objectMapper.readValue(source, type);
        } catch (JacksonException error) {
            throw new SdkSerializationException("Cannot deserialize JSON value", error);
        }
    }

    @Override
    public <T> T readValue(byte[] source, SdkTypeToken<T> type) throws SdkSerializationException {
        try {
            return objectMapper.readValue(source, objectMapper.getTypeFactory().constructType(type.getType()));
        } catch (JacksonException error) {
            throw new SdkSerializationException("Cannot deserialize JSON value", error);
        }
    }

    @Override
    public <T> T readValue(String source, SdkTypeToken<T> type) throws SdkSerializationException {
        try {
            return objectMapper.readValue(source, objectMapper.getTypeFactory().constructType(type.getType()));
        } catch (JacksonException error) {
            throw new SdkSerializationException("Cannot deserialize JSON value", error);
        }
    }
}
