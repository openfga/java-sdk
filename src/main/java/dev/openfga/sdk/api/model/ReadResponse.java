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
 * ReadResponse
 */
@JsonPropertyOrder({ReadResponse.JSON_PROPERTY_TUPLES, ReadResponse.JSON_PROPERTY_CONTINUATION_TOKEN})
public class ReadResponse {
    public static final String JSON_PROPERTY_TUPLES = "tuples";
    private List<Tuple> tuples = new ArrayList<>();

    public static final String JSON_PROPERTY_CONTINUATION_TOKEN = "continuation_token";
    private String continuationToken;

    public ReadResponse() {}

    public ReadResponse tuples(List<Tuple> tuples) {
        this.tuples = tuples;
        return this;
    }

    public ReadResponse addTuplesItem(Tuple tuplesItem) {
        if (this.tuples == null) {
            this.tuples = new ArrayList<>();
        }
        this.tuples.add(tuplesItem);
        return this;
    }

    /**
     * Get tuples
     * @return tuples
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TUPLES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Tuple> getTuples() {
        return tuples;
    }

    @JsonProperty(JSON_PROPERTY_TUPLES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTuples(List<Tuple> tuples) {
        this.tuples = tuples;
    }

    public ReadResponse continuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
        return this;
    }

    /**
     * The continuation token will be empty if there are no more tuples.
     * @return continuationToken
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CONTINUATION_TOKEN)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getContinuationToken() {
        return continuationToken;
    }

    @JsonProperty(JSON_PROPERTY_CONTINUATION_TOKEN)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    /**
     * Return true if this ReadResponse object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReadResponse readResponse = (ReadResponse) o;
        return Objects.equals(this.tuples, readResponse.tuples)
                && Objects.equals(this.continuationToken, readResponse.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tuples, continuationToken);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReadResponse {\n");
        sb.append("    tuples: ").append(toIndentedString(tuples)).append("\n");
        sb.append("    continuationToken: ")
                .append(toIndentedString(continuationToken))
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

        // add `tuples` to the URL query string
        if (getTuples() != null) {
            for (int i = 0; i < getTuples().size(); i++) {
                if (getTuples().get(i) != null) {
                    joiner.add(getTuples()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%stuples%s%s",
                                    prefix,
                                    suffix,
                                    "".equals(suffix)
                                            ? ""
                                            : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
                }
            }
        }

        // add `continuation_token` to the URL query string
        if (getContinuationToken() != null) {
            joiner.add(String.format(
                    "%scontinuation_token%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getContinuationToken()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
