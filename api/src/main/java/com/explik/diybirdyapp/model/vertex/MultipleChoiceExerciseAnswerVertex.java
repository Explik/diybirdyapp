package com.explik.diybirdyapp.model.vertex;

import com.explik.diybirdyapp.model.MultipleChoiceExerciseAnswer;
import com.syncleus.ferma.annotations.Property;

public abstract class MultipleChoiceExerciseAnswerVertex extends ExerciseAnswerVertex implements MultipleChoiceExerciseAnswer {
    @Property("optionId")
    public abstract String getOptionId();

    @Property("optionId")
    public abstract void setOptionId(String optionId);
}
