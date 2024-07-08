package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.Discriminator;

@Discriminator("write-translated-sentence-exercise")
public interface WriteTranslatedSentenceExercise extends Exercise {
    public String getOriginalSentence();
    public void setOriginalSentence(String originalSentence);

    public String getTargetLanguage();
    public void setTargetLanguage(String targetLanguage);
}
