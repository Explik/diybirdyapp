package com.explik.diybirdyapp.model.content;

import java.util.List;

public class FlashcardContentUploadImageModel extends FlashcardContentModel implements FileUploadModel {
    private String imageFileName;

    public String getImageFileName() { return imageFileName; }

    public void setImageFileName(String imageFileName) { this.imageFileName = imageFileName; }

    @Override
    public List<String> getFileNames() {
        return List.of(imageFileName);
    }
}
