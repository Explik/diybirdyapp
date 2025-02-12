package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;
import jakarta.validation.constraints.NotNull;

public class FlashcardContentImageDto extends FlashcardContentDto {
    public FlashcardContentImageDto() {
        super(ContentTypes.IMAGE);
    }

    @NotNull
    private String imageUrl;

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
