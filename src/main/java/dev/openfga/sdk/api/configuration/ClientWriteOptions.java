package dev.openfga.sdk.api.configuration;

import dev.openfga.sdk.api.model.WriteRequestDeletes;
import dev.openfga.sdk.api.model.WriteRequestWrites;
import java.util.Map;

public class ClientWriteOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String authorizationModelId;
    private Boolean disableTransactions = false;
    private int transactionChunkSize;
    private WriteRequestWrites.OnDuplicateEnum onDuplicate;
    private WriteRequestDeletes.OnMissingEnum onMissing;

    public ClientWriteOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public ClientWriteOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    /**
     * Sets whether transactions should be used when writing tuples.
     *
     * <p>When {@code true}, writes are sent as a single transactional request. When {@code false},
     * writes are split into chunks and sent as individual non-transactional requests, with chunk
     * size controlled by {@link #transactionChunkSize(int)}.
     *
     * @param enabled {@code true} to enable transactions (default), {@code false} to disable them
     * @return this {@code ClientWriteOptions} instance for method chaining
     * @see #isTransactionsEnabled()
     */
    public ClientWriteOptions transactions(boolean enabled) {
        this.disableTransactions = !enabled;
        return this;
    }

    /**
     * Returns whether transactions are enabled for write operations.
     *
     * @return {@code true} if transactions are enabled (default), {@code false} if disabled
     * @see #transactions(boolean)
     */
    public boolean isTransactionsEnabled() {
        return disableTransactions == null || !disableTransactions;
    }

    /**
     * Sets whether transactions should be disabled when writing tuples.
     *
     * @param disableTransactions {@code true} to disable transactions, {@code false} to enable them
     * @return this {@code ClientWriteOptions} instance for method chaining
     * @deprecated Use {@link #transactions(boolean)} instead. This method will be removed in a
     *     future release. Replace {@code disableTransactions(true)} with
     *     {@code transactions(false)}, and {@code disableTransactions(false)} with
     *     {@code transactions(true)}.
     */
    @Deprecated
    public ClientWriteOptions disableTransactions(boolean disableTransactions) {
        this.disableTransactions = disableTransactions;
        return this;
    }

    /**
     * Returns whether transactions are disabled for write operations.
     *
     * @return {@code true} if transactions are disabled, {@code false} if enabled (default)
     * @deprecated Use {@link #isTransactionsEnabled()} instead. This method will be removed in a
     *     future release. Note that {@code isTransactionsEnabled()} returns the inverse of
     *     this method.
     */
    @Deprecated
    public boolean disableTransactions() {
        return disableTransactions != null && disableTransactions;
    }

    public ClientWriteOptions transactionChunkSize(int transactionChunkSize) {
        this.transactionChunkSize = transactionChunkSize;
        return this;
    }

    public int getTransactionChunkSize() {
        return transactionChunkSize > 0 ? transactionChunkSize : 1;
    }

    public ClientWriteOptions onDuplicate(WriteRequestWrites.OnDuplicateEnum onDuplicate) {
        this.onDuplicate = onDuplicate;
        return this;
    }

    public WriteRequestWrites.OnDuplicateEnum getOnDuplicate() {
        return onDuplicate;
    }

    public ClientWriteOptions onMissing(WriteRequestDeletes.OnMissingEnum onMissing) {
        this.onMissing = onMissing;
        return this;
    }

    public WriteRequestDeletes.OnMissingEnum getOnMissing() {
        return onMissing;
    }
}
