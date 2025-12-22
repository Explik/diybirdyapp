package com.explik.diybirdyapp.persistence.generalCommand;

public class LockFlashcardContentCommand {
    private final String id;

    public LockFlashcardContentCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
