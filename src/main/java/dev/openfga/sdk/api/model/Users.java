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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Users
 */
@JsonPropertyOrder({Users.JSON_PROPERTY_USERS})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-22T23:54:43.912230Z[Etc/UTC]")
public class Users {
    public static final String JSON_PROPERTY_USERS = "users";
    private List<String> users = new ArrayList<>();

    public Users() {}

    public Users users(List<String> users) {
        this.users = users;
        return this;
    }

    public Users addUsersItem(String usersItem) {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        this.users.add(usersItem);
        return this;
    }

    /**
     * Get users
     * @return users
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_USERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getUsers() {
        return users;
    }

    @JsonProperty(JSON_PROPERTY_USERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUsers(List<String> users) {
        this.users = users;
    }

    /**
     * Return true if this Users object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Users users = (Users) o;
        return Objects.equals(this.users, users.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Users {\n");
        sb.append("    users: ").append(toIndentedString(users)).append("\n");
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

        // add `users` to the URL query string
        if (getUsers() != null) {
            for (int i = 0; i < getUsers().size(); i++) {
                joiner.add(String.format(
                        "%susers%s%s=%s",
                        prefix,
                        suffix,
                        "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix),
                        URLEncoder.encode(String.valueOf(getUsers().get(i)), StandardCharsets.UTF_8)
                                .replaceAll("\\+", "%20")));
            }
        }

        return joiner.toString();
    }
}
