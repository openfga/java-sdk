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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Metadata
 */
@JsonPropertyOrder({Metadata.JSON_PROPERTY_RELATIONS})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-21T20:03:24.252549Z[Etc/UTC]")
public class Metadata {
    public static final String JSON_PROPERTY_RELATIONS = "relations";
    private Map<String, RelationMetadata> relations = new HashMap<>();

    public Metadata() {}

    public Metadata relations(Map<String, RelationMetadata> relations) {
        this.relations = relations;
        return this;
    }

    public Metadata putRelationsItem(String key, RelationMetadata relationsItem) {
        if (this.relations == null) {
            this.relations = new HashMap<>();
        }
        this.relations.put(key, relationsItem);
        return this;
    }

    /**
     * Get relations
     * @return relations
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RELATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, RelationMetadata> getRelations() {
        return relations;
    }

    @JsonProperty(JSON_PROPERTY_RELATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRelations(Map<String, RelationMetadata> relations) {
        this.relations = relations;
    }

    /**
     * Return true if this Metadata object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Metadata metadata = (Metadata) o;
        return Objects.equals(this.relations, metadata.relations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relations);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Metadata {\n");
        sb.append("    relations: ").append(toIndentedString(relations)).append("\n");
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

        // add `relations` to the URL query string
        if (getRelations() != null) {
            for (String _key : getRelations().keySet()) {
                if (getRelations().get(_key) != null) {
                    joiner.add(getRelations()
                            .get(_key)
                            .toUrlQueryString(String.format(
                                    "%srelations%s%s",
                                    prefix,
                                    suffix,
                                    "".equals(suffix)
                                            ? ""
                                            : String.format("%s%d%s", containerPrefix, _key, containerSuffix))));
                }
            }
        }

        return joiner.toString();
    }
}
