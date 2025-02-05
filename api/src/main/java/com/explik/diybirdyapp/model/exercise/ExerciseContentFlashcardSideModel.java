package com.explik.diybirdyapp.model.exercise;

public class ExerciseContentFlashcardSideModel extends ExerciseContentModel {
    private ExerciseContentModel content;

    public ExerciseContentModel getContent() {
        return content;
    }

    public void setContent(ExerciseContentModel content) {
        this.content = content;
    }

    public static ExerciseContentFlashcardSideModel create(ExerciseContentModel content) {
        var instance = new ExerciseContentFlashcardSideModel();
        instance.setContent(content);
        return instance;
    }
}
