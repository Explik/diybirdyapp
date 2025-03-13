package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ContentTypes;

public class ExerciseContentFlashcardDto extends ExerciseContentDto {
    private ExerciseContentDto front; // Left in flashcard editing
    private ExerciseContentDto back; // Right in flashcard editing

    public ExerciseContentFlashcardDto() {
        super(ContentTypes.FLASHCARD);
    }

    public ExerciseContentDto getFront() { return front; }

    public void setFront(ExerciseContentDto front) { this.front = front; }

    public ExerciseContentDto getBack() { return back; }

    public void setBack(ExerciseContentDto back) { this.back = back; }
}
