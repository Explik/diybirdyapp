package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.modelFactory.ModelFactory;
import com.explik.diybirdyapp.persistence.operation.ExerciseCreationContext;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperations;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionRepositoryImpl implements ExerciseSessionRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    ModelFactory<ExerciseSessionOptionsVertex, ExerciseSessionOptionsModel> sessionOptionsModelFactory;

    @Autowired
    GenericProvider<ExerciseSessionOperations> sessionOperationProvider;

    public ExerciseSessionRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ExerciseSessionModel add(ExerciseSessionModel model) {
        var sessionType = model.getType();
        var sessionManager = sessionOperationProvider.get(sessionType);

        return sessionManager.init(traversalSource, ExerciseCreationContext.createDefault(model));
    }

    @Override
    public ExerciseSessionModel get(String id) {
        var vertex = getSessionVertex(id);
        return sessionModelFactory.create(vertex);
    }

    public ExerciseSessionModel nextExercise(String modelId) {
        var sessionVertex = getSessionVertex(modelId);
        var sessionType = sessionVertex.getType();
        var sessionManager = sessionOperationProvider.get(sessionType);

        var context = ExerciseCreationContext.createDefault(sessionModelFactory.create(sessionVertex));

        return sessionManager.nextExercise(traversalSource, context);
    }

    @Override
    public ExerciseSessionOptionsModel getConfig(String sessionId) {
        var sessionVertex = getSessionVertex(sessionId);
        var optionsVertex = sessionVertex.getOptions();

        return sessionOptionsModelFactory.create(optionsVertex);
    }

    public ExerciseSessionModel updateConfig(String modelId, ExerciseSessionOptionsModel config) {
        var sessionVertex = getSessionVertex(modelId);
        var sessionOptions = sessionVertex.getOptions();

        if (config.getTextToSpeechEnabled() != sessionOptions.getTextToSpeechEnabled())
            sessionOptions.setTextToSpeechEnabled(config.getTextToSpeechEnabled());
//        if (config.getInitialFlashcardLanguageId() != null && !config.getInitialFlashcardLanguageId().equals(sessionOptions.getInitialFlashcardLanguageId()))
//            sessionOptions.setInitialFlashcardLanguageId(config.getInitialFlashcardLanguageId());
//        if (config.getRetypeCorrectAnswerEnabled() != sessionOptions.getRetypeCorrectAnswer())
//            sessionOptions.setRetypeCorrectAnswer(config.getRetypeCorrectAnswerEnabled());
//        if (config.getAnswerLanguageIds() != null && config.getAnswerLanguageIds().length > 0) {
//            for(var langVertex : sessionOptions.getAnswerLanguages())
//                sessionOptions.removeAnswerLanguage(langVertex);
//
//            for (var langId : config.getAnswerLanguageIds()) {
//                var langVertex = LanguageVertex.findById(traversalSource, langId);
//                if (langVertex == null)
//                    throw new IllegalArgumentException("No language with id " + langId);
//                sessionOptions.addAnswerLanguage(langVertex);
//            }
        // }

        return sessionModelFactory.create(sessionVertex);
    }

    private ExerciseSessionVertex getSessionVertex(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);
        return sessionVertex;
    }
}
