package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;

public class FlashcardContentUploadAudioDto extends FlashcardContentDto {
    public FlashcardContentUploadAudioDto() {
        super(ContentTypes.AUDIO_UPLOAD);
    }

    private String audioFileName;
    private String languageId;

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }
}
