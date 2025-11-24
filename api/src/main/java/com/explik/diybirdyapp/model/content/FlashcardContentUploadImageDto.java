package com.explik.diybirdyapp.model.content;

import com.explik.diybirdyapp.ContentTypes;

import java.util.List;

public class FlashcardContentUploadImageDto extends FlashcardContentDto implements FileUploadModel {
    public FlashcardContentUploadImageDto() {
        super(ContentTypes.IMAGE_UPLOAD);
    }

    private String imageFileName;

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public List<String> getFileNames() {
        return List.of(imageFileName);
    }
}
