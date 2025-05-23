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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * BatchCheckResponse
 */
@JsonPropertyOrder({BatchCheckResponse.JSON_PROPERTY_RESULT})
public class BatchCheckResponse {
    public static final String JSON_PROPERTY_RESULT = "result";
    private Map<String, BatchCheckSingleResult> result = new HashMap<>();

    public BatchCheckResponse() {}

    public BatchCheckResponse result(Map<String, BatchCheckSingleResult> result) {
        this.result = result;
        return this;
    }

    public BatchCheckResponse putResultItem(String key, BatchCheckSingleResult resultItem) {
        if (this.result == null) {
            this.result = new HashMap<>();
        }
        this.result.put(key, resultItem);
        return this;
    }

    /**
     * map keys are the correlation_id values from the BatchCheckItems in the request
     * @return result
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RESULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, BatchCheckSingleResult> getResult() {
        return result;
    }

    @JsonProperty(JSON_PROPERTY_RESULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setResult(Map<String, BatchCheckSingleResult> result) {
        this.result = result;
    }

    /**
     * Return true if this BatchCheckResponse object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BatchCheckResponse batchCheckResponse = (BatchCheckResponse) o;
        return Objects.equals(this.result, batchCheckResponse.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BatchCheckResponse {\n");
        sb.append("    result: ").append(toIndentedString(result)).append("\n");
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

        // add `result` to the URL query string
        if (getResult() != null) {
            for (String _key : getResult().keySet()) {
                if (getResult().get(_key) != null) {
                    joiner.add(getResult()
                            .get(_key)
                            .toUrlQueryString(String.format(
                                    "%sresult%s%s",
                                    prefix,
                                    suffix,
                                    "".equals(suffix)
                                            ? ""
                                            : String.format("%s%d%s", containerPrefix, _key, containerSuffix))));
                }
            }
        }

        return joiner.toString();
    }
}
