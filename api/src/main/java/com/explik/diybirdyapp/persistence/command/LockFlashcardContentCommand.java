package com.explik.diybirdyapp.persistence.command;

public class LockFlashcardContentCommand {
    private final String id;

    public LockFlashcardContentCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
