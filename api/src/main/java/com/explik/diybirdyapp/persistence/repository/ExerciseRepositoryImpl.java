package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContextProvider;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.modelFactory.ContextualModelFactory;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.query.GetAllExercisesQuery;
import com.explik.diybirdyapp.persistence.query.GetExerciseByIdsQuery;
import com.explik.diybirdyapp.persistence.query.handler.GetAllExercisesQueryHandler;
import com.explik.diybirdyapp.persistence.query.handler.GetExerciseByIdsQueryHandler;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
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
    private QueryHandler<GetExerciseByIdsQuery, ExerciseDto> getExerciseByIdsQueryHandler;

    @Autowired
    private QueryHandler<GetAllExercisesQuery, List<ExerciseDto>> getAllExercisesQueryHandler;

    @Autowired
    private GenericProvider<ContextualModelFactory<ExerciseVertex, ExerciseDto, ExerciseRetrievalContext>> exerciseModelFactoryProvider;

    @Autowired
    private GenericProvider<ExerciseEvaluationStrategy> evaluationStrategyProvider;

    public ExerciseRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ExerciseDto get(String id, String sessionId) {
        var query = new GetExerciseByIdsQuery();
        query.setId(id);
        query.setSessionId(sessionId);

        return getExerciseByIdsQueryHandler.handle(query);
    }

    @Override
    public List<ExerciseDto> getAll() {
        var query = new GetAllExercisesQuery();
        return getAllExercisesQueryHandler.handle(query);
    }

    @Override
    public ExerciseDto submitAnswer(ExerciseAnswerModel answer) {
        assert answer != null;
        assert answer.getExerciseId() != null;

        var exerciseVertex = ExerciseVertex.getById(traversalSource, answer.getExerciseId());
        var exerciseType = exerciseVertex.getExerciseType().getId();
        var strategy = evaluationStrategyProvider.get(exerciseType);
        var strategyContext = getEvaluationContext(answer);

        return strategy.evaluate(exerciseVertex, strategyContext);
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
