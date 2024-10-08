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
 * ListUsersRequest
 */
@JsonPropertyOrder({
    ListUsersRequest.JSON_PROPERTY_AUTHORIZATION_MODEL_ID,
    ListUsersRequest.JSON_PROPERTY_OBJECT,
    ListUsersRequest.JSON_PROPERTY_RELATION,
    ListUsersRequest.JSON_PROPERTY_USER_FILTERS,
    ListUsersRequest.JSON_PROPERTY_CONTEXTUAL_TUPLES,
    ListUsersRequest.JSON_PROPERTY_CONTEXT,
    ListUsersRequest.JSON_PROPERTY_CONSISTENCY
})
public class ListUsersRequest {
    public static final String JSON_PROPERTY_AUTHORIZATION_MODEL_ID = "authorization_model_id";
    private String authorizationModelId;

    public static final String JSON_PROPERTY_OBJECT = "object";
    private FgaObject _object;

    public static final String JSON_PROPERTY_RELATION = "relation";
    private String relation;

    public static final String JSON_PROPERTY_USER_FILTERS = "user_filters";
    private List<UserTypeFilter> userFilters = new ArrayList<>();

    public static final String JSON_PROPERTY_CONTEXTUAL_TUPLES = "contextual_tuples";
    private List<TupleKey> contextualTuples = new ArrayList<>();

    public static final String JSON_PROPERTY_CONTEXT = "context";
    private Object context;

    public static final String JSON_PROPERTY_CONSISTENCY = "consistency";
    private ConsistencyPreference consistency = ConsistencyPreference.UNSPECIFIED;

    public ListUsersRequest() {}

    public ListUsersRequest authorizationModelId(String authorizationModelId) {
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

    public ListUsersRequest _object(FgaObject _object) {
        this._object = _object;
        return this;
    }

    /**
     * Get _object
     * @return _object
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_OBJECT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public FgaObject getObject() {
        return _object;
    }

    @JsonProperty(JSON_PROPERTY_OBJECT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setObject(FgaObject _object) {
        this._object = _object;
    }

    public ListUsersRequest relation(String relation) {
        this.relation = relation;
        return this;
    }

    /**
     * Get relation
     * @return relation
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_RELATION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getRelation() {
        return relation;
    }

    @JsonProperty(JSON_PROPERTY_RELATION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setRelation(String relation) {
        this.relation = relation;
    }

    public ListUsersRequest userFilters(List<UserTypeFilter> userFilters) {
        this.userFilters = userFilters;
        return this;
    }

    public ListUsersRequest addUserFiltersItem(UserTypeFilter userFiltersItem) {
        if (this.userFilters == null) {
            this.userFilters = new ArrayList<>();
        }
        this.userFilters.add(userFiltersItem);
        return this;
    }

    /**
     * The type of results returned. Only accepts exactly one value.
     * @return userFilters
     **/
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_USER_FILTERS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<UserTypeFilter> getUserFilters() {
        return userFilters;
    }

    @JsonProperty(JSON_PROPERTY_USER_FILTERS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setUserFilters(List<UserTypeFilter> userFilters) {
        this.userFilters = userFilters;
    }

    public ListUsersRequest contextualTuples(List<TupleKey> contextualTuples) {
        this.contextualTuples = contextualTuples;
        return this;
    }

    public ListUsersRequest addContextualTuplesItem(TupleKey contextualTuplesItem) {
        if (this.contextualTuples == null) {
            this.contextualTuples = new ArrayList<>();
        }
        this.contextualTuples.add(contextualTuplesItem);
        return this;
    }

    /**
     * Get contextualTuples
     * @return contextualTuples
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTEXTUAL_TUPLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<TupleKey> getContextualTuples() {
        return contextualTuples;
    }

    @JsonProperty(JSON_PROPERTY_CONTEXTUAL_TUPLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContextualTuples(List<TupleKey> contextualTuples) {
        this.contextualTuples = contextualTuples;
    }

    public ListUsersRequest context(Object context) {
        this.context = context;
        return this;
    }

    /**
     * Additional request context that will be used to evaluate any ABAC conditions encountered in the query evaluation.
     * @return context
     **/
    @javax.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTEXT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getContext() {
        return context;
    }

    @JsonProperty(JSON_PROPERTY_CONTEXT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContext(Object context) {
        this.context = context;
    }

    public ListUsersRequest consistency(ConsistencyPreference consistency) {
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
     * Return true if this ListUsers_request object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListUsersRequest listUsersRequest = (ListUsersRequest) o;
        return Objects.equals(this.authorizationModelId, listUsersRequest.authorizationModelId)
                && Objects.equals(this._object, listUsersRequest._object)
                && Objects.equals(this.relation, listUsersRequest.relation)
                && Objects.equals(this.userFilters, listUsersRequest.userFilters)
                && Objects.equals(this.contextualTuples, listUsersRequest.contextualTuples)
                && Objects.equals(this.context, listUsersRequest.context)
                && Objects.equals(this.consistency, listUsersRequest.consistency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                authorizationModelId, _object, relation, userFilters, contextualTuples, context, consistency);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ListUsersRequest {\n");
        sb.append("    authorizationModelId: ")
                .append(toIndentedString(authorizationModelId))
                .append("\n");
        sb.append("    _object: ").append(toIndentedString(_object)).append("\n");
        sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
        sb.append("    userFilters: ").append(toIndentedString(userFilters)).append("\n");
        sb.append("    contextualTuples: ")
                .append(toIndentedString(contextualTuples))
                .append("\n");
        sb.append("    context: ").append(toIndentedString(context)).append("\n");
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

        // add `authorization_model_id` to the URL query string
        if (getAuthorizationModelId() != null) {
            joiner.add(String.format(
                    "%sauthorization_model_id%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getAuthorizationModelId()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `object` to the URL query string
        if (getObject() != null) {
            joiner.add(getObject().toUrlQueryString(prefix + "object" + suffix));
        }

        // add `relation` to the URL query string
        if (getRelation() != null) {
            joiner.add(String.format(
                    "%srelation%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getRelation()), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20")));
        }

        // add `user_filters` to the URL query string
        if (getUserFilters() != null) {
            for (int i = 0; i < getUserFilters().size(); i++) {
                if (getUserFilters().get(i) != null) {
                    joiner.add(getUserFilters()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%suser_filters%s%s",
                                    prefix,
                                    suffix,
                                    "".equals(suffix)
                                            ? ""
                                            : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
                }
            }
        }

        // add `contextual_tuples` to the URL query string
        if (getContextualTuples() != null) {
            for (int i = 0; i < getContextualTuples().size(); i++) {
                if (getContextualTuples().get(i) != null) {
                    joiner.add(getContextualTuples()
                            .get(i)
                            .toUrlQueryString(String.format(
                                    "%scontextual_tuples%s%s",
                                    prefix,
                                    suffix,
                                    "".equals(suffix)
                                            ? ""
                                            : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
                }
            }
        }

        // add `context` to the URL query string
        if (getContext() != null) {
            joiner.add(String.format(
                    "%scontext%s=%s",
                    prefix,
                    suffix,
                    URLEncoder.encode(String.valueOf(getContext()), StandardCharsets.UTF_8)
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
