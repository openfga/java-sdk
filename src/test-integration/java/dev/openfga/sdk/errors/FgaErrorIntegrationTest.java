package dev.openfga.sdk.errors;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.openfga.OpenFGAContainer;

@TestInstance(Lifecycle.PER_CLASS)
@Testcontainers
public class FgaErrorIntegrationTest {

    @Container
    private static final OpenFGAContainer openfga = new OpenFGAContainer("openfga/openfga:v1.10.2");

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    // Test constants
    private static final String ERROR_CODE_VALIDATION_ERROR = "validation_error";
    private static final String ERROR_CODE_STORE_ID_NOT_FOUND = "store_id_not_found";
    private static final String OPERATION_WRITE = "write";
    private static final String OPERATION_CHECK = "check";
    private static final String OPERATION_READ = "read";
    private static final String OPERATION_EXPAND = "expand";
    private static final int HTTP_STATUS_BAD_REQUEST = 400;
    private static final int HTTP_STATUS_NOT_FOUND = 404;

    private String authModelJson;
    private OpenFgaClient fga;

    @BeforeAll
    public void loadAuthModelJson() throws IOException {
        authModelJson = Files.readString(Paths.get("src", "test-integration", "resources", "auth-model.json"));
    }

    @BeforeEach
    public void initializeApi() throws Exception {
        ClientConfiguration apiConfig = new ClientConfiguration().apiUrl(openfga.getHttpEndpoint());
        fga = new OpenFgaClient(apiConfig);

        // Create a store
        String storeName = "ErrorTestStore";
        var createStoreResponse =
                fga.createStore(new CreateStoreRequest().name(storeName)).get();
        String storeId = createStoreResponse.getId();
        fga.setStoreId(storeId);

        // Write the authorization model
        WriteAuthorizationModelRequest writeModelRequest =
                mapper.readValue(authModelJson, WriteAuthorizationModelRequest.class);
        fga.writeAuthorizationModel(writeModelRequest).get();
    }

    @Test
    public void writeValidationError() throws Exception {
        // Try to write a tuple with invalid type
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());
        assertTrue(exception.getMessage().contains("type 'invalid_type' not found"));
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.getApiErrorMessage().contains("type 'invalid_type' not found"));
        assertEquals(OPERATION_WRITE, exception.getOperationName());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void writeValidationErrorWithInvalidRelation() throws Exception {
        // Try to write a tuple with valid type but invalid relation
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("document:readme")
                        .relation("invalid_relation")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());
        // Verify the formatted message includes operation name and API error details
        assertTrue(exception.getMessage().contains("write"));
        assertTrue(exception.getMessage().contains("relation 'document#invalid_relation' not found"));
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertNotNull(exception.getApiErrorMessage());
        assertEquals(OPERATION_WRITE, exception.getOperationName());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void errorMessageFormattingWithAllFields() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        String message = exception.getMessage();
        assertTrue(message.startsWith("[write]"));
        assertTrue(message.contains("HTTP 400"));
        assertTrue(message.contains("type 'invalid_type' not found"));
        assertTrue(message.contains("(" + ERROR_CODE_VALIDATION_ERROR + ")"));
        assertTrue(message.contains("[request-id: "));
        assertTrue(message.endsWith("]"));

        assertEquals(OPERATION_WRITE, exception.getOperationName());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void checkValidationError() throws Exception {
        var checkRequest = new dev.openfga.sdk.api.client.model.ClientCheckRequest()
                ._object("invalid_type:readme")
                .relation("viewer")
                .user("user:anne");

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.check(checkRequest).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        assertEquals(OPERATION_CHECK, exception.getOperationName());
        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.getMessage().contains("[check]"));
        assertTrue(exception.getMessage().contains("HTTP 400"));
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void errorDetailsAreNotLostInStackTrace() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        try {
            fga.write(request).get();
            fail("Expected ExecutionException to be thrown");
        } catch (ExecutionException e) {
            FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, e.getCause());

            String errorString = exception.toString();
            assertTrue(errorString.contains("type 'invalid_type' not found"));

            String errorMessage = exception.getMessage();
            assertTrue(errorMessage.contains("[write]"));
            assertTrue(errorMessage.contains("HTTP 400"));
            assertTrue(errorMessage.contains(ERROR_CODE_VALIDATION_ERROR));

            assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
            assertNotNull(exception.getResponseData());
        }
    }

    @Test
    public void multipleTupleErrorsShowDetailedMessage() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(
                        new ClientTupleKey()
                                ._object("invalid_type1:readme")
                                .relation("viewer")
                                .user("user:anne"),
                        new ClientTupleKey()
                                ._object("invalid_type2:readme")
                                .relation("viewer")
                                .user("user:bob")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.getMessage().contains("[write]"));
        assertTrue(exception.getMessage().contains("HTTP 400"));
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void getStoreWithInvalidStoreIdFormat() throws Exception {
        ClientConfiguration badConfig =
                new ClientConfiguration().apiUrl(openfga.getHttpEndpoint()).storeId("01HVJPQR3TXYZ9NQXABCDEFGHI");
        OpenFgaClient badClient = new OpenFgaClient(badConfig);

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            badClient.getStore().get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.getApiErrorMessage().contains("does not match regex pattern"));
        assertNotNull(exception.getRequestId());
        assertTrue(exception.getMessage().contains("HTTP 400"));
        assertTrue(exception.getMessage().contains("[getStore]"));
    }

    @Test
    public void invalidParameterException() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest();

        dev.openfga.sdk.errors.FgaInvalidParameterException exception =
                assertThrows(dev.openfga.sdk.errors.FgaInvalidParameterException.class, () -> {
                    ClientConfiguration config = new ClientConfiguration().apiUrl(openfga.getHttpEndpoint());
                    OpenFgaClient client = new OpenFgaClient(config);
                    client.write(request).get();
                });

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("storeId")
                || exception.getMessage().contains("parameter"));
    }

    @Test
    public void invalidStoreIdFormat() throws Exception {
        ClientConfiguration badConfig =
                new ClientConfiguration().apiUrl(openfga.getHttpEndpoint()).storeId("non-existent-store-id-12345");
        OpenFgaClient badClient = new OpenFgaClient(badConfig);

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("document:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            badClient.write(request).get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.getMessage().contains("[write]"));
        assertTrue(exception.getMessage().contains("HTTP 400"));
        assertTrue(exception.getMessage().contains("StoreId"));
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void readOperationError() throws Exception {
        var readRequest = new dev.openfga.sdk.api.client.model.ClientReadRequest()._object("invalid_type:");

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.read(readRequest).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        assertEquals(OPERATION_READ, exception.getOperationName());
        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.getMessage().contains("[read]"));
        assertTrue(exception.getMessage().contains("HTTP 400"));
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void expandOperationError() throws Exception {
        var expandRequest = new dev.openfga.sdk.api.client.model.ClientExpandRequest()
                ._object("invalid_type:readme")
                .relation("viewer");

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.expand(expandRequest).get();
        });

        FgaApiValidationError exception = assertInstanceOf(FgaApiValidationError.class, executionException.getCause());

        assertEquals(OPERATION_EXPAND, exception.getOperationName());
        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.getMessage().contains("[expand]"));
        assertTrue(exception.getMessage().contains("HTTP 400"));
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
    }

    @Test
    public void differentErrorCodesAreSurfaced() throws Exception {
        // Test that different validation_error subcases are properly surfaced
        // Case 1: Invalid type
        ClientWriteRequest request1 = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException ex1 =
                assertThrows(ExecutionException.class, () -> fga.write(request1).get());
        FgaApiValidationError error1 = assertInstanceOf(FgaApiValidationError.class, ex1.getCause());
        assertTrue(error1.getApiErrorMessage().contains("type"));

        // Case 2: Invalid relation
        ClientWriteRequest request2 = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("document:readme")
                        .relation("invalid_relation")
                        .user("user:anne")));

        ExecutionException ex2 =
                assertThrows(ExecutionException.class, () -> fga.write(request2).get());
        FgaApiValidationError error2 = assertInstanceOf(FgaApiValidationError.class, ex2.getCause());
        assertTrue(error2.getApiErrorMessage().contains("relation"));

        // Both should have the same error code but different messages
        assertEquals(error1.getApiErrorCode(), error2.getApiErrorCode());
        assertNotEquals(error1.getApiErrorMessage(), error2.getApiErrorMessage());
    }

    @Test
    public void errorMessageContainsOperationContext() throws Exception {
        ClientWriteRequest writeReq = new ClientWriteRequest()
                .writes(List.of(
                        new ClientTupleKey()._object("invalid:x").relation("r").user("user:x")));

        ExecutionException writeEx =
                assertThrows(ExecutionException.class, () -> fga.write(writeReq).get());
        FgaError writeError = assertInstanceOf(FgaError.class, writeEx.getCause());
        assertEquals(OPERATION_WRITE, writeError.getOperationName());
        assertEquals(HTTP_STATUS_BAD_REQUEST, writeError.getStatusCode());
        assertTrue(writeError.getMessage().contains("[write]"));
        assertTrue(writeError.getMessage().contains("HTTP 400"));

        var checkReq = new dev.openfga.sdk.api.client.model.ClientCheckRequest()
                ._object("invalid:x")
                .relation("r")
                .user("user:x");

        ExecutionException checkEx =
                assertThrows(ExecutionException.class, () -> fga.check(checkReq).get());
        FgaError checkError = assertInstanceOf(FgaError.class, checkEx.getCause());
        assertEquals(OPERATION_CHECK, checkError.getOperationName());
        assertEquals(HTTP_STATUS_BAD_REQUEST, checkError.getStatusCode());
        assertTrue(checkError.getMessage().contains("[check]"));
        assertTrue(checkError.getMessage().contains("HTTP 400"));

        assertNotEquals(writeError.getOperationName(), checkError.getOperationName());
    }

    // --- Tests for New Helper Methods ---

    @Test
    public void isValidationErrorHelper() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.isValidationError());
        assertTrue(exception.isClientError());
        assertFalse(exception.isServerError());
        assertFalse(exception.isRetryable());
    }

    @Test
    public void isNotFoundErrorHelper() throws Exception {
        var tempStoreResponse = fga.createStore(new CreateStoreRequest().name("TempStoreForNotFoundTest"))
                .get();
        String tempStoreId = tempStoreResponse.getId();

        ClientConfiguration tempConfig = new ClientConfiguration().apiUrl(openfga.getHttpEndpoint());
        OpenFgaClient tempClient = new OpenFgaClient(tempConfig);
        tempClient.setStoreId(tempStoreId);
        tempClient.deleteStore().get();

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            tempClient.getStore().get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_NOT_FOUND, exception.getStatusCode());
        assertEquals(ERROR_CODE_STORE_ID_NOT_FOUND, exception.getApiErrorCode());
        assertTrue(exception.isNotFoundError());
        assertTrue(exception.isClientError());
        assertFalse(exception.isServerError());
        assertFalse(exception.isRetryable());
        assertFalse(exception.isValidationError());
        assertTrue(exception.getMessage().contains("HTTP 404"));
        assertTrue(exception.getMessage().contains("[getStore]"));
    }

    @Test
    public void isClientErrorHelper() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.isClientError());
        assertFalse(exception.isServerError());
    }

    @Test
    public void errorCategorizationHelpers() throws Exception {
        ClientConfiguration badConfig =
                new ClientConfiguration().apiUrl(openfga.getHttpEndpoint()).storeId("non-existent-store-id");
        OpenFgaClient badClient = new OpenFgaClient(badConfig);

        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("document:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            badClient.write(request).get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertTrue(exception.isClientError());
        assertTrue(exception.isValidationError());
        assertFalse(exception.isServerError());
        assertFalse(exception.isRetryable());
        assertFalse(exception.isAuthenticationError());
        assertFalse(exception.isNotFoundError());
    }

    @Test
    public void isRetryableHelper() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertFalse(exception.isRetryable());
    }

    @Test
    public void helperMethodsConsistency() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.isClientError());
        assertFalse(exception.isServerError());
        assertTrue(exception.isValidationError());
        assertFalse(exception.isRetryable());
    }

    @Test
    public void errorCodeFieldsAccessibility() throws Exception {
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        ._object("invalid_type:readme")
                        .relation("viewer")
                        .user("user:anne")));

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> {
            fga.write(request).get();
        });

        FgaError exception = assertInstanceOf(FgaError.class, executionException.getCause());

        assertEquals(HTTP_STATUS_BAD_REQUEST, exception.getStatusCode());
        assertEquals(ERROR_CODE_VALIDATION_ERROR, exception.getApiErrorCode());
        assertEquals(OPERATION_WRITE, exception.getOperationName());
        assertNotNull(exception.getApiErrorMessage());
        assertNotNull(exception.getRequestId());
        assertTrue(exception.getRequestId().matches("[a-zA-Z0-9-]+"));
    }

    @Test
    public void messageFormatConsistency() throws Exception {
        ClientWriteRequest writeReq = new ClientWriteRequest()
                .writes(List.of(
                        new ClientTupleKey()._object("invalid:x").relation("r").user("user:x")));

        ExecutionException writeEx =
                assertThrows(ExecutionException.class, () -> fga.write(writeReq).get());
        FgaError writeError = assertInstanceOf(FgaError.class, writeEx.getCause());

        var checkReq = new dev.openfga.sdk.api.client.model.ClientCheckRequest()
                ._object("invalid:x")
                .relation("r")
                .user("user:x");

        ExecutionException checkEx =
                assertThrows(ExecutionException.class, () -> fga.check(checkReq).get());
        FgaError checkError = assertInstanceOf(FgaError.class, checkEx.getCause());

        String writeMsg = writeError.getMessage();
        String checkMsg = checkError.getMessage();

        assertTrue(writeMsg.matches("\\[\\w+\\] HTTP \\d{3} .+"));
        assertTrue(checkMsg.matches("\\[\\w+\\] HTTP \\d{3} .+"));

        assertEquals(HTTP_STATUS_BAD_REQUEST, writeError.getStatusCode());
        assertEquals(HTTP_STATUS_BAD_REQUEST, checkError.getStatusCode());
        assertTrue(writeMsg.contains("[write]"));
        assertTrue(writeMsg.contains("HTTP 400"));
        assertTrue(writeMsg.contains("(" + ERROR_CODE_VALIDATION_ERROR + ")"));
        assertTrue(writeMsg.contains("[request-id: "));

        assertTrue(checkMsg.contains("[check]"));
        assertTrue(checkMsg.contains("HTTP 400"));
        assertTrue(checkMsg.contains("(" + ERROR_CODE_VALIDATION_ERROR + ")"));
        assertTrue(checkMsg.contains("[request-id: "));
    }
}
