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
 * RelationReference represents a relation of a particular object type (e.g. &#39;document#viewer&#39;).
 */
@JsonPropertyOrder({
    RelationReference.JSON_PROPERTY_TYPE,
    RelationReference.JSON_PROPERTY_RELATION,
    RelationReference.JSON_PROPERTY_WILDCARD
})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-25T14:11:21.475596Z[Etc/UTC]")
public class RelationReference {
    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_RELATION = "relation";
    private String relation;

    public static final String JSON_PROPERTY_WILDCARD = "wildcard";
    private Object wildcard;

    public RelationReference() {}

    public RelationReference type(String type) {
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

    public RelationReference relation(String relation) {
        this.relation = relation;
        return this;
    }

    /**
     * Get relation
     * @return relation
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RELATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getRelation() {
        return relation;
    }

    @JsonProperty(JSON_PROPERTY_RELATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRelation(String relation) {
        this.relation = relation;
    }

    public RelationReference wildcard(Object wildcard) {
        this.wildcard = wildcard;
        return this;
    }

    /**
     * Get wildcard
     * @return wildcard
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_WILDCARD)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getWildcard() {
        return wildcard;
    }

    @JsonProperty(JSON_PROPERTY_WILDCARD)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setWildcard(Object wildcard) {
        this.wildcard = wildcard;
    }

    /**
     * Return true if this RelationReference object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RelationReference relationReference = (RelationReference) o;
        return Objects.equals(this.type, relationReference.type)
                && Objects.equals(this.relation, relationReference.relation)
                && Objects.equals(this.wildcard, relationReference.wildcard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relation, wildcard);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RelationReference {\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
        sb.append("    wildcard: ").append(toIndentedString(wildcard)).append("\n");
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

        // add `relation` to the URL query string
        if (getRelation() != null) {
            joiner.add(String.format(
                    "%srelation%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getRelation()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `wildcard` to the URL query string
        if (getWildcard() != null) {
            joiner.add(String.format(
                    "%swildcard%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getWildcard()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
