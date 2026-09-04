package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.model.CheckRequest;
import dev.openfga.sdk.api.model.CheckRequestTupleKey;
import dev.openfga.sdk.api.model.ConsistencyPreference;
import dev.openfga.sdk.api.model.StreamResult;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Jackson2JsonSerializerTest {
    @Test
    void preservesDefaultRequestBytes() throws Exception {
        ObjectMapper objectMapper = Jackson2JsonSerializer.createDefaultObjectMapper();
        Jackson2JsonSerializer serializer = new Jackson2JsonSerializer();
        CheckRequest request = new CheckRequest()
                .tupleKey(new CheckRequestTupleKey()
                        .user("user:anne")
                        .relation("viewer")
                        ._object("document:roadmap"))
                .authorizationModelId("01H0FGA")
                .context(Map.of("region", "us"))
                .consistency(ConsistencyPreference.HIGHER_CONSISTENCY);

        assertArrayEquals(objectMapper.writeValueAsBytes(request), serializer.writeValueAsBytes(request));
    }

    @Test
    void readsGenericStreamResult() throws Exception {
        Jackson2JsonSerializer serializer = new Jackson2JsonSerializer();
        SdkTypeToken<StreamResult<StreamedListObjectsResponse>> type =
                new SdkTypeToken<StreamResult<StreamedListObjectsResponse>>() {};

        StreamResult<StreamedListObjectsResponse> result = serializer.readValue(
                "{\"result\":{\"object\":\"document:roadmap\"}}".getBytes(StandardCharsets.UTF_8), type);

        assertEquals("document:roadmap", result.getResult().getObject());
    }
}
