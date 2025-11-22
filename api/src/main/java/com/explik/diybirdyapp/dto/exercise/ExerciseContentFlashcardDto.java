package com.explik.diybirdyapp.dto.exercise;

import com.explik.diybirdyapp.ContentTypes;

public class ExerciseContentFlashcardDto extends ExerciseContentDto {
    private ExerciseContentDto front; // Left in flashcard editing
    private ExerciseContentDto back; // Right in flashcard editing
    private String initialSide = "front"; // "front" or "back"

    public ExerciseContentFlashcardDto() {
        super(ContentTypes.FLASHCARD);
    }

    public ExerciseContentDto getFront() { return front; }

    public void setFront(ExerciseContentDto front) { this.front = front; }

    public ExerciseContentDto getBack() { return back; }

    public void setBack(ExerciseContentDto back) { this.back = back; }

    public String getInitialSide() { return initialSide; }

    public void setInitialSide(String initialSide) { this.initialSide = initialSide; }
}
