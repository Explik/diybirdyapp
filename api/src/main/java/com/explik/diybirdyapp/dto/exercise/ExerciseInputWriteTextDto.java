package com.explik.diybirdyapp.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ExerciseInputWriteTextDto extends ExerciseInputDto {
    @NotNull(message = "text.required")
    private String text;

    private ExerciseInputFeedbackTextDto feedback;

    public ExerciseInputWriteTextDto() {
        setType(ExerciseInputTypes.WRITE_TEXT);
    }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public ExerciseInputFeedbackTextDto getFeedback() { return feedback; }

    public void setFeedback(ExerciseInputFeedbackTextDto feedback) { this.feedback = feedback; }

    public static class ExerciseInputFeedbackTextDto {
        @NotNull(message = "correctValues.required")
        private List<String> correctValues = List.of();

        @NotNull(message = "incorrectValues.required")
        private List<String> incorrectValues = List.of();

        private boolean isRetypeAnswerEnabled = false;

        public boolean getIsRetypeAnswerEnabled() { return isRetypeAnswerEnabled; }

        public void setIsRetypeAnswerEnabled(boolean retypeAnswerEnabled) { isRetypeAnswerEnabled = retypeAnswerEnabled; }

        public List<String> getCorrectValues() { return correctValues; }

        public void setCorrectValues(List<String> correctValues) { this.correctValues = correctValues; }

        public List<String> getIncorrectValues() { return incorrectValues; }

        public void setIncorrectValues(List<String> incorrectValues) { this.incorrectValues = incorrectValues; }
    }
}
