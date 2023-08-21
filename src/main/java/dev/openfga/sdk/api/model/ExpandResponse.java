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
import java.util.Objects;
import java.util.StringJoiner;

/**
 * ExpandResponse
 */
@JsonPropertyOrder({ExpandResponse.JSON_PROPERTY_TREE})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-25T14:11:21.475596Z[Etc/UTC]")
public class ExpandResponse {
    public static final String JSON_PROPERTY_TREE = "tree";
    private UsersetTree tree;

    public ExpandResponse() {}

    public ExpandResponse tree(UsersetTree tree) {
        this.tree = tree;
        return this;
    }

    /**
     * Get tree
     * @return tree
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TREE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public UsersetTree getTree() {
        return tree;
    }

    @JsonProperty(JSON_PROPERTY_TREE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTree(UsersetTree tree) {
        this.tree = tree;
    }

    /**
     * Return true if this ExpandResponse object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpandResponse expandResponse = (ExpandResponse) o;
        return Objects.equals(this.tree, expandResponse.tree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tree);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ExpandResponse {\n");
        sb.append("    tree: ").append(toIndentedString(tree)).append("\n");
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

        // add `tree` to the URL query string
        if (getTree() != null) {
            joiner.add(getTree().toUrlQueryString(prefix + "tree" + suffix));
        }

        return joiner.toString();
    }
}
