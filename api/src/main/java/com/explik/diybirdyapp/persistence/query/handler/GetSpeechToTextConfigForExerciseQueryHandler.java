package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.query.GetCorrectExerciseAnswerSpeakModelForExerciseQuery;
import com.explik.diybirdyapp.persistence.query.modelFactory.CorrectExerciseAnswerSpeakModel;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetSpeechToTextConfigForExerciseQueryHandler implements QueryHandler<GetCorrectExerciseAnswerSpeakModelForExerciseQuery, CorrectExerciseAnswerSpeakModel> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public CorrectExerciseAnswerSpeakModel handle(GetCorrectExerciseAnswerSpeakModelForExerciseQuery query) {
        // Get exercise vertex
        var exerciseVertex = ExerciseVertex.getById(traversalSource, query.getExerciseId());
        if (exerciseVertex == null)
            throw new IllegalArgumentException("Exercise with id " + query.getExerciseId() + " does not exist");

        // Get correct text options
        var correctOptions = exerciseVertex.getCorrectOptions();
        if (correctOptions.isEmpty())
            throw new IllegalArgumentException("Exercise has no correct options");

        // Get the language from the first correct text option (all should have same language)
        var firstCorrectTextVertex = (TextContentVertex) correctOptions.getFirst();
        var languageVertex = firstCorrectTextVertex.getLanguage();
        if (languageVertex == null)
            throw new IllegalArgumentException("Correct text option has no language");

        // Get correct text values and verify all correct options have the same language
        var correctTextValues = correctOptions.stream()
                .map(option -> {
                    if (option instanceof TextContentVertex textOption) {
                        var optionLanguage = textOption.getLanguage();
                        if (optionLanguage == null || !optionLanguage.getId().equals(languageVertex.getId())) {
                            throw new IllegalArgumentException("All correct text options must have the same language");
                        }
                        return textOption.getValue();
                    }
                    throw new IllegalArgumentException("Correct option is not a text content vertex");
                })
                .toList();

        // Get speech-to-text configuration for the language
        var configVertices = ConfigurationVertex.findByLanguageAndType(languageVertex, ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT);
        if (configVertices.isEmpty())
            throw new IllegalArgumentException("No speech-to-text configuration found for language: " + languageVertex.getId());

        var configVertex = configVertices.getFirst();

        // Create and return model
        var model = new CorrectExerciseAnswerSpeakModel();
        model.setLanguageCode((String) configVertex.getPropertyValue("languageCode"));
        model.setCorrectTextValues(correctTextValues);

        return model;
    }
}
