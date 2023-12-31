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
 * RelationMetadata
 */
@JsonPropertyOrder({RelationMetadata.JSON_PROPERTY_DIRECTLY_RELATED_USER_TYPES})
public class RelationMetadata {
    public static final String JSON_PROPERTY_DIRECTLY_RELATED_USER_TYPES = "directly_related_user_types";
    private List<RelationReference> directlyRelatedUserTypes = new ArrayList<>();

    public RelationMetadata() {}

    public RelationMetadata directlyRelatedUserTypes(List<RelationReference> directlyRelatedUserTypes) {
        this.directlyRelatedUserTypes = directlyRelatedUserTypes;
        return this;
    }

    public RelationMetadata addDirectlyRelatedUserTypesItem(RelationReference directlyRelatedUserTypesItem) {
        if (this.directlyRelatedUserTypes == null) {
            this.directlyRelatedUserTypes = new ArrayList<>();
        }
        this.directlyRelatedUserTypes.add(directlyRelatedUserTypesItem);
        return this;
    }

    /**
     * Get directlyRelatedUserTypes
     * @return directlyRelatedUserTypes
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DIRECTLY_RELATED_USER_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<RelationReference> getDirectlyRelatedUserTypes() {
        return directlyRelatedUserTypes;
    }

    @JsonProperty(JSON_PROPERTY_DIRECTLY_RELATED_USER_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDirectlyRelatedUserTypes(List<RelationReference> directlyRelatedUserTypes) {
        this.directlyRelatedUserTypes = directlyRelatedUserTypes;
    }

    /**
     * Return true if this RelationMetadata object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RelationMetadata relationMetadata = (RelationMetadata) o;
        return Objects.equals(this.directlyRelatedUserTypes, relationMetadata.directlyRelatedUserTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directlyRelatedUserTypes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RelationMetadata {\n");
        sb.append("    directlyRelatedUserTypes: ")
                .append(toIndentedString(directlyRelatedUserTypes))
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

        // add `directly_related_user_types` to the URL query string
        if (getDirectlyRelatedUserTypes() != null) {
            for (int i = 0; i < getDirectlyRelatedUserTypes().size(); i++) {
                if (getDirectlyRelatedUserTypes().get(i) != null) {
                    joiner.add(getDirectlyRelatedUserTypes()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%sdirectly_related_user_types%s%s",
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
