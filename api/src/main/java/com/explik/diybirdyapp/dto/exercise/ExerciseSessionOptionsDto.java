package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExerciseSessionOptionsLearnFlashcardsDto.class, name = ExerciseSessionTypes.LEARN_FLASHCARD),
        @JsonSubTypes.Type(value = ExerciseSessionOptionsReviewFlashcardsDto.class, name = ExerciseSessionTypes.REVIEW_FLASHCARD),
        @JsonSubTypes.Type(value = ExerciseSessionOptionsSelectFlashcardsDto.class, name = ExerciseSessionTypes.SELECT_FLASHCARD_DECK),
        @JsonSubTypes.Type(value = ExerciseSessionOptionsWriteFlashcardsDto.class, name = ExerciseSessionTypes.WRITE_FLASHCARD)
})
public abstract class ExerciseSessionOptionsDto {
    private String type;
    private boolean textToSpeechEnabled;

    public ExerciseSessionOptionsDto(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getTextToSpeechEnabled() {
        return textToSpeechEnabled;
    }

    public void setTextToSpeechEnabled(boolean textToSpeechEnabled) {
        this.textToSpeechEnabled = textToSpeechEnabled;
    }
}
