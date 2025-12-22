package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;

import java.util.List;

public class UpdateFlashcardDeckVertexCommand implements AtomicCommand {
    private String id;
    private String name;
    private List<FlashcardVertex> flashcards;

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

    public List<FlashcardVertex> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<FlashcardVertex> flashcards) {
        this.flashcards = flashcards;
    }
}
