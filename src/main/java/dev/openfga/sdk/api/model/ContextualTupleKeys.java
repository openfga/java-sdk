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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * ContextualTupleKeys
 */
@JsonPropertyOrder({ContextualTupleKeys.JSON_PROPERTY_TUPLE_KEYS})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-25T14:11:21.475596Z[Etc/UTC]")
public class ContextualTupleKeys {
    public static final String JSON_PROPERTY_TUPLE_KEYS = "tuple_keys";
    private List<TupleKey> tupleKeys = new ArrayList<>();

    public ContextualTupleKeys() {}

    public ContextualTupleKeys tupleKeys(List<TupleKey> tupleKeys) {
        this.tupleKeys = tupleKeys;
        return this;
    }

    public ContextualTupleKeys addTupleKeysItem(TupleKey tupleKeysItem) {
        if (this.tupleKeys == null) {
            this.tupleKeys = new ArrayList<>();
        }
        this.tupleKeys.add(tupleKeysItem);
        return this;
    }

    /**
     * Get tupleKeys
     * @return tupleKeys
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TUPLE_KEYS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<TupleKey> getTupleKeys() {
        return tupleKeys;
    }

    @JsonProperty(JSON_PROPERTY_TUPLE_KEYS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTupleKeys(List<TupleKey> tupleKeys) {
        this.tupleKeys = tupleKeys;
    }

    /**
     * Return true if this ContextualTupleKeys object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContextualTupleKeys contextualTupleKeys = (ContextualTupleKeys) o;
        return Objects.equals(this.tupleKeys, contextualTupleKeys.tupleKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKeys);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ContextualTupleKeys {\n");
        sb.append("    tupleKeys: ").append(toIndentedString(tupleKeys)).append("\n");
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

        // add `tuple_keys` to the URL query string
        if (getTupleKeys() != null) {
            for (int i = 0; i < getTupleKeys().size(); i++) {
                if (getTupleKeys().get(i) != null) {
                    joiner.add(getTupleKeys()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%stuple_keys%s%s",
                                    prefix,
                                    suffix,
                                    "".equals(suffix)
                                            ? ""
                                            : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
                }
            }
        }

        return joiner.toString();
    }
}
