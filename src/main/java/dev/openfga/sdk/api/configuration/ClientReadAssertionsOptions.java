package dev.openfga.sdk.api.configuration;

import static dev.openfga.sdk.util.StringUtil.isNullOrWhitespace;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.util.Map;

public class ClientReadAssertionsOptions implements AdditionalHeadersSupplier {
    private Map<String, String> additionalHeaders;
    private String authorizationModelId;

    public ClientReadAssertionsOptions additionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
        return this;
    }

    @Override
    public Map<String, String> getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public boolean hasValidAuthorizationModelId() throws FgaInvalidParameterException {
        return !isNullOrWhitespace(authorizationModelId);
    }

    public void assertValidAuthorizationModelId() throws FgaInvalidParameterException {
        if (!hasValidAuthorizationModelId()) {
            throw new FgaInvalidParameterException("authorizationModelId", "ClientConfiguration");
        }
    }

    public ClientReadAssertionsOptions authorizationModelId(String authorizationModelId) {
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
