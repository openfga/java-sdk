package dev.openfga.sdk.api.configuration;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.Map;

public class ClientReadAuthorizationModelOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String authorizationModelId;

    public ClientReadAuthorizationModelOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public void assertValidAuthorizationModelId() throws FgaInvalidParameterException {
        if (isNullOrWhitespace(authorizationModelId)) {
            throw new FgaInvalidParameterException("authorizationModelId", "ClientConfiguration");
        }
    }

    public ClientReadAuthorizationModelOptions authorizationModelId(String authorizationModelId) {
        this.authorizationModelId = authorizationModelId;
        return this;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    public String getAuthorizationModelIdChecked() throws FgaInvalidParameterException {
        assertValidAuthorizationModelId();
        return authorizationModelId;
    }
}
