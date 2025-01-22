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
 * BatchCheckRequest
 */
@JsonPropertyOrder({
    BatchCheckRequest.JSON_PROPERTY_CHECKS,
    BatchCheckRequest.JSON_PROPERTY_AUTHORIZATION_MODEL_ID,
    BatchCheckRequest.JSON_PROPERTY_CONSISTENCY
})
public class BatchCheckRequest {
    public static final String JSON_PROPERTY_CHECKS = "checks";
    private List<BatchCheckItem> checks = new ArrayList<>();

    public static final String JSON_PROPERTY_AUTHORIZATION_MODEL_ID = "authorization_model_id";
    private String authorizationModelId;

    public static final String JSON_PROPERTY_CONSISTENCY = "consistency";
    private ConsistencyPreference consistency = ConsistencyPreference.UNSPECIFIED;

    public BatchCheckRequest() {}

    public BatchCheckRequest checks(List<BatchCheckItem> checks) {
        this.checks = checks;
        return this;
    }

    public BatchCheckRequest addChecksItem(BatchCheckItem checksItem) {
        if (this.checks == null) {
            this.checks = new ArrayList<>();
        }
        this.checks.add(checksItem);
        return this;
    }

    /**
     * Get checks
     * @return checks
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CHECKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<BatchCheckItem> getChecks() {
        return checks;
    }

    @JsonProperty(JSON_PROPERTY_CHECKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setChecks(List<BatchCheckItem> checks) {
        this.checks = checks;
    }

    public BatchCheckRequest authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    /**
     * Get authorizationModelId
     * @return authorizationModelId
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_AUTHORIZATION_MODEL_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @JsonProperty(JSON_PROPERTY_AUTHORIZATION_MODEL_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAuthorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
    }

    public BatchCheckRequest consistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
        return this;
    }

    /**
     * Get consistency
     * @return consistency
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONSISTENCY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public ConsistencyPreference getConsistency() {
        return consistency;
    }

    @JsonProperty(JSON_PROPERTY_CONSISTENCY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setConsistency(ConsistencyPreference consistency) {
        this.consistency = consistency;
    }

    /**
     * Return true if this BatchCheck_request object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BatchCheckRequest batchCheckRequest = (BatchCheckRequest) o;
        return Objects.equals(this.checks, batchCheckRequest.checks)
                && Objects.equals(this.authorizationModelId, batchCheckRequest.authorizationModelId)
                && Objects.equals(this.consistency, batchCheckRequest.consistency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checks, authorizationModelId, consistency);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BatchCheckRequest {\n");
        sb.append("    checks: ").append(toIndentedString(checks)).append("\n");
        sb.append("    authorizationModelId: ")
                .append(toIndentedString(authorizationModelId))
                .append("\n");
        sb.append("    consistency: ").append(toIndentedString(consistency)).append("\n");
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

        // add `checks` to the URL query string
        if (getChecks() != null) {
            for (int i = 0; i < getChecks().size(); i++) {
                if (getChecks().get(i) != null) {
                    joiner.add(getChecks()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%schecks%s%s",
                                    prefix,
                                    suffix,
                                    "".equals(suffix)
                                            ? ""
                                            : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
                }
            }
        }

        // add `authorization_model_id` to the URL query string
        if (getAuthorizationModelId() != null) {
            joiner.add(String.format(
                    "%sauthorization_model_id%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getAuthorizationModelId()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `consistency` to the URL query string
        if (getConsistency() != null) {
            joiner.add(String.format(
                    "%sconsistency%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getConsistency()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
