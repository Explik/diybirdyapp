package com.explik.diybirdyapp.model.exercise;

import jakarta.validation.constraints.NotNull;

public class ExerciseInputWritePlaceholdersDto extends ExerciseInputDto {
    private WritePlaceholdersPartDto[] parts;
    private WritePlaceholdersFeedbackDto feedback;

    public WritePlaceholdersPartDto[] getParts() { return parts; }
    public void setParts(WritePlaceholdersPartDto[] parts) { this.parts = parts; }

    public WritePlaceholdersFeedbackDto getFeedback() { return feedback; }
    public void setFeedback(WritePlaceholdersFeedbackDto feedback) { this.feedback = feedback; }

    public static class WritePlaceholdersPartDto {
        @NotNull(message = "id.required")
        private String id;

        @NotNull(message = "type.required")
        private String type;

        private String value;

        private Number size;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public Number getSize() { return size; }
        public void setSize(Number size) { this.size = size; }
    }

    public static class WritePlaceholdersFeedbackDto {
        @NotNull(message = "correctPlaceholdersIds.required")
        private String[] correctPlaceholdersIds = new String[0];

        @NotNull(message = "incorrectPlaceholdersIds.required")
        private String[] incorrectPlaceholdersIds = new String[0];

        public String[] getCorrectPlaceholdersIds() { return correctPlaceholdersIds; }
        public void setCorrectPlaceholdersIds(String[] correctPlaceholdersIds) { this.correctPlaceholdersIds = correctPlaceholdersIds; }

        public String[] getIncorrectPlaceholdersIds() { return incorrectPlaceholdersIds; }
        public void setIncorrectPlaceholdersIds(String[] incorrectPlaceholdersIds) { this.incorrectPlaceholdersIds = incorrectPlaceholdersIds; }
    }
}
