package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.WriteRequestDeletes;
import dev.openfga.sdk.api.model.WriteRequestWrites;
import java.util.Map;

/**
 * Options for controlling the behavior of write operations on the OpenFGA client.
 *
 * <p>Use this class to configure how tuples are written, including whether to use transactions,
 * how writes are chunked, and how duplicate or missing tuples are handled.
 */
public class ClientWriteOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String authorizationModelId;
    private boolean transactionsEnabled = true;
    private int transactionChunkSize;
    private WriteRequestWrites.OnDuplicateEnum onDuplicate;
    private WriteRequestDeletes.OnMissingEnum onMissing;

    /**
     * Sets additional HTTP headers to include in write requests.
     *
     * @param additionalHeaders a map of header names to values
     * @return this {@code ClientWriteOptions} instance for method chaining
     */
    public ClientWriteOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    /**
     * Sets the authorization model ID to use for write operations.
     *
     * <p>If not set, the client will use the authorization model ID from its configuration.
     *
     * @param authorizationModelId the authorization model ID
     * @return this {@code ClientWriteOptions} instance for method chaining
     */
    public ClientWriteOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    /**
     * Returns the authorization model ID configured for write operations.
     *
     * @return the authorization model ID, or {@code null} if not set
     */
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    /**
     * Sets whether transactions should be used when writing tuples.
     *
     * <p>When {@code true}, all writes and deletes are sent as a single atomic request — if any
     * tuple fails, the entire operation is rolled back. When {@code false}, tuples are split into
     * chunks and sent as separate requests, each chunk atomic on its own but with no atomicity
     * guarantee across the full set. Chunk size is controlled by {@link #transactionChunkSize(int)}.
     *
     * @param enabled {@code true} to enable transactions (default), {@code false} to disable them
     * @return this {@code ClientWriteOptions} instance for method chaining
     * @see #isTransactionsEnabled()
     */
    public ClientWriteOptions transactions(boolean enabled) {
        this.transactionsEnabled = enabled;
        return this;
    }

    /**
     * Returns whether transactions are enabled for write operations.
     *
     * @return {@code true} if transactions are enabled (default), {@code false} if disabled
     * @see #transactions(boolean)
     */
    public boolean isTransactionsEnabled() {
        return transactionsEnabled;
    }

    /**
     * Sets whether transactions should be disabled when writing tuples.
     *
     * @param disableTransactions {@code true} to disable transactions, {@code false} to enable them
     * @return this {@code ClientWriteOptions} instance for method chaining
     */
    public ClientWriteOptions disableTransactions(boolean disableTransactions) {
        this.transactionsEnabled = !disableTransactions;
        return this;
    }

    /**
     * Returns whether transactions are disabled for write operations.
     *
     * @return {@code true} if transactions are disabled, {@code false} if enabled (default)
     */
    public boolean disableTransactions() {
        return !transactionsEnabled;
    }

    /**
     * Sets the number of tuples to include in each non-transactional write chunk.
     *
     * <p>Only applies when transactions are disabled via {@link #transactions(boolean)}. Writes
     * and deletes are split into chunks of this size and sent as separate requests.
     *
     * @param transactionChunkSize the chunk size; must be greater than zero
     * @return this {@code ClientWriteOptions} instance for method chaining
     * @see #transactions(boolean)
     */
    public ClientWriteOptions transactionChunkSize(int transactionChunkSize) {
        this.transactionChunkSize = transactionChunkSize;
        return this;
    }

    /**
     * Returns the chunk size for non-transactional writes.
     *
     * <p>Defaults to {@code 1} if not explicitly set.
     *
     * @return the configured chunk size, or {@code 1} if none was set
     */
    public int getTransactionChunkSize() {
        return transactionChunkSize > 0 ? transactionChunkSize : 1;
    }

    /**
     * Sets the behavior when a duplicate tuple is encountered during a write.
     *
     * @param onDuplicate the action to take on duplicate tuples
     * @return this {@code ClientWriteOptions} instance for method chaining
     * @see WriteRequestWrites.OnDuplicateEnum
     */
    public ClientWriteOptions onDuplicate(WriteRequestWrites.OnDuplicateEnum onDuplicate) {
        this.onDuplicate = onDuplicate;
        return this;
    }

    /**
     * Returns the configured behavior for duplicate tuples during writes.
     *
     * @return the on-duplicate action, or {@code null} if not set
     */
    public WriteRequestWrites.OnDuplicateEnum getOnDuplicate() {
        return onDuplicate;
    }

    /**
     * Sets the behavior when a tuple to be deleted is not found.
     *
     * @param onMissing the action to take when a tuple is missing during delete
     * @return this {@code ClientWriteOptions} instance for method chaining
     * @see WriteRequestDeletes.OnMissingEnum
     */
    public ClientWriteOptions onMissing(WriteRequestDeletes.OnMissingEnum onMissing) {
        this.onMissing = onMissing;
        return this;
    }

    /**
     * Returns the configured behavior for missing tuples during deletes.
     *
     * @return the on-missing action, or {@code null} if not set
     */
    public WriteRequestDeletes.OnMissingEnum getOnMissing() {
        return onMissing;
    }
}
