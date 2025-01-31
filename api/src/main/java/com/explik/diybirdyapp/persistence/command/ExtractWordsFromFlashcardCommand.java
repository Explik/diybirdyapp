package com.explik.diybirdyapp.persistence.command;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;

public class ExtractWordsFromFlashcardCommand {
    private final String flashcardId;

    public ExtractWordsFromFlashcardCommand(String flashcardId) {
        this.flashcardId = flashcardId;
    }

    public String getFlashcardId() {
        return flashcardId;
    }
}