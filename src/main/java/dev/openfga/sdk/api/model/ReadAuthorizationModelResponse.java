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
import java.util.Objects;
import java.util.StringJoiner;

/**
 * ReadAuthorizationModelResponse
 */
@JsonPropertyOrder({ReadAuthorizationModelResponse.JSON_PROPERTY_AUTHORIZATION_MODEL})
public class ReadAuthorizationModelResponse {
    public static final String JSON_PROPERTY_AUTHORIZATION_MODEL = "authorization_model";
    private AuthorizationModel authorizationModel;

    public ReadAuthorizationModelResponse() {}

    public ReadAuthorizationModelResponse authorizationModel(AuthorizationModel authorizationModel) {
        this.authorizationModel = authorizationModel;
        return this;
    }

    /**
     * Get authorizationModel
     * @return authorizationModel
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_AUTHORIZATION_MODEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public AuthorizationModel getAuthorizationModel() {
        return authorizationModel;
    }

    @JsonProperty(JSON_PROPERTY_AUTHORIZATION_MODEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAuthorizationModel(AuthorizationModel authorizationModel) {
        this.authorizationModel = authorizationModel;
    }

    /**
     * Return true if this ReadAuthorizationModelResponse object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReadAuthorizationModelResponse readAuthorizationModelResponse = (ReadAuthorizationModelResponse) o;
        return Objects.equals(this.authorizationModel, readAuthorizationModelResponse.authorizationModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModel);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReadAuthorizationModelResponse {\n");
        sb.append("    authorizationModel: ")
                .append(toIndentedString(authorizationModel))
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

        // add `authorization_model` to the URL query string
        if (getAuthorizationModel() != null) {
            joiner.add(getAuthorizationModel().toUrlQueryString(prefix + "authorization_model" + suffix));
        }

        return joiner.toString();
    }
}
