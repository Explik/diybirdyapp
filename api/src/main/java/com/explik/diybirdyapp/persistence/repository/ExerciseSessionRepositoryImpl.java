package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.exercise.*;
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

import java.util.ArrayList;
import java.util.List;

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

        updateSessionOptions(sessionOptions, config);

        return sessionModelFactory.create(sessionVertex);
    }

    private ExerciseSessionVertex getSessionVertex(String modelId) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + modelId);
        return sessionVertex;
    }

    private void updateSessionOptions(ExerciseSessionOptionsVertex vertex, ExerciseSessionOptionsModel model) {
        switch (model) {
            case ExerciseSessionOptionsLearnFlashcardModel learnModel ->
                    updateLearnSessionOptions(vertex, learnModel);
            case ExerciseSessionOptionsReviewFlashcardsModel reviewModel ->
                    updateReviewSessionOptions(vertex, reviewModel);
            case ExerciseSessionOptionsSelectFlashcardsModel selectModel ->
                    updateSelectSessionOptions(vertex, selectModel);
            case ExerciseSessionOptionsWriteFlashcardsModel writeModel ->
                    updateSelectWriteOptions(vertex, writeModel);
            default ->
                    throw new IllegalArgumentException("Unsupported ExerciseSessionOptionsModel type: " + model.getClass().getName());
        }

        updateCommonOptions(vertex, model);
    }

    private void updateLearnSessionOptions(ExerciseSessionOptionsVertex vertex, ExerciseSessionOptionsLearnFlashcardModel model) {
        if (model.getAnswerLanguageIds() != null && model.getAnswerLanguageIds().length > 0) {
            var languages = getLanguagesByIds(model.getAnswerLanguageIds());

            vertex.getAnswerLanguages().forEach(vertex::removeAnswerLanguage);
            languages.forEach(vertex::addAnswerLanguage);
        }

        if (model.getRetypeCorrectAnswerEnabled() != vertex.getRetypeCorrectAnswer())
            vertex.setRetypeCorrectAnswer(model.getRetypeCorrectAnswerEnabled());
    }

    private void updateReviewSessionOptions(ExerciseSessionOptionsVertex vertex, ExerciseSessionOptionsReviewFlashcardsModel model) {
        if (model.getInitialFlashcardLanguageId() != null)
            vertex.setInitialFlashcardLanguageId(model.getInitialFlashcardLanguageId());
    }

    private void updateSelectSessionOptions(ExerciseSessionOptionsVertex vertex, ExerciseSessionOptionsSelectFlashcardsModel model) {
        if (model.getInitialFlashcardLanguageId() != null)
            vertex.setInitialFlashcardLanguageId(model.getInitialFlashcardLanguageId());
        if (model.getTextToSpeechEnabled() != vertex.getTextToSpeechEnabled())
            vertex.setTextToSpeechEnabled(model.getTextToSpeechEnabled());
    }

    private void updateSelectWriteOptions(ExerciseSessionOptionsVertex vertex, ExerciseSessionOptionsWriteFlashcardsModel model) {
        if (model.getAnswerLanguageId() != null) {
            var language = getLanguageById(model.getAnswerLanguageId());

            vertex.getAnswerLanguages().forEach(vertex::removeAnswerLanguage);
            vertex.addAnswerLanguage(language);
        }

        if (model.getRetypeCorrectAnswerEnabled() != vertex.getRetypeCorrectAnswer())
            vertex.setRetypeCorrectAnswer(model.getRetypeCorrectAnswerEnabled());
    }

    private void updateCommonOptions(ExerciseSessionOptionsVertex vertex, ExerciseSessionOptionsModel model) {
        if (model.getTextToSpeechEnabled() != vertex.getTextToSpeechEnabled())
            vertex.setTextToSpeechEnabled(model.getTextToSpeechEnabled());
    }

    private LanguageVertex getLanguageById(String languageId) {
        var vertex = LanguageVertex.findById(traversalSource, languageId);
        if (vertex == null)
            throw new IllegalArgumentException("No language with id " + languageId);
        return vertex;
    }

    private List<LanguageVertex> getLanguagesByIds(String[] languageIds) {
        var buffer = new ArrayList<LanguageVertex>();
        for (var languageId : languageIds) {
            var vertex = LanguageVertex.findById(traversalSource, languageId);
            if (vertex == null)
                throw new IllegalArgumentException("No language with id " + languageId);
            buffer.add(vertex);
        }
        return buffer;
    }
}
