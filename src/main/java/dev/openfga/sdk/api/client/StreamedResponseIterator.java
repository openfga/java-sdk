package dev.openfga.sdk.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.model.StreamResultOfStreamedListObjectsResponse;
import dev.openfga.sdk.api.model.StreamedListObjectsResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for parsing newline-delimited JSON streaming responses.
 * Each line in the response is a StreamResultOfStreamedListObjectsResponse.
 *
 * If an error is encountered in the stream (either from parsing or from an error
 * response), it will be thrown as a StreamingException when hasNext() or next() is called.
 */
public class StreamedResponseIterator implements Iterator<StreamedListObjectsResponse> {
    private final BufferedReader reader;
    private final ObjectMapper objectMapper;
    private StreamedListObjectsResponse nextItem;
    private boolean hasNext;
    private StreamingException pendingException;

    public StreamedResponseIterator(Reader reader, ObjectMapper objectMapper) {
        this.reader = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
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
                    pendingException = new StreamingException(streamResult.getError());
                    hasNext = false;
                    nextItem = null;
                    return;
                }
            }
            // No more lines
            hasNext = false;
            nextItem = null;
        } catch (IOException e) {
            pendingException = new StreamingException("Failed to parse streaming response", e);
            hasNext = false;
            nextItem = null;
        }
    }

    @Override
    public boolean hasNext() {
        if (pendingException != null) {
            throw pendingException;
        }
        return hasNext && nextItem != null;
    }

    @Override
    public StreamedListObjectsResponse next() {
        if (pendingException != null) {
            throw pendingException;
        }

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        StreamedListObjectsResponse current = nextItem;
        advance();

        if (pendingException != null) {
            throw pendingException;
        }

        return current;
    }
}
