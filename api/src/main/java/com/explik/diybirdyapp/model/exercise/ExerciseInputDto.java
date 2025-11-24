package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExerciseInputArrangeTextOptionsDto.class, name = ExerciseInputTypes.ARRANGE_TEXT_OPTIONS),
        @JsonSubTypes.Type(value = ExerciseInputPairOptionsDto.class, name = ExerciseInputTypes.PAIR_OPTIONS),
        @JsonSubTypes.Type(value = ExerciseInputRecordAudioDto.class, name = ExerciseInputTypes.RECORD_AUDIO),
        @JsonSubTypes.Type(value = ExerciseInputRecordVideoDto.class, name = ExerciseInputTypes.RECORD_VIDEO),
        @JsonSubTypes.Type(value = ExerciseInputSelectOptionsDto.class, name = ExerciseInputTypes.SELECT_OPTIONS),
        @JsonSubTypes.Type(value = ExerciseInputSelectPlaceholdersDto.class, name = ExerciseInputTypes.SELECT_PLACEHOLDERS),
        @JsonSubTypes.Type(value = ExerciseInputSelectReviewOptionsDto.class, name = ExerciseInputTypes.RECOGNIZABILITY_RATING),
        @JsonSubTypes.Type(value = ExerciseInputWritePlaceholdersDto.class, name = ExerciseInputTypes.WRITE_PLACEHOLDERS),
        @JsonSubTypes.Type(value = ExerciseInputWriteTextDto.class, name = ExerciseInputTypes.WRITE_TEXT)
})
public class ExerciseInputDto {
    private String id;

    private String type;

    @NotNull(message = "sessionId.required")
    private String sessionId;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
