package dev.openfga.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import dev.openfga.sdk.api.client.SdkTypeToken;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.junit.jupiter.api.Test;

class SdkTypeTokenTest {
    @Test
    void exposesCapturedTypeToExternalSerializer() {
        SdkTypeToken<List<String>> type = new SdkTypeToken<List<String>>() {};

        ParameterizedType capturedType = assertInstanceOf(ParameterizedType.class, type.getType());

        assertEquals(List.class, capturedType.getRawType());
        assertEquals(String.class, capturedType.getActualTypeArguments()[0]);
    }
}
