package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.openfga.sdk.api.model.CheckRequest;
import dev.openfga.sdk.api.model.CheckRequestTupleKey;
import dev.openfga.sdk.api.model.ConsistencyPreference;
import dev.openfga.sdk.api.model.Store;
import dev.openfga.sdk.api.model.StreamResult;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

class Jackson2JsonSerializerTest {
    @Test
    void writesGoldenJsonWithNullOmissionOrderAndEnumFormatting() throws Exception {
        Jackson2JsonSerializer serializer = new Jackson2JsonSerializer();
        CheckRequest request = new CheckRequest()
                .tupleKey(new CheckRequestTupleKey()
                        .user("user:anne")
                        .relation("viewer")
                        ._object("document:roadmap"))
                .authorizationModelId("01H0FGA")
                .context(Map.of("region", "us"))
                .consistency(ConsistencyPreference.HIGHER_CONSISTENCY);

        assertArrayEquals(
                "{\"tuple_key\":{\"user\":\"user:anne\",\"relation\":\"viewer\",\"object\":\"document:roadmap\"},\"authorization_model_id\":\"01H0FGA\",\"context\":{\"region\":\"us\"},\"consistency\":\"HIGHER_CONSISTENCY\"}"
                        .getBytes(StandardCharsets.UTF_8),
                serializer.writeValueAsBytes(request));
    }

    @Test
    void writesExplicitNullableRequiredValueAndOmitsOptionalNulls() throws Exception {
        Jackson2JsonSerializer serializer = new Jackson2JsonSerializer();
        CheckRequest request = new CheckRequest();

        assertArrayEquals(
                "{\"tuple_key\":null,\"consistency\":\"UNSPECIFIED\"}".getBytes(StandardCharsets.UTF_8),
                serializer.writeValueAsBytes(request));
    }

    @Test
    void writesGoldenDateWithOriginalOffset() throws Exception {
        Store store = new Store()
                .id("store-id")
                .name("store")
                .createdAt(OffsetDateTime.parse("2026-09-14T12:34:56.123+02:00"))
                .updatedAt(OffsetDateTime.parse("2026-09-14T10:35:00Z"));

        assertArrayEquals(
                ("{\"id\":\"store-id\",\"name\":\"store\","
                                + "\"created_at\":\"2026-09-14T12:34:56.123+02:00\","
                                + "\"updated_at\":\"2026-09-14T10:35:00Z\"}")
                        .getBytes(StandardCharsets.UTF_8),
                new Jackson2JsonSerializer().writeValueAsBytes(store));
    }

    @Test
    void distinguishesUndefinedNullAndPresentNullableValues() throws Exception {
        assertArrayEquals(
                "{\"explicitNull\":null,\"present\":\"value\"}".getBytes(StandardCharsets.UTF_8),
                new Jackson2JsonSerializer().writeValueAsBytes(new NullableValues()));
    }

    @JsonPropertyOrder({"undefined", "explicitNull", "present"})
    static class NullableValues {
        public JsonNullable<String> undefined = JsonNullable.undefined();
        public JsonNullable<String> explicitNull = JsonNullable.of(null);
        public JsonNullable<String> present = JsonNullable.of("value");
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
