package com.explik.diybirdyapp.controller.dto.content;

import com.explik.diybirdyapp.ContentTypes;
import jakarta.validation.constraints.NotNull;

public class FlashcardContentTextDto extends FlashcardContentDto {
    public FlashcardContentTextDto() {
        super(ContentTypes.TEXT);
    }

    @NotNull
    private String text;

    @NotNull
    private String languageId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }
}
