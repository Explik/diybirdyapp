package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;

public class AddLanguageCommand {
    private FlashcardLanguageDto language;

    public FlashcardLanguageDto getLanguage() {
        return language;
    }

    public void setLanguage(FlashcardLanguageDto language) {
        this.language = language;
    }
}
