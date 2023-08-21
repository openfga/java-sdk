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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * TypeDefinition
 */
@JsonPropertyOrder({
    TypeDefinition.JSON_PROPERTY_TYPE,
    TypeDefinition.JSON_PROPERTY_RELATIONS,
    TypeDefinition.JSON_PROPERTY_METADATA
})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-21T20:03:24.252549Z[Etc/UTC]")
public class TypeDefinition {
    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_RELATIONS = "relations";
    private Map<String, Userset> relations = new HashMap<>();

    public static final String JSON_PROPERTY_METADATA = "metadata";
    private Metadata metadata;

    public TypeDefinition() {}

    public TypeDefinition type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     * @return type
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(String type) {
        this.type = type;
    }

    public TypeDefinition relations(Map<String, Userset> relations) {
        this.relations = relations;
        return this;
    }

    public TypeDefinition putRelationsItem(String key, Userset relationsItem) {
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
    public Map<String, Userset> getRelations() {
        return relations;
    }

    @JsonProperty(JSON_PROPERTY_RELATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRelations(Map<String, Userset> relations) {
        this.relations = relations;
    }

    public TypeDefinition metadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Get metadata
     * @return metadata
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Metadata getMetadata() {
        return metadata;
    }

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Return true if this TypeDefinition object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeDefinition typeDefinition = (TypeDefinition) o;
        return Objects.equals(this.type, typeDefinition.type)
                && Objects.equals(this.relations, typeDefinition.relations)
                && Objects.equals(this.metadata, typeDefinition.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relations, metadata);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TypeDefinition {\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    relations: ").append(toIndentedString(relations)).append("\n");
        sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
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

        // add `type` to the URL query string
        if (getType() != null) {
            joiner.add(String.format(
                    "%stype%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getType()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

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

        // add `metadata` to the URL query string
        if (getMetadata() != null) {
            joiner.add(getMetadata().toUrlQueryString(prefix + "metadata" + suffix));
        }

        return joiner.toString();
    }
}
