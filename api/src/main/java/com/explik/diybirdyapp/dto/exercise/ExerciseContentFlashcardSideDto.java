package com.explik.diybirdyapp.dto.exercise;

import com.explik.diybirdyapp.ContentTypes;

public class ExerciseContentFlashcardSideDto extends ExerciseContentDto {
    private ExerciseContentDto content;

    public ExerciseContentFlashcardSideDto() {
        super(ContentTypes.FLASHCARD_SIDE);
    }

    public ExerciseContentDto getContent() {
        return content;
    }

    public void setContent(ExerciseContentDto content) {
        this.content = content;
    }
}
