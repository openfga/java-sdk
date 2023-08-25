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
 * ReadChangesResponse
 */
@JsonPropertyOrder({ReadChangesResponse.JSON_PROPERTY_CHANGES, ReadChangesResponse.JSON_PROPERTY_CONTINUATION_TOKEN})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-25T20:28:06.814651Z[Etc/UTC]")
public class ReadChangesResponse {
    public static final String JSON_PROPERTY_CHANGES = "changes";
    private List<TupleChange> changes = new ArrayList<>();

    public static final String JSON_PROPERTY_CONTINUATION_TOKEN = "continuation_token";
    private String continuationToken;

    public ReadChangesResponse() {}

    public ReadChangesResponse changes(List<TupleChange> changes) {
        this.changes = changes;
        return this;
    }

    public ReadChangesResponse addChangesItem(TupleChange changesItem) {
        if (this.changes == null) {
            this.changes = new ArrayList<>();
        }
        this.changes.add(changesItem);
        return this;
    }

    /**
     * Get changes
     * @return changes
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CHANGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<TupleChange> getChanges() {
        return changes;
    }

    @JsonProperty(JSON_PROPERTY_CHANGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setChanges(List<TupleChange> changes) {
        this.changes = changes;
    }

    public ReadChangesResponse continuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
        return this;
    }

    /**
     * The continuation token will be identical if there are no new changes.
     * @return continuationToken
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTINUATION_TOKEN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getContinuationToken() {
        return continuationToken;
    }

    @JsonProperty(JSON_PROPERTY_CONTINUATION_TOKEN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    /**
     * Return true if this ReadChangesResponse object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReadChangesResponse readChangesResponse = (ReadChangesResponse) o;
        return Objects.equals(this.changes, readChangesResponse.changes)
                && Objects.equals(this.continuationToken, readChangesResponse.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changes, continuationToken);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReadChangesResponse {\n");
        sb.append("    changes: ").append(toIndentedString(changes)).append("\n");
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

        // add `changes` to the URL query string
        if (getChanges() != null) {
            for (int i = 0; i < getChanges().size(); i++) {
                if (getChanges().get(i) != null) {
                    joiner.add(getChanges()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%schanges%s%s",
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
