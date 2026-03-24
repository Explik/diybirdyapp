package com.explik.diybirdyapp.persistence.command;

public class CompleteExerciseSessionCommand implements AtomicCommand {
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}