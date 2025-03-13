package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ContentTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExerciseContentAudioDto.class, name = ContentTypes.AUDIO),
        @JsonSubTypes.Type(value = ExerciseContentFlashcardDto.class, name = ContentTypes.FLASHCARD),
        @JsonSubTypes.Type(value = ExerciseContentFlashcardSideDto.class, name = ContentTypes.FLASHCARD_SIDE),
        @JsonSubTypes.Type(value = ExerciseContentImageDto.class, name = ContentTypes.IMAGE),
        @JsonSubTypes.Type(value = ExerciseContentTextDto.class, name = ContentTypes.TEXT),
        @JsonSubTypes.Type(value = ExerciseContentVideoDto.class, name = ContentTypes.VIDEO),
})
public abstract class ExerciseContentDto {
    public ExerciseContentDto(String type) {
        this.type = type;
    }

    @NotNull
    private String id;

    @NotNull
    private String type;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
