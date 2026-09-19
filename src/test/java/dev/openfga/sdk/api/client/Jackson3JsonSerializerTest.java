package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.openfga.sdk.api.model.CheckRequest;
import dev.openfga.sdk.api.model.CheckRequestTupleKey;
import dev.openfga.sdk.api.model.ConsistencyPreference;
import dev.openfga.sdk.api.model.Store;
import dev.openfga.sdk.api.model.StreamResult;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import dev.openfga.sdk.errors.SdkSerializationException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Jackson3JsonSerializerTest {
    private final ObjectMapper baseline = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
            .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

    @Test
    void writesGoldenJsonWithNullOmissionOrderAndEnumFormatting() throws Exception {
        JsonSerializer serializer = JsonSerializer.createDefault();
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
        assertArrayEquals(baseline.writeValueAsBytes(request), serializer.writeValueAsBytes(request));
    }

    @Test
    void writesExplicitNullableRequiredValueAndOmitsOptionalNulls() throws Exception {
        JsonSerializer serializer = JsonSerializer.createDefault();
        CheckRequest request = new CheckRequest();

        assertArrayEquals(
                "{\"tuple_key\":null,\"consistency\":\"UNSPECIFIED\"}".getBytes(StandardCharsets.UTF_8),
                serializer.writeValueAsBytes(request));
        assertArrayEquals(baseline.writeValueAsBytes(request), serializer.writeValueAsBytes(request));
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
                JsonSerializer.createDefault().writeValueAsBytes(store));
        assertArrayEquals(
                baseline.writeValueAsBytes(store),
                JsonSerializer.createDefault().writeValueAsBytes(store));
    }

    @Test
    void readsGenericStreamResult() throws Exception {
        JsonSerializer serializer = JsonSerializer.createDefault();
        SdkTypeToken<StreamResult<StreamedListObjectsResponse>> type =
                new SdkTypeToken<StreamResult<StreamedListObjectsResponse>>() {};

        StreamResult<StreamedListObjectsResponse> result = serializer.readValue(
                "{\"result\":{\"object\":\"document:roadmap\"}}".getBytes(StandardCharsets.UTF_8), type);

        assertEquals("document:roadmap", result.getResult().getObject());
        com.fasterxml.jackson.core.type.TypeReference<StreamResult<StreamedListObjectsResponse>> baselineType =
                new com.fasterxml.jackson.core.type.TypeReference<StreamResult<StreamedListObjectsResponse>>() {};
        assertEquals(
                baseline.readValue("{\"result\":{\"object\":\"document:roadmap\"}}", baselineType)
                        .getResult(),
                result.getResult());
    }

    @Test
    void readsDatesAndUnknownFieldsLikeJackson2() throws Exception {
        String json = "{\"id\":\"store-id\",\"name\":\"store\","
                + "\"created_at\":\"2026-09-14T12:34:56.123+02:00\",\"future_field\":true}";
        Store actual = JsonSerializer.createDefault().readValue(json, Store.class);
        assertEquals(baseline.readValue(json, Store.class), actual);
        assertEquals(OffsetDateTime.parse("2026-09-14T12:34:56.123+02:00"), actual.getCreatedAt());
    }

    @Test
    void exposesMalformedJsonAsSdkSerializationException() {
        JsonSerializer serializer = JsonSerializer.createDefault();
        byte[] malformed = "{".getBytes(StandardCharsets.UTF_8);
        SdkTypeToken<StreamResult<Store>> type = new SdkTypeToken<StreamResult<Store>>() {};

        assertNotNull(assertThrows(SdkSerializationException.class, () -> serializer.readValue("{", Store.class))
                .getCause());
        assertNotNull(assertThrows(SdkSerializationException.class, () -> serializer.readValue(malformed, Store.class))
                .getCause());
        assertNotNull(assertThrows(SdkSerializationException.class, () -> serializer.readValue("{", type))
                .getCause());
        assertNotNull(assertThrows(SdkSerializationException.class, () -> serializer.readValue(malformed, type))
                .getCause());
    }

    @Test
    void readsConcreteResponseFromBytes() throws Exception {
        Store store = JsonSerializer.createDefault()
                .readValue("{\"id\":\"store-id\",\"name\":\"store\"}".getBytes(StandardCharsets.UTF_8), Store.class);

        assertEquals(new Store().id("store-id").name("store"), store);
    }

    @Test
    void preservesGetterFailureWhenSerializationFails() {
        IllegalStateException failure = new IllegalStateException("Cannot read payload");
        Object payload = new Object() {
            public String getValue() {
                throw failure;
            }
        };

        SdkSerializationException error =
                assertThrows(SdkSerializationException.class, () -> JsonSerializer.createDefault()
                        .writeValueAsBytes(payload));

        assertNotNull(error.getCause());
        assertSame(failure, error.getCause().getCause());
    }
}
