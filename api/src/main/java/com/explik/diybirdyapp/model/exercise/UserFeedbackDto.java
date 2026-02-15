package com.explik.diybirdyapp.model.exercise;

import jakarta.validation.constraints.NotNull;

public class UserFeedbackDto {
    @NotNull(message = "type.required")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
