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
 * AuthorizationModel
 */
@JsonPropertyOrder({
    AuthorizationModel.JSON_PROPERTY_ID,
    AuthorizationModel.JSON_PROPERTY_SCHEMA_VERSION,
    AuthorizationModel.JSON_PROPERTY_TYPE_DEFINITIONS
})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-21T20:03:24.252549Z[Etc/UTC]")
public class AuthorizationModel {
    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_SCHEMA_VERSION = "schema_version";
    private String schemaVersion;

    public static final String JSON_PROPERTY_TYPE_DEFINITIONS = "type_definitions";
    private List<TypeDefinition> typeDefinitions = new ArrayList<>();

    public AuthorizationModel() {}

    public AuthorizationModel id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setId(String id) {
        this.id = id;
    }

    public AuthorizationModel schemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
        return this;
    }

    /**
     * Get schemaVersion
     * @return schemaVersion
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SCHEMA_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getSchemaVersion() {
        return schemaVersion;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public AuthorizationModel typeDefinitions(List<TypeDefinition> typeDefinitions) {
        this.typeDefinitions = typeDefinitions;
        return this;
    }

    public AuthorizationModel addTypeDefinitionsItem(TypeDefinition typeDefinitionsItem) {
        if (this.typeDefinitions == null) {
            this.typeDefinitions = new ArrayList<>();
        }
        this.typeDefinitions.add(typeDefinitionsItem);
        return this;
    }

    /**
     * Get typeDefinitions
     * @return typeDefinitions
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TYPE_DEFINITIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    @JsonProperty(JSON_PROPERTY_TYPE_DEFINITIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTypeDefinitions(List<TypeDefinition> typeDefinitions) {
        this.typeDefinitions = typeDefinitions;
    }

    /**
     * Return true if this AuthorizationModel object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorizationModel authorizationModel = (AuthorizationModel) o;
        return Objects.equals(this.id, authorizationModel.id)
                && Objects.equals(this.schemaVersion, authorizationModel.schemaVersion)
                && Objects.equals(this.typeDefinitions, authorizationModel.typeDefinitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, schemaVersion, typeDefinitions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AuthorizationModel {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    schemaVersion: ").append(toIndentedString(schemaVersion)).append("\n");
        sb.append("    typeDefinitions: ")
                .append(toIndentedString(typeDefinitions))
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

        // add `id` to the URL query string
        if (getId() != null) {
            joiner.add(String.format(
                    "%sid%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getId()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `schema_version` to the URL query string
        if (getSchemaVersion() != null) {
            joiner.add(String.format(
                    "%sschema_version%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getSchemaVersion()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `type_definitions` to the URL query string
        if (getTypeDefinitions() != null) {
            for (int i = 0; i < getTypeDefinitions().size(); i++) {
                if (getTypeDefinitions().get(i) != null) {
                    joiner.add(getTypeDefinitions()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%stype_definitions%s%s",
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
