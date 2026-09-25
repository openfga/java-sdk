package dev.openfga.sdk;

import dev.openfga.sdk.api.client.JsonSerializer;
import dev.openfga.sdk.api.client.SdkTypeToken;
import tools.jackson.databind.json.JsonMapper;

/** Keeps the custom mapper configuration used by HTTP request tests. */
public final class TestJsonSerializer implements JsonSerializer {
    private final JsonMapper mapper = JsonMapper.builderWithJackson2Defaults().build();

    @Override
    public byte[] writeValueAsBytes(Object value) {
        return mapper.writeValueAsBytes(value);
    }

    @Override
    public <T> T readValue(byte[] source, Class<T> type) {
        return mapper.readValue(source, type);
    }

    @Override
    public <T> T readValue(String source, Class<T> type) {
        return mapper.readValue(source, type);
    }

    @Override
    public <T> T readValue(byte[] source, SdkTypeToken<T> type) {
        return mapper.readValue(source, mapper.getTypeFactory().constructType(type.getType()));
    }

    @Override
    public <T> T readValue(String source, SdkTypeToken<T> type) {
        return mapper.readValue(source, mapper.getTypeFactory().constructType(type.getType()));
    }
}
