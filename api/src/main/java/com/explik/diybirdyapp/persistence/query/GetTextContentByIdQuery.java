package com.explik.diybirdyapp.persistence.query;

/**
 * Query to retrieve a text content vertex by its ID.
 */
public class GetTextContentByIdQuery {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
