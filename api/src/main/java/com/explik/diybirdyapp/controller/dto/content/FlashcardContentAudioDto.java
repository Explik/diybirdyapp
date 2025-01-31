package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;

public class FlashcardContentAudioDto extends FlashcardContentDto {
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
