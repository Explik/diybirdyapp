package com.explik.diybirdyapp.model.exercise;

public class ExerciseContentFlashcardModel extends ExerciseContentModel {
    private ExerciseContentModel front;
    private ExerciseContentModel back;
    private String initialSide = "front"; // "front" or "back"

    public ExerciseContentModel getFront() { return front; }

    public void setFront(ExerciseContentModel front) { this.front = front; }

    public ExerciseContentModel getBack() { return back; }

    public void setBack(ExerciseContentModel back) { this.back = back; }

    public String getInitialSide() { return initialSide; }

    public void setInitialSide(String initialSide) { this.initialSide = initialSide; }
}

