package com.explik.diybirdyapp.controller.model.content;

import com.explik.diybirdyapp.ContentTypes;
import jakarta.validation.constraints.NotBlank;

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
