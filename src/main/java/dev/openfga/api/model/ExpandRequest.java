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

package dev.openfga.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * ExpandRequest
 */
@JsonPropertyOrder({ExpandRequest.JSON_PROPERTY_TUPLE_KEY, ExpandRequest.JSON_PROPERTY_AUTHORIZATION_MODEL_ID})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-10T23:45:10.540161Z[Etc/UTC]")
public class ExpandRequest {
    public static final String JSON_PROPERTY_TUPLE_KEY = "tuple_key";
    private TupleKey tupleKey;

    public static final String JSON_PROPERTY_AUTHORIZATION_MODEL_ID = "authorization_model_id";
    private String authorizationModelId;

    public ExpandRequest() {}

    public ExpandRequest tupleKey(TupleKey tupleKey) {
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
    public TupleKey getTupleKey() {
        return tupleKey;
    }

    @JsonProperty(JSON_PROPERTY_TUPLE_KEY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTupleKey(TupleKey tupleKey) {
        this.tupleKey = tupleKey;
    }

    public ExpandRequest authorizationModelId(String authorizationModelId) {
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
     * Return true if this Expand_request object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpandRequest expandRequest = (ExpandRequest) o;
        return Objects.equals(this.tupleKey, expandRequest.tupleKey)
                && Objects.equals(this.authorizationModelId, expandRequest.authorizationModelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, authorizationModelId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ExpandRequest {\n");
        sb.append("    tupleKey: ").append(toIndentedString(tupleKey)).append("\n");
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

        // add `tuple_key` to the URL query string
        if (getTupleKey() != null) {
            joiner.add(getTupleKey().toUrlQueryString(prefix + "tuple_key" + suffix));
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
