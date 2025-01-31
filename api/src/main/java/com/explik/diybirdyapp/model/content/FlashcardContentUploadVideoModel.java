package com.explik.diybirdyapp.model.content;

import java.util.List;

public class FlashcardContentUploadVideoModel extends FlashcardContentModel implements FileUploadModel {
    private String videoFileName;
    private String languageId;

    public String getVideoFileName() { return videoFileName; }

    public void setVideoFileName(String videoFileName) { this.videoFileName = videoFileName; }

    public String getLanguageId() { return languageId; }

    public void setLanguageId(String languageId) { this.languageId = languageId; }

    @Override
    public List<String> getFileNames() {
        return List.of(videoFileName);
    }
}
