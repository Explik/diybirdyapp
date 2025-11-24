package com.explik.diybirdyapp.model.content;

import java.util.List;

public class VocabularyDto {
    private List<VocabularyContentTextDto> words ;

    public List<VocabularyContentTextDto> getWords() {
        return words;
    }

    public void setWords(List<VocabularyContentTextDto> words) {
        this.words = words;
    }
}
