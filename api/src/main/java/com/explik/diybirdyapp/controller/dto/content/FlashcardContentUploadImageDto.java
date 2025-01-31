package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;

public class FlashcardContentUploadImageDto extends FlashcardContentDto {
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
}
