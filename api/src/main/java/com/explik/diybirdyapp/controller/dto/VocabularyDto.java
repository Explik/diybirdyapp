package com.explik.diybirdyapp.controller.dto;

import com.explik.diybirdyapp.model.VocabularyTextContentModel;

import java.util.List;

public class VocabularyDto {
    private List<VocabularyTextContentModel> words ;

    public List<VocabularyTextContentModel> getWords() {
        return words;
    }

    public void setWords(List<VocabularyTextContentModel> words) {
        this.words = words;
    }
}
