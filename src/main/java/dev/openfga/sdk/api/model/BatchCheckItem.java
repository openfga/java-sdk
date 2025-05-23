/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 1.x
 * Contact: community@openfga.dev
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package dev.openfga.sdk.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * BatchCheckItem
 */
@JsonPropertyOrder({
    BatchCheckItem.JSON_PROPERTY_TUPLE_KEY,
    BatchCheckItem.JSON_PROPERTY_CONTEXTUAL_TUPLES,
    BatchCheckItem.JSON_PROPERTY_CONTEXT,
    BatchCheckItem.JSON_PROPERTY_CORRELATION_ID
})
public class BatchCheckItem {
    public static final String JSON_PROPERTY_TUPLE_KEY = "tuple_key";
    private CheckRequestTupleKey tupleKey;

    public static final String JSON_PROPERTY_CONTEXTUAL_TUPLES = "contextual_tuples";
    private ContextualTupleKeys contextualTuples;

    public static final String JSON_PROPERTY_CONTEXT = "context";
    private Object context;

    public static final String JSON_PROPERTY_CORRELATION_ID = "correlation_id";
    private String correlationId;

    public BatchCheckItem() {}

    public BatchCheckItem tupleKey(CheckRequestTupleKey tupleKey) {
        this.tupleKey = tupleKey;
        return this;
    }

    /**
     * Get tupleKey
     * @return tupleKey
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TUPLE_KEY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public CheckRequestTupleKey getTupleKey() {
        return tupleKey;
    }

    @JsonProperty(JSON_PROPERTY_TUPLE_KEY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTupleKey(CheckRequestTupleKey tupleKey) {
        this.tupleKey = tupleKey;
    }

    public BatchCheckItem contextualTuples(ContextualTupleKeys contextualTuples) {
        this.contextualTuples = contextualTuples;
        return this;
    }

    /**
     * Get contextualTuples
     * @return contextualTuples
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTEXTUAL_TUPLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public ContextualTupleKeys getContextualTuples() {
        return contextualTuples;
    }

    @JsonProperty(JSON_PROPERTY_CONTEXTUAL_TUPLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContextualTuples(ContextualTupleKeys contextualTuples) {
        this.contextualTuples = contextualTuples;
    }

    public BatchCheckItem context(Object context) {
        this.context = context;
        return this;
    }

    /**
     * Get context
     * @return context
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTEXT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getContext() {
        return context;
    }

    @JsonProperty(JSON_PROPERTY_CONTEXT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContext(Object context) {
        this.context = context;
    }

    public BatchCheckItem correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    /**
     * correlation_id must be a string containing only letters, numbers, or hyphens, with length ≤ 36 characters.
     * @return correlationId
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CORRELATION_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getCorrelationId() {
        return correlationId;
    }

    @JsonProperty(JSON_PROPERTY_CORRELATION_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Return true if this BatchCheckItem object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BatchCheckItem batchCheckItem = (BatchCheckItem) o;
        return Objects.equals(this.tupleKey, batchCheckItem.tupleKey)
                && Objects.equals(this.contextualTuples, batchCheckItem.contextualTuples)
                && Objects.equals(this.context, batchCheckItem.context)
                && Objects.equals(this.correlationId, batchCheckItem.correlationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, contextualTuples, context, correlationId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BatchCheckItem {\n");
        sb.append("    tupleKey: ").append(toIndentedString(tupleKey)).append("\n");
        sb.append("    contextualTuples: ")
                .append(toIndentedString(contextualTuples))
                .append("\n");
        sb.append("    context: ").append(toIndentedString(context)).append("\n");
        sb.append("    correlationId: ").append(toIndentedString(correlationId)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * Convert the instance into URL query string.
     *
     * @return URL query string
     */
    public String toUrlQueryString() {
        return toUrlQueryString(null);
    }

    /**
     * Convert the instance into URL query string.
     *
     * @param prefix prefix of the query string
     * @return URL query string
     */
    public String toUrlQueryString(String prefix) {
        String suffix = "";
        String containerSuffix = "";
        String containerPrefix = "";
        if (prefix == null) {
            // style=form, explode=true, e.g. /pet?name=cat&type=manx
            prefix = "";
        } else {
            // deepObject style e.g. /pet?id[name]=cat&id[type]=manx
            prefix = prefix + "[";
            suffix = "]";
            containerSuffix = "]";
            containerPrefix = "[";
        }

        StringJoiner joiner = new StringJoiner("&");

        // add `tuple_key` to the URL query string
        if (getTupleKey() != null) {
            joiner.add(getTupleKey().toUrlQueryString(prefix + "tuple_key" + suffix));
        }

        // add `contextual_tuples` to the URL query string
        if (getContextualTuples() != null) {
            joiner.add(getContextualTuples().toUrlQueryString(prefix + "contextual_tuples" + suffix));
        }

        // add `context` to the URL query string
        if (getContext() != null) {
            joiner.add(String.format(
                    "%scontext%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getContext()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `correlation_id` to the URL query string
        if (getCorrelationId() != null) {
            joiner.add(String.format(
                    "%scorrelation_id%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getCorrelationId()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
