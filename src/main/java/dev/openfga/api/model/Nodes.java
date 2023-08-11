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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Nodes
 */
@JsonPropertyOrder({Nodes.JSON_PROPERTY_NODES})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-11T21:07:38.017940Z[Etc/UTC]")
public class Nodes {
    public static final String JSON_PROPERTY_NODES = "nodes";
    private List<Node> nodes = new ArrayList<>();

    public Nodes() {}

    public Nodes nodes(List<Node> nodes) {
        this.nodes = nodes;
        return this;
    }

    public Nodes addNodesItem(Node nodesItem) {
        if (this.nodes == null) {
            this.nodes = new ArrayList<>();
        }
        this.nodes.add(nodesItem);
        return this;
    }

    /**
     * Get nodes
     * @return nodes
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NODES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Node> getNodes() {
        return nodes;
    }

    @JsonProperty(JSON_PROPERTY_NODES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Return true if this Nodes object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Nodes nodes = (Nodes) o;
        return Objects.equals(this.nodes, nodes.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Nodes {\n");
        sb.append("    nodes: ").append(toIndentedString(nodes)).append("\n");
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

        // add `nodes` to the URL query string
        if (getNodes() != null) {
            for (int i = 0; i < getNodes().size(); i++) {
                if (getNodes().get(i) != null) {
                    joiner.add(getNodes()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%snodes%s%s",
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
