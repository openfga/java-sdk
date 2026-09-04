package dev.openfga.sdk.errors;

import java.io.IOException;

/** Reports a failure to serialize or deserialize an SDK value. */
public class SdkSerializationException extends IOException {
    public SdkSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
