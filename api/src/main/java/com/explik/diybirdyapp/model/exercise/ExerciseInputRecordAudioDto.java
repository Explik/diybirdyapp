package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ExerciseInputRecordAudioDto extends ExerciseInputDto {
    @NotNull(message = "url.required")
    private String url;

    private ExerciseInputRecordAudioFeedbackDto feedback;

    public ExerciseInputRecordAudioDto() {
        setType(ExerciseInputTypes.RECORD_AUDIO);
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public ExerciseInputRecordAudioFeedbackDto getFeedback() { return feedback; }

    public void setFeedback(ExerciseInputRecordAudioFeedbackDto feedback) { this.feedback = feedback; }

    public static class ExerciseInputRecordAudioFeedbackDto {
        @NotNull(message = "correctValues.required")
        private List<String> correctValues = List.of();

        @NotNull(message = "incorrectValues.required")
        private List<String> incorrectValues = List.of();

        public List<String> getCorrectValues() { return correctValues; }

        public void setCorrectValues(List<String> correctValues) { this.correctValues = correctValues; }

        public List<String> getIncorrectValues() { return incorrectValues; }

        public void setIncorrectValues(List<String> incorrectValues) { this.incorrectValues = incorrectValues; }
    }
}
