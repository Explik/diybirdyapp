package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ExerciseInputSelectPlaceholdersDto extends ExerciseInputDto {
    @NotNull
    private List<SelectPlaceholdersInputPart> parts = List.of();

    @NotNull
    private List<SelectPlaceholdersInputOption> options = List.of();

    public ExerciseInputSelectPlaceholdersDto() {
        setType(ExerciseInputTypes.SELECT_PLACEHOLDERS);
    }

    public List<SelectPlaceholdersInputPart> getParts() {
        return parts;
    }

    public void setParts(List<SelectPlaceholdersInputPart> parts) {
        this.parts = parts;
    }

    public List<SelectPlaceholdersInputOption> getOptions() {
        return options;
    }

    public void setOptions(List<SelectPlaceholdersInputOption> options) {
        this.options = options;
    }

    public static class SelectPlaceholdersInputPart {
        @NotNull
        private String type;

        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class SelectPlaceholdersInputOption {
        @NotNull
        private String id;

        @NotNull
        private String text;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
