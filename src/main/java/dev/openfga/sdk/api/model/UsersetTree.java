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
 * A UsersetTree contains the result of an Expansion.
 */
@JsonPropertyOrder({UsersetTree.JSON_PROPERTY_ROOT})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-23T01:25:17.613607Z[Etc/UTC]")
public class UsersetTree {
    public static final String JSON_PROPERTY_ROOT = "root";
    private Node root;

    public UsersetTree() {}

    public UsersetTree root(Node root) {
        this.root = root;
        return this;
    }

    /**
     * Get root
     * @return root
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ROOT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Node getRoot() {
        return root;
    }

    @JsonProperty(JSON_PROPERTY_ROOT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * Return true if this UsersetTree object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UsersetTree usersetTree = (UsersetTree) o;
        return Objects.equals(this.root, usersetTree.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UsersetTree {\n");
        sb.append("    root: ").append(toIndentedString(root)).append("\n");
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

        // add `root` to the URL query string
        if (getRoot() != null) {
            joiner.add(getRoot().toUrlQueryString(prefix + "root" + suffix));
        }

        return joiner.toString();
    }
}
