package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FlashcardContentAudioDto extends FlashcardContentDto {
    @NotBlank(message = "audioUrl.required")
    private String audioUrl;

    public FlashcardContentAudioDto() {
        super(ContentTypes.AUDIO);
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
