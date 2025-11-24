package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContextProvider;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.modelFactory.ContextualModelFactory;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.strategy.ExerciseEvaluationContext;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.strategy.ExerciseEvaluationStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExerciseRepositoryImpl implements ExerciseRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    private GenericProvider<ContextualModelFactory<ExerciseVertex, ExerciseDto, ExerciseRetrievalContext>> exerciseModelFactoryProvider;

    @Autowired
    private GenericProvider<ExerciseEvaluationStrategy> evaluationStrategyProvider;

    public ExerciseRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ExerciseDto get(String id, String sessionId) {
        var vertex = ExerciseVertex.getById(traversalSource, id);
        var exerciseType = vertex.getType();
        var exerciseFactory = exerciseModelFactoryProvider.get(exerciseType);

        if (sessionId == null)
            return exerciseFactory.create(vertex, ExerciseRetrievalContext.createDefault());

        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("Session with ID " + sessionId + " does not exist");

        var context = generateRetrievalContext(sessionVertex);
        return exerciseFactory.create(vertex, context);
    }

    @Override
    public List<ExerciseDto> getAll() {
        // Null indicates generic exercise model factory
        var factory = exerciseModelFactoryProvider.get(null);
        var vertices = ExerciseVertex.getAll(traversalSource);

        return vertices
                .stream()
                .map(e -> factory.create(e, ExerciseRetrievalContext.createDefault()))
                .toList();
    }

    @Override
    public ExerciseDto submitAnswer(ExerciseAnswerModel answer) {
        assert answer != null;
        assert answer.getExerciseId() != null;

        var exerciseVertex = ExerciseVertex.getById(traversalSource, answer.getExerciseId());
        var exerciseType = exerciseVertex.getType();
        var strategy = evaluationStrategyProvider.get(exerciseType);
        var strategyContext = getEvaluationContext(answer);

        return strategy.evaluate(exerciseVertex, strategyContext);
    }

    private ExerciseRetrievalContext generateRetrievalContext(ExerciseSessionVertex sessionVertex) {
        var provider = new ExerciseRetrievalContextProvider();
        return provider.get(sessionVertex);
    }

    private ExerciseEvaluationContext getEvaluationContext(ExerciseAnswerModel answer) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answer.getSessionId());
        var sessionOptionsVertex = (sessionVertex != null) ? sessionVertex.getOptions() : null;

        var strategyContext = ExerciseEvaluationContext.create(answer);
        if (sessionOptionsVertex != null) {
            strategyContext.setRetypeCorrectAnswerEnabled(sessionOptionsVertex.getRetypeCorrectAnswer());
            strategyContext.setAlgorithm(sessionOptionsVertex.getAlgorithm());
        }

        return strategyContext;
    }
}
