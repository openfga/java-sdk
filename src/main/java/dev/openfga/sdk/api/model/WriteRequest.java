/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 0.1
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
 * WriteRequest
 */
@JsonPropertyOrder({
    WriteRequest.JSON_PROPERTY_WRITES,
    WriteRequest.JSON_PROPERTY_DELETES,
    WriteRequest.JSON_PROPERTY_AUTHORIZATION_MODEL_ID
})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-23T20:06:14.249201Z[Etc/UTC]")
public class WriteRequest {
    public static final String JSON_PROPERTY_WRITES = "writes";
    private TupleKeys writes;

    public static final String JSON_PROPERTY_DELETES = "deletes";
    private TupleKeys deletes;

    public static final String JSON_PROPERTY_AUTHORIZATION_MODEL_ID = "authorization_model_id";
    private String authorizationModelId;

    public WriteRequest() {}

    public WriteRequest writes(TupleKeys writes) {
        this.writes = writes;
        return this;
    }

    /**
     * Get writes
     * @return writes
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_WRITES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public TupleKeys getWrites() {
        return writes;
    }

    @JsonProperty(JSON_PROPERTY_WRITES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setWrites(TupleKeys writes) {
        this.writes = writes;
    }

    public WriteRequest deletes(TupleKeys deletes) {
        this.deletes = deletes;
        return this;
    }

    /**
     * Get deletes
     * @return deletes
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DELETES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public TupleKeys getDeletes() {
        return deletes;
    }

    @JsonProperty(JSON_PROPERTY_DELETES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDeletes(TupleKeys deletes) {
        this.deletes = deletes;
    }

    public WriteRequest authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    /**
     * Get authorizationModelId
     * @return authorizationModelId
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_AUTHORIZATION_MODEL_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @JsonProperty(JSON_PROPERTY_AUTHORIZATION_MODEL_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAuthorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
    }

    /**
     * Return true if this Write_request object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WriteRequest writeRequest = (WriteRequest) o;
        return Objects.equals(this.writes, writeRequest.writes)
                && Objects.equals(this.deletes, writeRequest.deletes)
                && Objects.equals(this.authorizationModelId, writeRequest.authorizationModelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(writes, deletes, authorizationModelId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WriteRequest {\n");
        sb.append("    writes: ").append(toIndentedString(writes)).append("\n");
        sb.append("    deletes: ").append(toIndentedString(deletes)).append("\n");
        sb.append("    authorizationModelId: ")
                .append(toIndentedString(authorizationModelId))
                .append("\n");
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

        // add `writes` to the URL query string
        if (getWrites() != null) {
            joiner.add(getWrites().toUrlQueryString(prefix + "writes" + suffix));
        }

        // add `deletes` to the URL query string
        if (getDeletes() != null) {
            joiner.add(getDeletes().toUrlQueryString(prefix + "deletes" + suffix));
        }

        // add `authorization_model_id` to the URL query string
        if (getAuthorizationModelId() != null) {
            joiner.add(String.format(
                    "%sauthorization_model_id%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getAuthorizationModelId()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
