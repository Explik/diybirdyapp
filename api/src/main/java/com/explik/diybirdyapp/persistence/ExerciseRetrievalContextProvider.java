package com.explik.diybirdyapp.persistence;

import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;

public class ExerciseRetrievalContextProvider {
    public ExerciseRetrievalContext get(ExerciseSessionVertex sessionVertex) {
        var sessionOptionsVertex = sessionVertex.getOptions();
        if (sessionOptionsVertex == null)
            return ExerciseRetrievalContext.createDefault();

        var context = new ExerciseRetrievalContext();
        context.setTextToSpeechEnabled(sessionOptionsVertex.getTextToSpeechEnabled());
        return context;
    }
}
