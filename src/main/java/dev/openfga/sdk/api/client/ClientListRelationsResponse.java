package dev.openfga.sdk.api.client;

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

    public static ClientListRelationsResponse fromBatchCheckResponses(List<ClientBatchCheckResponse> responses) {
        return new ClientListRelationsResponse(responses.stream()
                .filter(ClientBatchCheckResponse::getAllowed)
                .map(batchCheckResponse -> batchCheckResponse.getRequest().getRelation())
                .collect(Collectors.toList()));
    }
}
