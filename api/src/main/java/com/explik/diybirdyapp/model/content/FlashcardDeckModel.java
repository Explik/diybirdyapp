package com.explik.diybirdyapp.model.content;

public class FlashcardDeckModel {
    private String id;
    private String name;

    public FlashcardDeckModel() {}

    public FlashcardDeckModel(String id) {
        this.id = id;
    }

    public FlashcardDeckModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
