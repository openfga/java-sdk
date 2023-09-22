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

package dev.openfga.sdk.api.configuration;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.time.Duration;

public class ClientConfiguration extends Configuration {
    private String storeId;
    private String authorizationModelId;

    public void assertValidStoreId() throws FgaInvalidParameterException {
        if (isNullOrWhitespace(storeId)) {
            throw new FgaInvalidParameterException("storeId", "ClientConfiguration");
        }
    }

    /**
     * Set the Store ID.
     *
     * @param storeId The URL.
     * @return This object.
     */
    public ClientConfiguration storeId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    /**
     * Get the Authorization Model ID.
     *
     * @return The Authorization Model ID.
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     * Get the Store ID.
     *
     * @return The Store ID.
     * @throws FgaInvalidParameterException when the Store ID is null, empty, or whitespace
     */
    public String getStoreIdChecked() throws FgaInvalidParameterException {
        if (isNullOrWhitespace(storeId)) {
            throw new FgaInvalidParameterException("storeId", "ClientConfiguration");
        }
        return storeId;
    }

    /**
     * Set the Authorization Model ID.
     *
     * @param authorizationModelId The URL.
     * @return This object.
     */
    public ClientConfiguration authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    /**
     * Get the Authorization Model ID.
     *
     * @return The Authorization Model ID.
     */
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    /**
     * Get the Authorization Model ID.
     *
     * @return The Authorization Model ID.
     * @throws FgaInvalidParameterException when the Authorization Model ID is null, empty, or whitespace
     */
    public String getAuthorizationModelIdChecked() throws FgaInvalidParameterException {
        if (isNullOrWhitespace(authorizationModelId)) {
            throw new FgaInvalidParameterException("authorizationModelId", "ClientConfiguration");
        }
        return authorizationModelId;
    }

    /* Overrides beyond this point required for typing. */

    @Override
    public ClientConfiguration override(ConfigurationOverride configurationOverride) {
        super.override(configurationOverride);
        return this;
    }

    @Override
    public ClientConfiguration apiUrl(String apiUrl) {
        super.apiUrl(apiUrl);
        return this;
    }

    @Override
    public ClientConfiguration credentials(Credentials credentials) {
        super.credentials(credentials);
        return this;
    }

    @Override
    public ClientConfiguration userAgent(String userAgent) {
        super.userAgent(userAgent);
        return this;
    }

    @Override
    public ClientConfiguration readTimeout(Duration readTimeout) {
        super.readTimeout(readTimeout);
        return this;
    }

    @Override
    public ClientConfiguration connectTimeout(Duration connectTimeout) {
        super.connectTimeout(connectTimeout);
        return this;
    }
}
