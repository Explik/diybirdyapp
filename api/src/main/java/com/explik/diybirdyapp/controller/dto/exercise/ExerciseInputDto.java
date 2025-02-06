package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExerciseInputTextDto.class, name = ExerciseInputTypes.TEXT),
        @JsonSubTypes.Type(value = ExerciseInputMultipleChoiceTextDto.class, name = ExerciseInputTypes.MULTIPLE_CHOICE),
        @JsonSubTypes.Type(value = ExerciseInputRecognizabilityRatingDto.class, name = ExerciseInputTypes.RECOGNIZABILITY_RATING),
        @JsonSubTypes.Type(value = ExerciseInputAudioDto.class, name = ExerciseInputTypes.AUDIO),
        @JsonSubTypes.Type(value = ExerciseInputArrangeTextOptionsDto.class, name = ExerciseInputTypes.ARRANGE_TEXT_OPTIONS),
})
public class ExerciseInputDto {
    private String id;
    private String type;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
