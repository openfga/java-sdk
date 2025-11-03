package dev.openfga.sdk.api.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public final class StreamingResponseBody implements Closeable {
    private final InputStream body;

    public StreamingResponseBody(InputStream body) {
        this.body = body;
    }

    public InputStream getBody() {
        return body;
    }

    @Override
    public void close() throws IOException {
        if (body != null) {
            body.close();
        }
    }
}