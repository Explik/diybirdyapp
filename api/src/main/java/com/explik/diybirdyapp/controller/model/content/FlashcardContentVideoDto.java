package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;

public class FlashcardContentVideoDto extends FlashcardContentDto {
    public FlashcardContentVideoDto() {
        super(ContentTypes.VIDEO);
    }

    private String videoUrl;

    public String getVideoUrl() { return videoUrl; }

    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
