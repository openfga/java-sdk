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

package dev.openfga.api.model;

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
 * ListStoresResponse
 */
@JsonPropertyOrder({ListStoresResponse.JSON_PROPERTY_STORES, ListStoresResponse.JSON_PROPERTY_CONTINUATION_TOKEN})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-08-11T21:23:49.205789Z[Etc/UTC]")
public class ListStoresResponse {
    public static final String JSON_PROPERTY_STORES = "stores";
    private List<Store> stores = new ArrayList<>();

    public static final String JSON_PROPERTY_CONTINUATION_TOKEN = "continuation_token";
    private String continuationToken;

    public ListStoresResponse() {}

    public ListStoresResponse stores(List<Store> stores) {
        this.stores = stores;
        return this;
    }

    public ListStoresResponse addStoresItem(Store storesItem) {
        if (this.stores == null) {
            this.stores = new ArrayList<>();
        }
        this.stores.add(storesItem);
        return this;
    }

    /**
     * Get stores
     * @return stores
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STORES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Store> getStores() {
        return stores;
    }

    @JsonProperty(JSON_PROPERTY_STORES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public ListStoresResponse continuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
        return this;
    }

    /**
     * The continuation token will be empty if there are no more stores.
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
     * Return true if this ListStoresResponse object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListStoresResponse listStoresResponse = (ListStoresResponse) o;
        return Objects.equals(this.stores, listStoresResponse.stores)
                && Objects.equals(this.continuationToken, listStoresResponse.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stores, continuationToken);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ListStoresResponse {\n");
        sb.append("    stores: ").append(toIndentedString(stores)).append("\n");
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

        // add `stores` to the URL query string
        if (getStores() != null) {
            for (int i = 0; i < getStores().size(); i++) {
                if (getStores().get(i) != null) {
                    joiner.add(getStores()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%sstores%s%s",
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
