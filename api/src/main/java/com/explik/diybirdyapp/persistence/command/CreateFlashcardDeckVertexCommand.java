package com.explik.diybirdyapp.persistence.command;

import java.util.List;

public class CreateFlashcardDeckVertexCommand implements AtomicCommand {
    private String id;
    private String name;
    private List<String> flashcardIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFlashcardIds() {
        return flashcardIds;
    }

    public void setFlashcards(List<String> flashcardIds) {
        this.flashcardIds = flashcardIds;
    }
}
