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
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Difference
 */
@JsonPropertyOrder({Difference.JSON_PROPERTY_BASE, Difference.JSON_PROPERTY_SUBTRACT})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-11T21:23:49.205789Z[Etc/UTC]")
public class Difference {
    public static final String JSON_PROPERTY_BASE = "base";
    private Userset base;

    public static final String JSON_PROPERTY_SUBTRACT = "subtract";
    private Userset subtract;

    public Difference() {}

    public Difference base(Userset base) {
        this.base = base;
        return this;
    }

    /**
     * Get base
     * @return base
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Userset getBase() {
        return base;
    }

    @JsonProperty(JSON_PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setBase(Userset base) {
        this.base = base;
    }

    public Difference subtract(Userset subtract) {
        this.subtract = subtract;
        return this;
    }

    /**
     * Get subtract
     * @return subtract
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SUBTRACT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Userset getSubtract() {
        return subtract;
    }

    @JsonProperty(JSON_PROPERTY_SUBTRACT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setSubtract(Userset subtract) {
        this.subtract = subtract;
    }

    /**
     * Return true if this Difference object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Difference difference = (Difference) o;
        return Objects.equals(this.base, difference.base) && Objects.equals(this.subtract, difference.subtract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, subtract);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Difference {\n");
        sb.append("    base: ").append(toIndentedString(base)).append("\n");
        sb.append("    subtract: ").append(toIndentedString(subtract)).append("\n");
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

        // add `base` to the URL query string
        if (getBase() != null) {
            joiner.add(getBase().toUrlQueryString(prefix + "base" + suffix));
        }

        // add `subtract` to the URL query string
        if (getSubtract() != null) {
            joiner.add(getSubtract().toUrlQueryString(prefix + "subtract" + suffix));
        }

        return joiner.toString();
    }
}
