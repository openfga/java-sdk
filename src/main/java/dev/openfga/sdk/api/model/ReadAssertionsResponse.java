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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * ReadAssertionsResponse
 */
@JsonPropertyOrder({
    ReadAssertionsResponse.JSON_PROPERTY_AUTHORIZATION_MODEL_ID,
    ReadAssertionsResponse.JSON_PROPERTY_ASSERTIONS
})
public class ReadAssertionsResponse {
    public static final String JSON_PROPERTY_AUTHORIZATION_MODEL_ID = "authorization_model_id";
    private String authorizationModelId;

    public static final String JSON_PROPERTY_ASSERTIONS = "assertions";
    private List<Assertion> assertions = new ArrayList<>();

    public ReadAssertionsResponse() {}

    public ReadAssertionsResponse authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    /**
     * Get authorizationModelId
     * @return authorizationModelId
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_AUTHORIZATION_MODEL_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @JsonProperty(JSON_PROPERTY_AUTHORIZATION_MODEL_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setAuthorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
    }

    public ReadAssertionsResponse assertions(List<Assertion> assertions) {
        this.assertions = assertions;
        return this;
    }

    public ReadAssertionsResponse addAssertionsItem(Assertion assertionsItem) {
        if (this.assertions == null) {
            this.assertions = new ArrayList<>();
        }
        this.assertions.add(assertionsItem);
        return this;
    }

    /**
     * Get assertions
     * @return assertions
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ASSERTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Assertion> getAssertions() {
        return assertions;
    }

    @JsonProperty(JSON_PROPERTY_ASSERTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAssertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    /**
     * Return true if this ReadAssertionsResponse object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReadAssertionsResponse readAssertionsResponse = (ReadAssertionsResponse) o;
        return Objects.equals(this.authorizationModelId, readAssertionsResponse.authorizationModelId)
                && Objects.equals(this.assertions, readAssertionsResponse.assertions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModelId, assertions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReadAssertionsResponse {\n");
        sb.append("    authorizationModelId: ")
                .append(toIndentedString(authorizationModelId))
                .append("\n");
        sb.append("    assertions: ").append(toIndentedString(assertions)).append("\n");
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

        // add `authorization_model_id` to the URL query string
        if (getAuthorizationModelId() != null) {
            joiner.add(String.format(
                    "%sauthorization_model_id%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getAuthorizationModelId()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `assertions` to the URL query string
        if (getAssertions() != null) {
            for (int i = 0; i < getAssertions().size(); i++) {
                if (getAssertions().get(i) != null) {
                    joiner.add(getAssertions()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%sassertions%s%s",
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
