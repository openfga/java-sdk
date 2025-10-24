package dev.openfga.sdk.api.client.model;

import java.util.List;
import java.util.stream.Collectors;

public class ClientListRelationsResponse {
    private final List<String> relations;

    public ClientListRelationsResponse(List<String> relations) {
        this.relations = relations;
    }

    public List<String> getRelations() {
        return relations;
    }

    public static ClientListRelationsResponse fromBatchCheckResponses(List<ClientBatchCheckClientResponse> responses)
            throws Throwable {
        // If any response ultimately failed (with retries) we throw the first exception encountered.
        var failedResponse = responses.stream()
                .filter(response -> response.getThrowable() != null)
                .findFirst();
        if (failedResponse.isPresent()) {
            throw failedResponse.get().getThrowable();
        }

        var relations = responses.stream()
                .filter(ClientBatchCheckClientResponse::getAllowed)
                .map(ClientBatchCheckClientResponse::getRelation)
                .collect(Collectors.toList());
        return new ClientListRelationsResponse(relations);
    }
}
