package com.explik.diybirdyapp.model.content;

import com.explik.diybirdyapp.ContentTypes;

import java.util.List;

public class FlashcardContentUploadVideoDto extends FlashcardContentDto implements FileUploadModel {
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

    @Override
    public List<String> getFileNames() {
        return List.of(videoFileName);
    }
}
