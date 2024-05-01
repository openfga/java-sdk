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
 * User.  Represents any possible value for a user (subject or principal). Can be a: - Specific user object e.g.: &#39;user:will&#39;, &#39;folder:marketing&#39;, &#39;org:contoso&#39;, ...) - Specific userset (e.g. &#39;group:engineering#member&#39;) - Public-typed wildcard (e.g. &#39;user:*&#39;)  See https://openfga.dev/docs/concepts#what-is-a-user
 */
@JsonPropertyOrder({User.JSON_PROPERTY_OBJECT, User.JSON_PROPERTY_USERSET, User.JSON_PROPERTY_WILDCARD})
public class User {
    public static final String JSON_PROPERTY_OBJECT = "object";
    private FgaObject _object;

    public static final String JSON_PROPERTY_USERSET = "userset";
    private UsersetUser userset;

    public static final String JSON_PROPERTY_WILDCARD = "wildcard";
    private TypedWildcard wildcard;

    public User() {}

    public User _object(FgaObject _object) {
        this._object = _object;
        return this;
    }

    /**
     * Get _object
     * @return _object
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OBJECT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public FgaObject getObject() {
        return _object;
    }

    @JsonProperty(JSON_PROPERTY_OBJECT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setObject(FgaObject _object) {
        this._object = _object;
    }

    public User userset(UsersetUser userset) {
        this.userset = userset;
        return this;
    }

    /**
     * Get userset
     * @return userset
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_USERSET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public UsersetUser getUserset() {
        return userset;
    }

    @JsonProperty(JSON_PROPERTY_USERSET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUserset(UsersetUser userset) {
        this.userset = userset;
    }

    public User wildcard(TypedWildcard wildcard) {
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
    public TypedWildcard getWildcard() {
        return wildcard;
    }

    @JsonProperty(JSON_PROPERTY_WILDCARD)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setWildcard(TypedWildcard wildcard) {
        this.wildcard = wildcard;
    }

    /**
     * Return true if this User object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(this._object, user._object)
                && Objects.equals(this.userset, user.userset)
                && Objects.equals(this.wildcard, user.wildcard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_object, userset, wildcard);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class User {\n");
        sb.append("    _object: ").append(toIndentedString(_object)).append("\n");
        sb.append("    userset: ").append(toIndentedString(userset)).append("\n");
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

        // add `object` to the URL query string
        if (getObject() != null) {
            joiner.add(getObject().toUrlQueryString(prefix + "object" + suffix));
        }

        // add `userset` to the URL query string
        if (getUserset() != null) {
            joiner.add(getUserset().toUrlQueryString(prefix + "userset" + suffix));
        }

        // add `wildcard` to the URL query string
        if (getWildcard() != null) {
            joiner.add(getWildcard().toUrlQueryString(prefix + "wildcard" + suffix));
        }

        return joiner.toString();
    }
}
