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
 * ConditionMetadata
 */
@JsonPropertyOrder({ConditionMetadata.JSON_PROPERTY_MODULE, ConditionMetadata.JSON_PROPERTY_SOURCE_INFO})
public class ConditionMetadata {
    public static final String JSON_PROPERTY_MODULE = "module";
    private String module;

    public static final String JSON_PROPERTY_SOURCE_INFO = "source_info";
    private SourceInfo sourceInfo;

    public ConditionMetadata() {}

    public ConditionMetadata module(String module) {
        this.module = module;
        return this;
    }

    /**
     * Get module
     * @return module
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MODULE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getModule() {
        return module;
    }

    @JsonProperty(JSON_PROPERTY_MODULE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setModule(String module) {
        this.module = module;
    }

    public ConditionMetadata sourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
        return this;
    }

    /**
     * Get sourceInfo
     * @return sourceInfo
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SOURCE_INFO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    @JsonProperty(JSON_PROPERTY_SOURCE_INFO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    /**
     * Return true if this ConditionMetadata object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConditionMetadata conditionMetadata = (ConditionMetadata) o;
        return Objects.equals(this.module, conditionMetadata.module)
                && Objects.equals(this.sourceInfo, conditionMetadata.sourceInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, sourceInfo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ConditionMetadata {\n");
        sb.append("    module: ").append(toIndentedString(module)).append("\n");
        sb.append("    sourceInfo: ").append(toIndentedString(sourceInfo)).append("\n");
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

        // add `module` to the URL query string
        if (getModule() != null) {
            joiner.add(String.format(
                    "%smodule%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getModule()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `source_info` to the URL query string
        if (getSourceInfo() != null) {
            joiner.add(getSourceInfo().toUrlQueryString(prefix + "source_info" + suffix));
        }

        return joiner.toString();
    }
}
