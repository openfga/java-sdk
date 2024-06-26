/*
 * OpenFGA
 * A high performance and flexible authorization/permission engine built for developers and inspired by Google Zanzibar.
 *
 * The version of the OpenAPI document: 1.x
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
 * Userset
 */
@JsonPropertyOrder({
    Userset.JSON_PROPERTY_THIS,
    Userset.JSON_PROPERTY_COMPUTED_USERSET,
    Userset.JSON_PROPERTY_TUPLE_TO_USERSET,
    Userset.JSON_PROPERTY_UNION,
    Userset.JSON_PROPERTY_INTERSECTION,
    Userset.JSON_PROPERTY_DIFFERENCE
})
public class Userset {
    public static final String JSON_PROPERTY_THIS = "this";
    private Object _this;

    public static final String JSON_PROPERTY_COMPUTED_USERSET = "computedUserset";
    private ObjectRelation computedUserset;

    public static final String JSON_PROPERTY_TUPLE_TO_USERSET = "tupleToUserset";
    private TupleToUserset tupleToUserset;

    public static final String JSON_PROPERTY_UNION = "union";
    private Usersets union;

    public static final String JSON_PROPERTY_INTERSECTION = "intersection";
    private Usersets intersection;

    public static final String JSON_PROPERTY_DIFFERENCE = "difference";
    private Difference difference;

    public Userset() {}

    public Userset _this(Object _this) {
        this._this = _this;
        return this;
    }

    /**
     * A DirectUserset is a sentinel message for referencing the direct members specified by an object/relation mapping.
     * @return _this
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_THIS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getThis() {
        return _this;
    }

    @JsonProperty(JSON_PROPERTY_THIS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setThis(Object _this) {
        this._this = _this;
    }

    public Userset computedUserset(ObjectRelation computedUserset) {
        this.computedUserset = computedUserset;
        return this;
    }

    /**
     * Get computedUserset
     * @return computedUserset
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COMPUTED_USERSET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public ObjectRelation getComputedUserset() {
        return computedUserset;
    }

    @JsonProperty(JSON_PROPERTY_COMPUTED_USERSET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setComputedUserset(ObjectRelation computedUserset) {
        this.computedUserset = computedUserset;
    }

    public Userset tupleToUserset(TupleToUserset tupleToUserset) {
        this.tupleToUserset = tupleToUserset;
        return this;
    }

    /**
     * Get tupleToUserset
     * @return tupleToUserset
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TUPLE_TO_USERSET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public TupleToUserset getTupleToUserset() {
        return tupleToUserset;
    }

    @JsonProperty(JSON_PROPERTY_TUPLE_TO_USERSET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTupleToUserset(TupleToUserset tupleToUserset) {
        this.tupleToUserset = tupleToUserset;
    }

    public Userset union(Usersets union) {
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
    public Usersets getUnion() {
        return union;
    }

    @JsonProperty(JSON_PROPERTY_UNION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUnion(Usersets union) {
        this.union = union;
    }

    public Userset intersection(Usersets intersection) {
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
    public Usersets getIntersection() {
        return intersection;
    }

    @JsonProperty(JSON_PROPERTY_INTERSECTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setIntersection(Usersets intersection) {
        this.intersection = intersection;
    }

    public Userset difference(Difference difference) {
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
    public Difference getDifference() {
        return difference;
    }

    @JsonProperty(JSON_PROPERTY_DIFFERENCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDifference(Difference difference) {
        this.difference = difference;
    }

    /**
     * Return true if this Userset object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Userset userset = (Userset) o;
        return Objects.equals(this._this, userset._this)
                && Objects.equals(this.computedUserset, userset.computedUserset)
                && Objects.equals(this.tupleToUserset, userset.tupleToUserset)
                && Objects.equals(this.union, userset.union)
                && Objects.equals(this.intersection, userset.intersection)
                && Objects.equals(this.difference, userset.difference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_this, computedUserset, tupleToUserset, union, intersection, difference);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Userset {\n");
        sb.append("    _this: ").append(toIndentedString(_this)).append("\n");
        sb.append("    computedUserset: ")
                .append(toIndentedString(computedUserset))
                .append("\n");
        sb.append("    tupleToUserset: ")
                .append(toIndentedString(tupleToUserset))
                .append("\n");
        sb.append("    union: ").append(toIndentedString(union)).append("\n");
        sb.append("    intersection: ").append(toIndentedString(intersection)).append("\n");
        sb.append("    difference: ").append(toIndentedString(difference)).append("\n");
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

        // add `this` to the URL query string
        if (getThis() != null) {
            joiner.add(String.format(
                    "%sthis%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getThis()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `computedUserset` to the URL query string
        if (getComputedUserset() != null) {
            joiner.add(getComputedUserset().toUrlQueryString(prefix + "computedUserset" + suffix));
        }

        // add `tupleToUserset` to the URL query string
        if (getTupleToUserset() != null) {
            joiner.add(getTupleToUserset().toUrlQueryString(prefix + "tupleToUserset" + suffix));
        }

        // add `union` to the URL query string
        if (getUnion() != null) {
            joiner.add(getUnion().toUrlQueryString(prefix + "union" + suffix));
        }

        // add `intersection` to the URL query string
        if (getIntersection() != null) {
            joiner.add(getIntersection().toUrlQueryString(prefix + "intersection" + suffix));
        }

        // add `difference` to the URL query string
        if (getDifference() != null) {
            joiner.add(getDifference().toUrlQueryString(prefix + "difference" + suffix));
        }

        return joiner.toString();
    }
}
