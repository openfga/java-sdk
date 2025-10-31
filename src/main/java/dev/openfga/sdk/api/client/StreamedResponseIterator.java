package dev.openfga.sdk.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.model.Status;
import dev.openfga.sdk.api.model.StreamResultOfStreamedListObjectsResponse;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for parsing newline-delimited JSON streaming responses.
 * Each line in the response is a StreamResultOfStreamedListObjectsResponse.
 *
 * If an error is encountered in the stream (either from parsing or from an error
 * response), it will be thrown as a RuntimeException when hasNext() or next() is called.
 */
public class StreamedResponseIterator implements Iterator<StreamedListObjectsResponse> {
    private final BufferedReader reader;
    private final ObjectMapper objectMapper;
    private StreamedListObjectsResponse nextItem;
    private boolean hasNext;
    private RuntimeException pendingException;

    public StreamedResponseIterator(String ndjsonResponse, ObjectMapper objectMapper) {
        this.reader = new BufferedReader(new StringReader(ndjsonResponse));
        this.objectMapper = objectMapper;
        this.hasNext = true;
        this.pendingException = null;
        advance();
    }

    private void advance() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                StreamResultOfStreamedListObjectsResponse streamResult =
                        objectMapper.readValue(line, StreamResultOfStreamedListObjectsResponse.class);

                if (streamResult.getResult() != null) {
                    nextItem = streamResult.getResult();
                    return;
                }

                if (streamResult.getError() != null) {
                    // Handle error in stream - convert to exception
                    Status error = streamResult.getError();
                    String errorMessage = String.format(
                            "Error in streaming response: code=%d, message=%s",
                            error.getCode(), error.getMessage() != null ? error.getMessage() : "Unknown error");
                    pendingException = new RuntimeException(errorMessage);
                    hasNext = false;
                    nextItem = null;
                    return;
                }
            }
            // No more lines
            hasNext = false;
            nextItem = null;
        } catch (IOException e) {
            pendingException = new RuntimeException("Failed to parse streaming response", e);
            hasNext = false;
            nextItem = null;
        }
    }

    @Override
    public boolean hasNext() {
        // If there's a pending exception, throw it before returning false
        if (pendingException != null) {
            RuntimeException ex = pendingException;
            pendingException = null; // Clear it so we don't throw multiple times
            throw ex;
        }
        return hasNext && nextItem != null;
    }

    @Override
    public StreamedListObjectsResponse next() {
        // Check for pending exception first
        if (pendingException != null) {
            RuntimeException ex = pendingException;
            pendingException = null;
            throw ex;
        }

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        StreamedListObjectsResponse current = nextItem;
        advance();

        // Check again after advance in case an error occurred
        if (pendingException != null) {
            RuntimeException ex = pendingException;
            pendingException = null;
            throw ex;
        }

        return current;
    }
}
