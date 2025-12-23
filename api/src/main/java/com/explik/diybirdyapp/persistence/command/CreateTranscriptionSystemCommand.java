package com.explik.diybirdyapp.persistence.command;

public class CreateTranscriptionSystemCommand implements AtomicCommand {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
