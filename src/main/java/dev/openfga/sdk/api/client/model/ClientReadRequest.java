package dev.openfga.sdk.api.client.model;

public class ClientReadRequest {
    private String user;
    private String relation;
    private String _object;

    public ClientReadRequest _object(String _object) {
        this._object = _object;
        return this;
    }

    /**
     * Get _object
     * @return _object
     **/
    public String getObject() {
        return _object;
    }

    public ClientReadRequest relation(String relation) {
        this.relation = relation;
        return this;
    }

    /**
     * Get relation
     * @return relation
     **/
    public String getRelation() {
        return relation;
    }

    public ClientReadRequest user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Get user
     * @return user
     **/
    public String getUser() {
        return user;
    }
}
