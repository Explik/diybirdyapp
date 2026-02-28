package com.explik.diybirdyapp.persistence.command;

public class CreateExerciseFeedbackCommand implements AtomicCommand {
    private String exerciseAnswerId;
    private String type;
    private String status;

    public String getExerciseAnswerId() {
        return exerciseAnswerId;
    }

    public void setExerciseAnswerId(String exerciseAnswerId) {
        this.exerciseAnswerId = exerciseAnswerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
