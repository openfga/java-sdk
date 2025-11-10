package dev.openfga.sdk.api.client;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;
import static java.util.UUID.randomUUID;

import dev.openfga.sdk.api.*;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.api.model.*;
import dev.openfga.sdk.constants.FgaConstants;
import dev.openfga.sdk.errors.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OpenFgaClient {
    private final ApiClient apiClient;
    private ClientConfiguration configuration;
    private OpenFgaApi api;

    public OpenFgaClient(ClientConfiguration configuration) throws FgaInvalidParameterException {
        this(configuration, new ApiClient());
    }

    public OpenFgaClient(ClientConfiguration configuration, ApiClient apiClient) throws FgaInvalidParameterException {
        this.apiClient = apiClient;
        this.configuration = configuration;
        this.api = new OpenFgaApi(configuration, apiClient);
    }

    /* ***********
     * Utilities *
     *************/

    /**
     * Returns the underlying low-level OpenFgaApi instance.
     */
    public OpenFgaApi getApi() {
        return api;
    }

    public void setStoreId(String storeId) {
        configuration.storeId(storeId);
    }

    public void setAuthorizationModelId(String authorizationModelId) {
        configuration.authorizationModelId(authorizationModelId);
    }

    public void setConfiguration(ClientConfiguration configuration) throws FgaInvalidParameterException {
        this.configuration = configuration;
        this.api = new OpenFgaApi(configuration, apiClient);
    }

    /* ********
     * Stores *
     **********/

    /**
     * ListStores - Get a paginated list of stores.
     */
    public CompletableFuture<ClientListStoresResponse> listStores() throws FgaInvalidParameterException {
        configuration.assertValid();
        return call(() -> api.listStores(null, null, null)).thenApply(ClientListStoresResponse::new);
    }

    /**
     * ListStores - Get a paginated list of stores.
     */
    public CompletableFuture<ClientListStoresResponse> listStores(ClientListStoresOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        var overrides = new ConfigurationOverride().addHeaders(options);
        return call(() -> api.listStores(
                        options.getPageSize(), options.getContinuationToken(), options.getName(), overrides))
                .thenApply(ClientListStoresResponse::new);
    }

    /**
     * CreateStore - Initialize a store
     */
    public CompletableFuture<ClientCreateStoreResponse> createStore(CreateStoreRequest request)
            throws FgaInvalidParameterException {
        return createStore(request, null);
    }

    /**
     * CreateStore - Initialize a store
     */
    public CompletableFuture<ClientCreateStoreResponse> createStore(
            CreateStoreRequest request, ClientCreateStoreOptions options) throws FgaInvalidParameterException {
        configuration.assertValid();
        var overrides = new ConfigurationOverride().addHeaders(options);
        return call(() -> api.createStore(request, overrides)).thenApply(ClientCreateStoreResponse::new);
    }

    /**
     * GetStore - Get information about the current store.
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientGetStoreResponse> getStore() throws FgaInvalidParameterException {
        return getStore(null);
    }

    /**
     * GetStore - Get information about the current store.
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientGetStoreResponse> getStore(ClientGetStoreOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();
        var overrides = new ConfigurationOverride().addHeaders(options);
        return call(() -> api.getStore(storeId, overrides)).thenApply(ClientGetStoreResponse::new);
    }

    /**
     * DeleteStore - Delete a store
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientDeleteStoreResponse> deleteStore() throws FgaInvalidParameterException {
        return deleteStore(null);
    }

    /**
     * DeleteStore - Delete a store
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientDeleteStoreResponse> deleteStore(ClientDeleteStoreOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();
        var overrides = new ConfigurationOverride().addHeaders(options);
        return call(() -> api.deleteStore(storeId, overrides)).thenApply(ClientDeleteStoreResponse::new);
    }

    /* **********************
     * Authorization Models *
     ************************/

    /**
     * ReadAuthorizationModels - Read all authorization models
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadAuthorizationModelsResponse> readAuthorizationModels()
            throws FgaInvalidParameterException {
        return readAuthorizationModels(null);
    }

    /**
     * ReadAuthorizationModels - Read all authorization models
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadAuthorizationModelsResponse> readAuthorizationModels(
            ClientReadAuthorizationModelsOptions options) throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        Integer pageSize;
        String continuationToken;

        if (options != null) {
            pageSize = options.getPageSize();
            continuationToken = options.getContinuationToken();
        } else {
            // null are valid for these values
            continuationToken = null;
            pageSize = null;
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.readAuthorizationModels(storeId, pageSize, continuationToken, overrides))
                .thenApply(ClientReadAuthorizationModelsResponse::new);
    }

    /**
     * WriteAuthorizationModel - Create a new version of the authorization model
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteAuthorizationModelResponse> writeAuthorizationModel(
            WriteAuthorizationModelRequest request) throws FgaInvalidParameterException {
        return writeAuthorizationModel(request, null);
    }

    /**
     * WriteAuthorizationModel - Create a new version of the authorization model
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteAuthorizationModelResponse> writeAuthorizationModel(
            WriteAuthorizationModelRequest request, ClientWriteAuthorizationModelOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();
        var overrides = new ConfigurationOverride().addHeaders(options);
        return call(() -> api.writeAuthorizationModel(storeId, request, overrides))
                .thenApply(ClientWriteAuthorizationModelResponse::new);
    }

    /**
     * ReadAuthorizationModel - Read the current authorization model
     *
     * @throws FgaInvalidParameterException When either the Store ID or Authorization Model ID are null, empty, or whitespace
     */
    public CompletableFuture<ClientReadAuthorizationModelResponse> readAuthorizationModel()
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();
        String authorizationModelId = configuration.getAuthorizationModelIdChecked();
        return call(() -> api.readAuthorizationModel(storeId, authorizationModelId))
                .thenApply(ClientReadAuthorizationModelResponse::new);
    }

    /**
     * ReadAuthorizationModel - Read the current authorization model
     *
     * @throws FgaInvalidParameterException When either the Store ID or Authorization Model ID are null, empty, or whitespace
     */
    public CompletableFuture<ClientReadAuthorizationModelResponse> readAuthorizationModel(
            ClientReadAuthorizationModelOptions options) throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();
        // Set authorizationModelId from options if available; otherwise, require a valid configuration value
        String authorizationModelId;
        if (options != null && !isNullOrWhitespace(options.getAuthorizationModelId())) {
            authorizationModelId = options.getAuthorizationModelIdChecked();
        } else {
            authorizationModelId = configuration.getAuthorizationModelIdChecked();
        }
        var overrides = new ConfigurationOverride().addHeaders(options);
        return call(() -> api.readAuthorizationModel(storeId, authorizationModelId, overrides))
                .thenApply(ClientReadAuthorizationModelResponse::new);
    }

    /**
     * ReadLatestAuthorizationModel - Read the latest authorization model for the current store
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadAuthorizationModelResponse> readLatestAuthorizationModel()
            throws FgaInvalidParameterException {
        return readLatestAuthorizationModel(null);
    }

    /**
     * ReadLatestAuthorizationModel - Read the latest authorization model for the current store
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadAuthorizationModelResponse> readLatestAuthorizationModel(
            ClientReadLatestAuthorizationModelOptions options) throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();
        var overrides = new ConfigurationOverride().addHeaders(options);
        return call(() -> api.readAuthorizationModels(storeId, 1, null, overrides))
                .thenApply(ClientReadAuthorizationModelResponse::latestOf);
    }

    /* *********************
     * Relationship Tuples *
     ***********************/

    /**
     * Read Changes - Read the list of historical relationship tuple writes and deletes
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadChangesResponse> readChanges(ClientReadChangesRequest request)
            throws FgaInvalidParameterException {
        return readChanges(request, null);
    }

    /**
     * Read Changes - Read the list of historical relationship tuple writes and deletes
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadChangesResponse> readChanges(
            ClientReadChangesRequest request, ClientReadChangesOptions readChangesOptions)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();
        var options = readChangesOptions != null ? readChangesOptions : new ClientReadChangesOptions();
        var overrides = new ConfigurationOverride().addHeaders(options);
        return call(() -> api.readChanges(
                        storeId,
                        request.getType(),
                        options.getPageSize(),
                        options.getContinuationToken(),
                        request.getStartTime(),
                        overrides))
                .thenApply(ClientReadChangesResponse::new);
    }

    /**
     * Read - Read tuples previously written to the store (does not evaluate)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadResponse> read(ClientReadRequest request) throws FgaInvalidParameterException {
        return read(request, null);
    }

    /**
     * Read - Read tuples previously written to the store (does not evaluate)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadResponse> read(ClientReadRequest request, ClientReadOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        ReadRequest body = new ReadRequest();

        if (request != null
                && (request.getUser() != null || request.getRelation() != null || request.getObject() != null)) {
            body.tupleKey(new ReadRequestTupleKey()
                    .user(request.getUser())
                    .relation(request.getRelation())
                    ._object(request.getObject()));
        }

        if (options != null) {
            body.pageSize(options.getPageSize()).continuationToken(options.getContinuationToken());
            if (options.getConsistency() != null) {
                body.consistency(options.getConsistency());
            }
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.read(storeId, body, overrides)).thenApply(ClientReadResponse::new);
    }

    /**
     * Write - Create or delete relationship tuples
     *
     * <p>This method can operate in two modes depending on the options provided:</p>
     *
     * <h3>Transactional Mode (default)</h3>
     * <p>When {@code options.disableTransactions()} is false or not set:</p>
     * <ul>
     *   <li>All writes and deletes are executed as a single atomic transaction</li>
     *   <li>If any tuple fails, the entire operation fails and no changes are made</li>
     *   <li>On success: All tuples in the response have {@code ClientWriteStatus.SUCCESS}</li>
     *   <li>On failure: The method throws an exception (no partial results)</li>
     * </ul>
     *
     * <h3>Non-Transactional Mode</h3>
     * <p>When {@code options.disableTransactions()} is true:</p>
     * <ul>
     *   <li>Tuples are processed in chunks (size controlled by {@code transactionChunkSize})</li>
     *   <li>Each chunk is processed independently - some may succeed while others fail</li>
     *   <li>The method always returns a response (never throws for tuple-level failures)</li>
     *   <li>Individual tuple results are indicated by {@code ClientWriteStatus} in the response</li>
     * </ul>
     *
     * <h4>Non-Transactional Success Scenarios:</h4>
     * <ul>
     *   <li>All tuples succeed: All responses have {@code status = SUCCESS, error = null}</li>
     *   <li>Mixed results: Some responses have {@code status = SUCCESS}, others have {@code status = FAILURE} with error details</li>
     *   <li>All tuples fail: All responses have {@code status = FAILURE} with individual error details</li>
     * </ul>
     *
     * <h4>Non-Transactional Exception Scenarios:</h4>
     * <ul>
     *   <li>Authentication errors: Method throws immediately (no partial processing)</li>
     *   <li>Configuration errors: Method throws before processing any tuples</li>
     *   <li>Network/infrastructure errors: Method may throw depending on the specific error</li>
     * </ul>
     *
     * <h4>Caller Responsibilities:</h4>
     * <ul>
     *   <li>For transactional mode: Handle exceptions for any failures</li>
     *   <li>For non-transactional mode: Check {@code status} field of each tuple in the response</li>
     *   <li>For non-transactional mode: Implement retry logic for failed tuples if needed</li>
     *   <li>For non-transactional mode: Handle partial success scenarios appropriately</li>
     * </ul>
     *
     * @param request The write request containing tuples to create or delete
     * @return A CompletableFuture containing the write response with individual tuple results
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteResponse> write(ClientWriteRequest request)
            throws FgaInvalidParameterException {
        return write(request, null);
    }

    /**
     * Write - Create or delete relationship tuples
     *
     * <p>This method can operate in two modes depending on the options provided:</p>
     *
     * <h3>Transactional Mode (default)</h3>
     * <p>When {@code options.disableTransactions()} is false or not set:</p>
     * <ul>
     *   <li>All writes and deletes are executed as a single atomic transaction</li>
     *   <li>If any tuple fails, the entire operation fails and no changes are made</li>
     *   <li>On success: All tuples in the response have {@code ClientWriteStatus.SUCCESS}</li>
     *   <li>On failure: The method throws an exception (no partial results)</li>
     * </ul>
     *
     * <h3>Non-Transactional Mode</h3>
     * <p>When {@code options.disableTransactions()} is true:</p>
     * <ul>
     *   <li>Tuples are processed in chunks (size controlled by {@code transactionChunkSize})</li>
     *   <li>Each chunk is processed independently - some may succeed while others fail</li>
     *   <li>The method always returns a response (never throws for tuple-level failures)</li>
     *   <li>Individual tuple results are indicated by {@code ClientWriteStatus} in the response</li>
     * </ul>
     *
     * <h4>Non-Transactional Success Scenarios:</h4>
     * <ul>
     *   <li>All tuples succeed: All responses have {@code status = SUCCESS, error = null}</li>
     *   <li>Mixed results: Some responses have {@code status = SUCCESS}, others have {@code status = FAILURE} with error details</li>
     *   <li>All tuples fail: All responses have {@code status = FAILURE} with individual error details</li>
     * </ul>
     *
     * <h4>Non-Transactional Exception Scenarios:</h4>
     * <ul>
     *   <li>Authentication errors: Method throws immediately (no partial processing)</li>
     *   <li>Configuration errors: Method throws before processing any tuples</li>
     *   <li>Network/infrastructure errors: Method may throw depending on the specific error</li>
     * </ul>
     *
     * <h4>Caller Responsibilities:</h4>
     * <ul>
     *   <li>For transactional mode: Handle exceptions for any failures</li>
     *   <li>For non-transactional mode: Check {@code status} field of each tuple in the response</li>
     *   <li>For non-transactional mode: Implement retry logic for failed tuples if needed</li>
     *   <li>For non-transactional mode: Handle partial success scenarios appropriately</li>
     * </ul>
     *
     * @param request The write request containing tuples to create or delete
     * @param options Write options including transaction mode and chunk size settings
     * @return A CompletableFuture containing the write response with individual tuple results
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteResponse> write(ClientWriteRequest request, ClientWriteOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        if (options != null && options.disableTransactions()) {
            return writeNonTransaction(storeId, request, options);
        }

        return writeTransactions(storeId, request, options);
    }

    private CompletableFuture<ClientWriteResponse> writeTransactions(
            String storeId, ClientWriteRequest request, ClientWriteOptions options) {

        WriteRequest body = new WriteRequest();

        var writeTuples = request.getWrites();
        if (writeTuples != null && !writeTuples.isEmpty()) {
            var onDuplicate = options != null ? options.getOnDuplicate() : null;
            body.writes(ClientTupleKey.asWriteRequestWrites(writeTuples, onDuplicate));
        }

        var deleteTuples = request.getDeletes();
        if (deleteTuples != null && !deleteTuples.isEmpty()) {
            var onMissing = options != null ? options.getOnMissing() : null;
            body.deletes(ClientTupleKeyWithoutCondition.asWriteRequestDeletes(deleteTuples, onMissing));
        }

        if (options != null && !isNullOrWhitespace(options.getAuthorizationModelId())) {
            body.authorizationModelId(options.getAuthorizationModelId());
        } else {
            String authorizationModelId = configuration.getAuthorizationModelId();
            body.authorizationModelId(authorizationModelId);
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.write(storeId, body, overrides)).thenApply(apiResponse -> {
            // For transaction-based writes, all tuples are successful if the call succeeds
            List<ClientWriteSingleResponse> writeResponses = writeTuples != null
                    ? writeTuples.stream()
                            .map(tuple -> new ClientWriteSingleResponse(tuple.asTupleKey(), ClientWriteStatus.SUCCESS))
                            .collect(Collectors.toList())
                    : new ArrayList<>();

            List<ClientWriteSingleResponse> deleteResponses = deleteTuples != null
                    ? deleteTuples.stream()
                            .map(tuple -> new ClientWriteSingleResponse(
                                    new TupleKey()
                                            .user(tuple.getUser())
                                            .relation(tuple.getRelation())
                                            ._object(tuple.getObject()),
                                    ClientWriteStatus.SUCCESS))
                            .collect(Collectors.toList())
                    : new ArrayList<>();

            return new ClientWriteResponse(apiResponse, writeResponses, deleteResponses);
        });
    }

    /**
     * Non-transactional write implementation that processes tuples in parallel chunks.
     *
     * <p>This method implements the error isolation behavior where individual chunk failures
     * do not prevent other chunks from being processed. It performs the following steps:</p>
     *
     * <ol>
     *   <li>Splits writes and deletes into chunks based on {@code transactionChunkSize}</li>
     *   <li>Processes each chunk as an independent transaction in parallel</li>
     *   <li>Collects results from all chunks, marking individual tuples as SUCCESS or FAILURE</li>
     *   <li>Re-throws authentication errors immediately to stop all processing</li>
     *   <li>Converts other errors to FAILURE status for affected tuples</li>
     * </ol>
     *
     * <p>The method guarantees that:</p>
     * <ul>
     *   <li>Authentication errors are never swallowed (they stop all processing)</li>
     *   <li>Other errors are isolated to their respective chunks</li>
     *   <li>The response always contains a result for every input tuple</li>
     *   <li>The order of results matches the order of input tuples</li>
     * </ul>
     *
     * @param storeId The store ID to write to
     * @param request The write request containing tuples to process
     * @param writeOptions Options including chunk size and headers
     * @return CompletableFuture with results for all tuples, marking each as SUCCESS or FAILURE
     */
    private CompletableFuture<ClientWriteResponse> writeNonTransaction(
            String storeId, ClientWriteRequest request, ClientWriteOptions writeOptions) {

        var options = writeOptions != null
                ? writeOptions
                : new ClientWriteOptions().transactionChunkSize(FgaConstants.CLIENT_MAX_METHOD_PARALLEL_REQUESTS);

        HashMap<String, String> headers = options.getAdditionalHeaders() != null
                ? new HashMap<>(options.getAdditionalHeaders())
                : new HashMap<>();
        headers.putIfAbsent(FgaConstants.CLIENT_METHOD_HEADER, "Write");
        headers.putIfAbsent(
                FgaConstants.CLIENT_BULK_REQUEST_ID_HEADER, randomUUID().toString());
        options.additionalHeaders(headers);

        int chunkSize = options.getTransactionChunkSize();

        List<CompletableFuture<List<ClientWriteSingleResponse>>> writeFutures = new ArrayList<>();
        List<CompletableFuture<List<ClientWriteSingleResponse>>> deleteFutures = new ArrayList<>();

        // Handle writes
        if (request.getWrites() != null && !request.getWrites().isEmpty()) {
            var writeChunks = chunksOf(chunkSize, request.getWrites()).collect(Collectors.toList());

            for (List<ClientTupleKey> chunk : writeChunks) {
                CompletableFuture<List<ClientWriteSingleResponse>> chunkFuture = this.writeTransactions(
                                storeId, ClientWriteRequest.ofWrites(chunk), options)
                        .thenApply(response -> {
                            // On success, mark all tuples in this chunk as successful
                            return chunk.stream()
                                    .map(tuple -> new ClientWriteSingleResponse(
                                            tuple.asTupleKey(), ClientWriteStatus.SUCCESS))
                                    .collect(Collectors.toList());
                        })
                        .exceptionally(exception -> {
                            // Re-throw authentication errors to stop all processing
                            Throwable cause =
                                    exception instanceof CompletionException ? exception.getCause() : exception;
                            if (cause instanceof FgaApiAuthenticationError) {
                                throw new CompletionException(cause);
                            }

                            // On failure, mark all tuples in this chunk as failed, but continue processing other chunks
                            return chunk.stream()
                                    .map(tuple -> new ClientWriteSingleResponse(
                                            tuple.asTupleKey(),
                                            ClientWriteStatus.FAILURE,
                                            cause instanceof Exception ? (Exception) cause : new Exception(cause)))
                                    .collect(Collectors.toList());
                        });

                writeFutures.add(chunkFuture);
            }
        }

        // Handle deletes
        if (request.getDeletes() != null && !request.getDeletes().isEmpty()) {
            var deleteChunks = chunksOf(chunkSize, request.getDeletes()).collect(Collectors.toList());

            for (List<ClientTupleKeyWithoutCondition> chunk : deleteChunks) {
                CompletableFuture<List<ClientWriteSingleResponse>> chunkFuture = this.writeTransactions(
                                storeId, ClientWriteRequest.ofDeletes(chunk), options)
                        .thenApply(response -> {
                            // On success, mark all tuples in this chunk as successful
                            return chunk.stream()
                                    .map(tuple -> new ClientWriteSingleResponse(
                                            new TupleKey()
                                                    .user(tuple.getUser())
                                                    .relation(tuple.getRelation())
                                                    ._object(tuple.getObject()),
                                            ClientWriteStatus.SUCCESS))
                                    .collect(Collectors.toList());
                        })
                        .exceptionally(exception -> {
                            // Re-throw authentication errors to stop all processing
                            Throwable cause =
                                    exception instanceof CompletionException ? exception.getCause() : exception;
                            if (cause instanceof FgaApiAuthenticationError) {
                                throw new CompletionException(cause);
                            }

                            // On failure, mark all tuples in this chunk as failed, but continue processing other chunks
                            return chunk.stream()
                                    .map(tuple -> new ClientWriteSingleResponse(
                                            new TupleKey()
                                                    .user(tuple.getUser())
                                                    .relation(tuple.getRelation())
                                                    ._object(tuple.getObject()),
                                            ClientWriteStatus.FAILURE,
                                            cause instanceof Exception ? (Exception) cause : new Exception(cause)))
                                    .collect(Collectors.toList());
                        });

                deleteFutures.add(chunkFuture);
            }
        }

        // Combine all futures
        CompletableFuture<List<ClientWriteSingleResponse>> allWritesFuture = writeFutures.isEmpty()
                ? CompletableFuture.completedFuture(new ArrayList<>())
                : CompletableFuture.allOf(writeFutures.toArray(new CompletableFuture[0]))
                        .thenApply(v -> writeFutures.stream()
                                .map(CompletableFuture::join)
                                .flatMap(List::stream)
                                .collect(Collectors.toList()));

        CompletableFuture<List<ClientWriteSingleResponse>> allDeletesFuture = deleteFutures.isEmpty()
                ? CompletableFuture.completedFuture(new ArrayList<>())
                : CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0]))
                        .thenApply(v -> deleteFutures.stream()
                                .map(CompletableFuture::join)
                                .flatMap(List::stream)
                                .collect(Collectors.toList()));

        return CompletableFuture.allOf(allWritesFuture, allDeletesFuture)
                .thenApply(v -> new ClientWriteResponse(allWritesFuture.join(), allDeletesFuture.join()));
    }

    private <T> Stream<List<T>> chunksOf(int chunkSize, List<T> list) {
        if (list == null || list.isEmpty()) {
            return Stream.empty();
        }

        int nChunks = (int) Math.ceil(list.size() / (double) chunkSize);

        int finalEndExclusive = list.size();
        Stream.Builder<List<T>> chunks = Stream.builder();

        for (int i = 0; i < nChunks; i++) {
            List<T> chunk = list.subList(i * chunkSize, Math.min((i + 1) * chunkSize, finalEndExclusive));
            chunks.add(chunk);
        }

        return chunks.build();
    }

    /**
     * WriteTuples - Utility method to write tuples, wraps Write
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteResponse> writeTuples(List<ClientTupleKey> tupleKeys)
            throws FgaInvalidParameterException {
        return writeTuples(tupleKeys, null);
    }

    /**
     * WriteTuples - Utility method to write tuples, wraps Write
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteResponse> writeTuples(
            List<ClientTupleKey> tupleKeys, ClientWriteTuplesOptions options) throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        var body = new WriteRequest();

        var onDuplicate = options != null ? options.getOnDuplicate() : null;
        body.writes(ClientTupleKey.asWriteRequestWrites(tupleKeys, onDuplicate));

        String authorizationModelId = configuration.getAuthorizationModelId();
        if (!isNullOrWhitespace(authorizationModelId)) {
            body.authorizationModelId(authorizationModelId);
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.write(storeId, body, overrides)).thenApply(apiResponse -> {
            List<ClientWriteSingleResponse> writeResponses = tupleKeys.stream()
                    .map(tuple -> new ClientWriteSingleResponse(tuple.asTupleKey(), ClientWriteStatus.SUCCESS))
                    .collect(Collectors.toList());
            return new ClientWriteResponse(apiResponse, writeResponses, new ArrayList<>());
        });
    }

    /**
     * DeleteTuples - Utility method to delete tuples, wraps Write
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteResponse> deleteTuples(List<ClientTupleKeyWithoutCondition> tupleKeys)
            throws FgaInvalidParameterException {
        return deleteTuples(tupleKeys, null);
    }

    /**
     * DeleteTuples - Utility method to delete tuples, wraps Write
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteResponse> deleteTuples(
            List<ClientTupleKeyWithoutCondition> tupleKeys, ClientDeleteTuplesOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        var body = new WriteRequest();

        var onMissing = options != null ? options.getOnMissing() : null;
        body.deletes(ClientTupleKeyWithoutCondition.asWriteRequestDeletes(tupleKeys, onMissing));

        String authorizationModelId = configuration.getAuthorizationModelId();
        if (!isNullOrWhitespace(authorizationModelId)) {
            body.authorizationModelId(authorizationModelId);
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.write(storeId, body, overrides)).thenApply(apiResponse -> {
            List<ClientWriteSingleResponse> deleteResponses = tupleKeys.stream()
                    .map(tuple -> new ClientWriteSingleResponse(
                            new TupleKey()
                                    .user(tuple.getUser())
                                    .relation(tuple.getRelation())
                                    ._object(tuple.getObject()),
                            ClientWriteStatus.SUCCESS))
                    .collect(Collectors.toList());
            return new ClientWriteResponse(apiResponse, new ArrayList<>(), deleteResponses);
        });
    }

    /* **********************
     * Relationship Queries *
     ***********************/

    /**
     * Check - Check if a user has a particular relation with an object (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientCheckResponse> check(ClientCheckRequest request)
            throws FgaInvalidParameterException {
        return check(request, null);
    }

    /**
     * Check - Check if a user has a particular relation with an object (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientCheckResponse> check(ClientCheckRequest request, ClientCheckOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        CheckRequest body = request.asCheckRequest();
        if (options != null) {
            if (options.getConsistency() != null) {
                body.consistency(options.getConsistency());
            }

            // Set authorizationModelId from options if available; otherwise, use the default from configuration
            String authorizationModelId = !isNullOrWhitespace(options.getAuthorizationModelId())
                    ? options.getAuthorizationModelId()
                    : configuration.getAuthorizationModelId();
            body.authorizationModelId(authorizationModelId);
        } else {
            body.setAuthorizationModelId(configuration.getAuthorizationModelId());
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.check(storeId, body, overrides)).thenApply(ClientCheckResponse::new);
    }

    /**
     * BatchCheck - Run a set of checks (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<List<ClientBatchCheckClientResponse>> clientBatchCheck(List<ClientCheckRequest> requests)
            throws FgaInvalidParameterException {
        return clientBatchCheck(requests, null);
    }

    /**
     * BatchCheck - Run a set of checks (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<List<ClientBatchCheckClientResponse>> clientBatchCheck(
            List<ClientCheckRequest> requests, ClientBatchCheckClientOptions batchCheckOptions)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        configuration.assertValidStoreId();

        var options = batchCheckOptions != null
                ? batchCheckOptions
                : new ClientBatchCheckClientOptions()
                        .maxParallelRequests(FgaConstants.CLIENT_MAX_METHOD_PARALLEL_REQUESTS);

        HashMap<String, String> headers = options.getAdditionalHeaders() != null
                ? new HashMap<>(options.getAdditionalHeaders())
                : new HashMap<>();
        headers.putIfAbsent(FgaConstants.CLIENT_METHOD_HEADER, "ClientBatchCheck");
        headers.putIfAbsent(
                FgaConstants.CLIENT_BULK_REQUEST_ID_HEADER, randomUUID().toString());
        options.additionalHeaders(headers);

        int maxParallelRequests = options.getMaxParallelRequests() != null
                ? options.getMaxParallelRequests()
                : FgaConstants.CLIENT_MAX_METHOD_PARALLEL_REQUESTS;
        var executor = Executors.newScheduledThreadPool(maxParallelRequests);
        var latch = new CountDownLatch(requests.size());

        var responses = new ConcurrentLinkedQueue<ClientBatchCheckClientResponse>();

        final var clientCheckOptions = options.asClientCheckOptions();

        Consumer<ClientCheckRequest> singleClientCheckRequest =
                request -> call(() -> this.check(request, clientCheckOptions))
                        .handleAsync(ClientBatchCheckClientResponse.asyncHandler(request))
                        .thenAccept(responses::add)
                        .thenRun(latch::countDown);

        try {
            requests.forEach(request -> executor.execute(() -> singleClientCheckRequest.accept(request)));
            latch.await();
            return CompletableFuture.completedFuture(new ArrayList<>(responses));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * BatchCheck - Run a set of checks (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientBatchCheckResponse> batchCheck(ClientBatchCheckRequest request)
            throws FgaInvalidParameterException, FgaValidationError {
        return batchCheck(request, null);
    }

    /**
     * BatchCheck - Run a set of checks (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientBatchCheckResponse> batchCheck(
            ClientBatchCheckRequest requests, ClientBatchCheckOptions batchCheckOptions)
            throws FgaInvalidParameterException, FgaValidationError {
        configuration.assertValid();
        configuration.assertValidStoreId();

        var options = batchCheckOptions != null
                ? batchCheckOptions
                : new ClientBatchCheckOptions()
                        .maxParallelRequests(FgaConstants.CLIENT_MAX_METHOD_PARALLEL_REQUESTS)
                        .maxBatchSize(FgaConstants.CLIENT_MAX_BATCH_SIZE);

        HashMap<String, String> headers = options.getAdditionalHeaders() != null
                ? new HashMap<>(options.getAdditionalHeaders())
                : new HashMap<>();
        headers.putIfAbsent(FgaConstants.CLIENT_METHOD_HEADER, "BatchCheck");
        headers.putIfAbsent(
                FgaConstants.CLIENT_BULK_REQUEST_ID_HEADER, randomUUID().toString());
        options.additionalHeaders(headers);

        Map<String, ClientBatchCheckItem> correlationIdToCheck = new HashMap<>();

        List<BatchCheckItem> collect = new ArrayList<>();
        for (ClientBatchCheckItem check : requests.getChecks()) {
            String correlationId = check.getCorrelationId();
            correlationId = correlationId == null || correlationId.isBlank()
                    ? randomUUID().toString()
                    : correlationId;

            BatchCheckItem batchCheckItem = new BatchCheckItem()
                    .tupleKey(new CheckRequestTupleKey()
                            .user(check.getUser())
                            .relation(check.getRelation())
                            ._object(check.getObject()))
                    .context(check.getContext())
                    .correlationId(correlationId);

            List<ClientTupleKey> contextualTuples = check.getContextualTuples();
            if (contextualTuples != null && !contextualTuples.isEmpty()) {
                batchCheckItem.contextualTuples(ClientTupleKey.asContextualTupleKeys(contextualTuples));
            }

            collect.add(batchCheckItem);

            if (correlationIdToCheck.containsKey(correlationId)) {
                throw new FgaValidationError(
                        "correlationId", "When calling batchCheck, correlation IDs must be unique");
            }

            correlationIdToCheck.put(correlationId, check);
        }

        int maxBatchSize =
                options.getMaxBatchSize() != null ? options.getMaxBatchSize() : FgaConstants.CLIENT_MAX_BATCH_SIZE;
        List<List<BatchCheckItem>> batchedChecks = IntStream.range(
                        0, (collect.size() + maxBatchSize - 1) / maxBatchSize)
                .mapToObj(i -> collect.subList(i * maxBatchSize, Math.min((i + 1) * maxBatchSize, collect.size())))
                .collect(Collectors.toList());

        int maxParallelRequests = options.getMaxParallelRequests() != null
                ? options.getMaxParallelRequests()
                : FgaConstants.CLIENT_MAX_METHOD_PARALLEL_REQUESTS;
        var executor = Executors.newScheduledThreadPool(maxParallelRequests);
        var latch = new CountDownLatch(batchedChecks.size());

        var responses = new ConcurrentLinkedQueue<ClientBatchCheckSingleResponse>();
        var failure = new AtomicReference<Throwable>();

        var override = new ConfigurationOverride().addHeaders(options);

        Consumer<List<BatchCheckItem>> singleBatchCheckRequest = request -> call(() -> {
                    BatchCheckRequest body = new BatchCheckRequest().checks(request);
                    if (options.getConsistency() != null) {
                        body.consistency(options.getConsistency());
                    }

                    // Set authorizationModelId from options if available; otherwise, use the default from configuration
                    String authorizationModelId = !isNullOrWhitespace(options.getAuthorizationModelId())
                            ? options.getAuthorizationModelId()
                            : configuration.getAuthorizationModelId();

                    if (!isNullOrWhitespace(authorizationModelId)) {
                        body.authorizationModelId(authorizationModelId);
                    }

                    return api.batchCheck(configuration.getStoreId(), body, override);
                })
                .whenComplete((batchCheckResponseApiResponse, throwable) -> {
                    try {
                        if (throwable != null) {
                            failure.compareAndSet(null, throwable);
                            return;
                        }

                        Map<String, BatchCheckSingleResult> response =
                                batchCheckResponseApiResponse.getData().getResult();

                        List<ClientBatchCheckSingleResponse> batchResults = new ArrayList<>();
                        response.forEach((key, result) -> {
                            boolean allowed = Boolean.TRUE.equals(result.getAllowed());
                            ClientBatchCheckItem checkItem = correlationIdToCheck.get(key);
                            var singleResponse =
                                    new ClientBatchCheckSingleResponse(allowed, checkItem, key, result.getError());
                            batchResults.add(singleResponse);
                        });
                        responses.addAll(batchResults);
                    } finally {
                        latch.countDown();
                    }
                });

        try {
            batchedChecks.forEach(batch -> executor.execute(() -> singleBatchCheckRequest.accept(batch)));
            latch.await();
            if (failure.get() != null) {
                return CompletableFuture.failedFuture(failure.get());
            }
            return CompletableFuture.completedFuture(new ClientBatchCheckResponse(new ArrayList<>(responses)));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Expand - Expands the relationships in userset tree format (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientExpandResponse> expand(ClientExpandRequest request)
            throws FgaInvalidParameterException {
        return expand(request, null);
    }

    /**
     * Expand - Expands the relationships in userset tree format (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientExpandResponse> expand(ClientExpandRequest request, ClientExpandOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        ExpandRequest body = new ExpandRequest();

        if (request != null) {
            body.tupleKey(
                    new ExpandRequestTupleKey().relation(request.getRelation())._object(request.getObject()));
        }

        if (options != null) {
            if (options.getConsistency() != null) {
                body.consistency(options.getConsistency());
            }

            // Set authorizationModelId from options if available; otherwise, use the default from configuration
            String authorizationModelId = !isNullOrWhitespace(options.getAuthorizationModelId())
                    ? options.getAuthorizationModelId()
                    : configuration.getAuthorizationModelId();
            body.authorizationModelId(authorizationModelId);
        } else {
            body.setAuthorizationModelId(configuration.getAuthorizationModelId());
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.expand(storeId, body, overrides)).thenApply(ClientExpandResponse::new);
    }

    /**
     * ListObjects - List the objects of a particular type that the user has a certain relation to (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientListObjectsResponse> listObjects(ClientListObjectsRequest request)
            throws FgaInvalidParameterException {
        return listObjects(request, null);
    }

    /**
     * ListObjects - List the objects of a particular type that the user has a certain relation to (evaluates)
     *
     * @throws FgaInvalidParameterException When the Store ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientListObjectsResponse> listObjects(
            ClientListObjectsRequest request, ClientListObjectsOptions options) throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        ListObjectsRequest body = new ListObjectsRequest();

        if (request != null) {
            body.user(request.getUser()).relation(request.getRelation()).type(request.getType());
            if (request.getContextualTupleKeys() != null) {
                var contextualTuples = request.getContextualTupleKeys();
                var bodyContextualTuples = ClientTupleKey.asContextualTupleKeys(contextualTuples);
                body.contextualTuples(bodyContextualTuples);
            }
            if (request.getContext() != null) {
                body.context(request.getContext());
            }
        }

        if (options != null) {
            if (options.getConsistency() != null) {
                body.consistency(options.getConsistency());
            }

            // Set authorizationModelId from options if available; otherwise, use the default from configuration
            String authorizationModelId = !isNullOrWhitespace(options.getAuthorizationModelId())
                    ? options.getAuthorizationModelId()
                    : configuration.getAuthorizationModelId();
            body.authorizationModelId(authorizationModelId);
        } else {
            body.setAuthorizationModelId(configuration.getAuthorizationModelId());
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.listObjects(storeId, body, overrides)).thenApply(ClientListObjectsResponse::new);
    }

    /**
     * ListRelations - List allowed relations a user has with an object (evaluates)
     */
    public CompletableFuture<ClientListRelationsResponse> listRelations(ClientListRelationsRequest request)
            throws FgaInvalidParameterException {
        return listRelations(request, null);
    }

    /**
     * ListRelations - List allowed relations a user has with an object (evaluates)
     */
    public CompletableFuture<ClientListRelationsResponse> listRelations(
            ClientListRelationsRequest request, ClientListRelationsOptions listRelationsOptions)
            throws FgaInvalidParameterException {
        if (request.getRelations() == null || request.getRelations().isEmpty()) {
            throw new FgaInvalidParameterException(
                    "At least 1 relation to check has to be provided when calling ListRelations");
        }

        var options = listRelationsOptions != null
                ? listRelationsOptions
                : new ClientListRelationsOptions()
                        .maxParallelRequests(FgaConstants.CLIENT_MAX_METHOD_PARALLEL_REQUESTS);

        HashMap<String, String> headers = options.getAdditionalHeaders() != null
                ? new HashMap<>(options.getAdditionalHeaders())
                : new HashMap<>();
        headers.putIfAbsent(FgaConstants.CLIENT_METHOD_HEADER, "ListRelations");
        headers.putIfAbsent(
                FgaConstants.CLIENT_BULK_REQUEST_ID_HEADER, randomUUID().toString());
        options.additionalHeaders(headers);

        var batchCheckRequests = request.getRelations().stream()
                .map(relation -> new ClientCheckRequest()
                        .user(request.getUser())
                        .relation(relation)
                        ._object(request.getObject())
                        .contextualTuples(request.getContextualTupleKeys())
                        .context(request.getContext()))
                .collect(Collectors.toList());

        return this.clientBatchCheck(batchCheckRequests, options.asClientBatchCheckClientOptions())
                .thenCompose(responses -> call(() -> ClientListRelationsResponse.fromBatchCheckResponses(responses)));
    }

    /**
     * ListUsers - List all users of the given type that the object has a relation with (evaluates)
     */
    public CompletableFuture<ClientListUsersResponse> listUsers(ClientListUsersRequest request)
            throws FgaInvalidParameterException {
        return listUsers(request, null);
    }

    /**
     * ListUsers - List all users of the given type that the object has a relation with (evaluates)
     */
    public CompletableFuture<ClientListUsersResponse> listUsers(
            ClientListUsersRequest request, ClientListUsersOptions options) throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        ListUsersRequest body = new ListUsersRequest();

        if (request != null) {
            body._object(request.getObject()).relation(request.getRelation()).userFilters(request.getUserFilters());
            if (request.getContextualTupleKeys() != null) {
                var contextualTuples = request.getContextualTupleKeys();
                var bodyContextualTuples = ClientTupleKey.asContextualTupleKeys(contextualTuples);
                body.contextualTuples(bodyContextualTuples.getTupleKeys());
            }
            if (request.getContext() != null) {
                body.context(request.getContext());
            }
        }

        if (options != null) {
            if (options.getConsistency() != null) {
                body.consistency(options.getConsistency());
            }

            // Set authorizationModelId from options if available; otherwise, use the default from configuration
            String authorizationModelId = !isNullOrWhitespace(options.getAuthorizationModelId())
                    ? options.getAuthorizationModelId()
                    : configuration.getAuthorizationModelId();
            body.authorizationModelId(authorizationModelId);
        } else {
            body.setAuthorizationModelId(configuration.getAuthorizationModelId());
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.listUsers(storeId, body, overrides)).thenApply(ClientListUsersResponse::new);
    }

    /* ************
     * Assertions *
     **************/

    /**
     * ReadAssertions - Read assertions for a particular authorization model
     *
     * @throws FgaInvalidParameterException When either the Store ID or Authorization Model ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadAssertionsResponse> readAssertions() throws FgaInvalidParameterException {
        return readAssertions(null);
    }

    /**
     * ReadAssertions - Read assertions for a particular authorization model
     *
     * @throws FgaInvalidParameterException When either the Store ID or Authorization Model ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientReadAssertionsResponse> readAssertions(ClientReadAssertionsOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        String authorizationModelId;
        if (options != null && options.hasValidAuthorizationModelId()) {
            authorizationModelId = options.getAuthorizationModelId();
        } else {
            authorizationModelId = configuration.getAuthorizationModelIdChecked();
        }

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.readAssertions(storeId, authorizationModelId, overrides))
                .thenApply(ClientReadAssertionsResponse::new);
    }

    /**
     * WriteAssertions - Updates assertions for a particular authorization model
     *
     * @throws FgaInvalidParameterException When either the Store ID or Authorization Model ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteAssertionsResponse> writeAssertions(List<ClientAssertion> assertions)
            throws FgaInvalidParameterException {
        return writeAssertions(assertions, null);
    }

    /**
     * WriteAssertions - Updates assertions for a particular authorization model
     *
     * @throws FgaInvalidParameterException When either the Store ID or Authorization Model ID is null, empty, or whitespace
     */
    public CompletableFuture<ClientWriteAssertionsResponse> writeAssertions(
            List<ClientAssertion> assertions, ClientWriteAssertionsOptions options)
            throws FgaInvalidParameterException {
        configuration.assertValid();
        String storeId = configuration.getStoreIdChecked();

        String authorizationModelId;
        if (options != null && options.hasValidAuthorizationModelId()) {
            authorizationModelId = options.getAuthorizationModelId();
        } else {
            authorizationModelId = configuration.getAuthorizationModelIdChecked();
        }

        WriteAssertionsRequest body = new WriteAssertionsRequest().assertions(ClientAssertion.asAssertions(assertions));

        var overrides = new ConfigurationOverride().addHeaders(options);

        return call(() -> api.writeAssertions(storeId, authorizationModelId, body, overrides))
                .thenApply(ClientWriteAssertionsResponse::new);
    }

    /**
     * A {@link FunctionalInterface} for calling a low-level API from {@link OpenFgaApi}. It wraps exceptions
     * encountered with {@link CompletableFuture#failedFuture(Throwable)}
     *
     * @param <R> The type of API response
     */
    @FunctionalInterface
    private interface CheckedAsyncInvocation<R> {
        CompletableFuture<R> call() throws Throwable;
    }

    private <T> CompletableFuture<T> call(CheckedAsyncInvocation<T> action) {
        try {
            return action.call();
        } catch (CompletionException completionException) {
            return CompletableFuture.failedFuture(completionException.getCause());
        } catch (Throwable throwable) {
            return CompletableFuture.failedFuture(throwable);
        }
    }

    /**
     * A {@link FunctionalInterface} for calling any function that could throw an exception.
     * It wraps exceptions encountered with {@link CompletableFuture#failedFuture(Throwable)}
     *
     * @param <R> The return type
     */
    @FunctionalInterface
    private interface CheckedInvocation<R> {
        R call() throws Throwable;
    }

    private <T> CompletableFuture<T> call(CheckedInvocation<T> action) {
        try {
            return CompletableFuture.completedFuture(action.call());
        } catch (CompletionException completionException) {
            return CompletableFuture.failedFuture(completionException.getCause());
        } catch (Throwable throwable) {
            return CompletableFuture.failedFuture(throwable);
        }
    }
}
