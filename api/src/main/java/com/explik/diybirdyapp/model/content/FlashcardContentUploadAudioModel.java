package com.explik.diybirdyapp.model.content;

import java.util.List;

public class FlashcardContentUploadAudioModel extends FlashcardContentModel implements FileUploadModel {
    private String audioFileName;
    private String languageId;

    public String getAudioFileName() { return audioFileName; }

    public void setAudioFileName(String audioFileName) { this.audioFileName = audioFileName; }

    public String getLanguageId() { return languageId; }

    public void setLanguageId(String languageId) { this.languageId = languageId; }

    @Override
    public List<String> getFileNames() {
        return List.of(audioFileName);
    }
}
