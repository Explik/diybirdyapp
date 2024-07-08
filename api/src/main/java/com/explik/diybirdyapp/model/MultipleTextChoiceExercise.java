package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.Discriminator;

import java.util.List;

@Discriminator("multiple-text-choice-exercise")
public interface MultipleTextChoiceExercise extends Exercise {
    public List<Option> getOptions();
    public void setOptions(List<Option> options);

    public static record Option(String id, String text) {}
}
