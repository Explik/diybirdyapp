package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;

public class FlashcardContentUploadVideoDto extends FlashcardContentDto {
    public FlashcardContentUploadVideoDto() {
        super(ContentTypes.VIDEO_UPLOAD);
    }

    private String videoFileName;
    private String languageId;

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }
}
