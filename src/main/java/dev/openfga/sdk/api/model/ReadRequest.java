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
 * ReadRequest
 */
@JsonPropertyOrder({
    ReadRequest.JSON_PROPERTY_TUPLE_KEY,
    ReadRequest.JSON_PROPERTY_PAGE_SIZE,
    ReadRequest.JSON_PROPERTY_CONTINUATION_TOKEN,
    ReadRequest.JSON_PROPERTY_CONSISTENCY
})
public class ReadRequest {
    public static final String JSON_PROPERTY_TUPLE_KEY = "tuple_key";
    private ReadRequestTupleKey tupleKey;

    public static final String JSON_PROPERTY_PAGE_SIZE = "page_size";
    private Integer pageSize;

    public static final String JSON_PROPERTY_CONTINUATION_TOKEN = "continuation_token";
    private String continuationToken;

    public static final String JSON_PROPERTY_CONSISTENCY = "consistency";
    private ConsistencyPreference consistency = ConsistencyPreference.UNSPECIFIED;

    public ReadRequest() {}

    public ReadRequest tupleKey(ReadRequestTupleKey tupleKey) {
        this.tupleKey = tupleKey;
        return this;
    }

    /**
     * Get tupleKey
     * @return tupleKey
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TUPLE_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public ReadRequestTupleKey getTupleKey() {
        return tupleKey;
    }

    @JsonProperty(JSON_PROPERTY_TUPLE_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTupleKey(ReadRequestTupleKey tupleKey) {
        this.tupleKey = tupleKey;
    }

    public ReadRequest pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Get pageSize
     * minimum: 1
     * maximum: 100
     * @return pageSize
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PAGE_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getPageSize() {
        return pageSize;
    }

    @JsonProperty(JSON_PROPERTY_PAGE_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public ReadRequest continuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
        return this;
    }

    /**
     * Get continuationToken
     * @return continuationToken
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTINUATION_TOKEN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getContinuationToken() {
        return continuationToken;
    }

    @JsonProperty(JSON_PROPERTY_CONTINUATION_TOKEN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public ReadRequest consistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
        return this;
    }

    /**
     * Get consistency
     * @return consistency
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONSISTENCY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    @JsonProperty(JSON_PROPERTY_CONSISTENCY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setConsistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
    }

    /**
     * Return true if this Read_request object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReadRequest readRequest = (ReadRequest) o;
        return Objects.equals(this.tupleKey, readRequest.tupleKey)
                && Objects.equals(this.pageSize, readRequest.pageSize)
                && Objects.equals(this.continuationToken, readRequest.continuationToken)
                && Objects.equals(this.consistency, readRequest.consistency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, pageSize, continuationToken, consistency);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReadRequest {\n");
        sb.append("    tupleKey: ").append(toIndentedString(tupleKey)).append("\n");
        sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
        sb.append("    continuationToken: ")
                .append(toIndentedString(continuationToken))
                .append("\n");
        sb.append("    consistency: ").append(toIndentedString(consistency)).append("\n");
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

        // add `page_size` to the URL query string
        if (getPageSize() != null) {
            joiner.add(String.format(
                    "%spage_size%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getPageSize()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `continuation_token` to the URL query string
        if (getContinuationToken() != null) {
            joiner.add(String.format(
                    "%scontinuation_token%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getContinuationToken()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `consistency` to the URL query string
        if (getConsistency() != null) {
            joiner.add(String.format(
                    "%sconsistency%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getConsistency()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
