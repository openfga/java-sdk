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
 * Node
 */
@JsonPropertyOrder({
    Node.JSON_PROPERTY_NAME,
    Node.JSON_PROPERTY_LEAF,
    Node.JSON_PROPERTY_DIFFERENCE,
    Node.JSON_PROPERTY_UNION,
    Node.JSON_PROPERTY_INTERSECTION
})
public class Node {
    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_LEAF = "leaf";
    private Leaf leaf;

    public static final String JSON_PROPERTY_DIFFERENCE = "difference";
    private UsersetTreeDifference difference;

    public static final String JSON_PROPERTY_UNION = "union";
    private Nodes union;

    public static final String JSON_PROPERTY_INTERSECTION = "intersection";
    private Nodes intersection;

    public Node() {}

    public Node name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     * @return name
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setName(String name) {
        this.name = name;
    }

    public Node leaf(Leaf leaf) {
        this.leaf = leaf;
        return this;
    }

    /**
     * Get leaf
     * @return leaf
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LEAF)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Leaf getLeaf() {
        return leaf;
    }

    @JsonProperty(JSON_PROPERTY_LEAF)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLeaf(Leaf leaf) {
        this.leaf = leaf;
    }

    public Node difference(UsersetTreeDifference difference) {
        this.difference = difference;
        return this;
    }

    /**
     * Get difference
     * @return difference
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DIFFERENCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public UsersetTreeDifference getDifference() {
        return difference;
    }

    @JsonProperty(JSON_PROPERTY_DIFFERENCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDifference(UsersetTreeDifference difference) {
        this.difference = difference;
    }

    public Node union(Nodes union) {
        this.union = union;
        return this;
    }

    /**
     * Get union
     * @return union
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UNION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Nodes getUnion() {
        return union;
    }

    @JsonProperty(JSON_PROPERTY_UNION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUnion(Nodes union) {
        this.union = union;
    }

    public Node intersection(Nodes intersection) {
        this.intersection = intersection;
        return this;
    }

    /**
     * Get intersection
     * @return intersection
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_INTERSECTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Nodes getIntersection() {
        return intersection;
    }

    @JsonProperty(JSON_PROPERTY_INTERSECTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setIntersection(Nodes intersection) {
        this.intersection = intersection;
    }

    /**
     * Return true if this Node object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return Objects.equals(this.name, node.name)
                && Objects.equals(this.leaf, node.leaf)
                && Objects.equals(this.difference, node.difference)
                && Objects.equals(this.union, node.union)
                && Objects.equals(this.intersection, node.intersection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, leaf, difference, union, intersection);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Node {\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    leaf: ").append(toIndentedString(leaf)).append("\n");
        sb.append("    difference: ").append(toIndentedString(difference)).append("\n");
        sb.append("    union: ").append(toIndentedString(union)).append("\n");
        sb.append("    intersection: ").append(toIndentedString(intersection)).append("\n");
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

        // add `name` to the URL query string
        if (getName() != null) {
            joiner.add(String.format(
                    "%sname%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getName()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `leaf` to the URL query string
        if (getLeaf() != null) {
            joiner.add(getLeaf().toUrlQueryString(prefix + "leaf" + suffix));
        }

        // add `difference` to the URL query string
        if (getDifference() != null) {
            joiner.add(getDifference().toUrlQueryString(prefix + "difference" + suffix));
        }

        // add `union` to the URL query string
        if (getUnion() != null) {
            joiner.add(getUnion().toUrlQueryString(prefix + "union" + suffix));
        }

        // add `intersection` to the URL query string
        if (getIntersection() != null) {
            joiner.add(getIntersection().toUrlQueryString(prefix + "intersection" + suffix));
        }

        return joiner.toString();
    }
}
