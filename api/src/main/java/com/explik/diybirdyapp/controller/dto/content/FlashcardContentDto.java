package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FlashcardContentAudioDto.class, name = ContentTypes.AUDIO),
        @JsonSubTypes.Type(value = FlashcardContentImageDto.class, name = ContentTypes.IMAGE),
        @JsonSubTypes.Type(value = FlashcardContentTextDto.class, name = ContentTypes.TEXT),
        @JsonSubTypes.Type(value = FlashcardContentVideoDto.class, name = ContentTypes.VIDEO),
        @JsonSubTypes.Type(value = FlashcardContentUploadAudioDto.class, name = ContentTypes.AUDIO_UPLOAD),
        @JsonSubTypes.Type(value = FlashcardContentUploadImageDto.class, name = ContentTypes.IMAGE_UPLOAD),
        @JsonSubTypes.Type(value = FlashcardContentUploadVideoDto.class, name = ContentTypes.VIDEO_UPLOAD)
})
public class FlashcardContentDto {
    @NotNull
    private String id;

    @NotNull
    private String type;

    public FlashcardContentDto(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
