package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.query.modelFactory.ContextualModelFactory;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.query.GetAllExercisesQuery;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllExercisesQueryHandler implements QueryHandler<GetAllExercisesQuery, List<ExerciseDto>> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    private GenericProvider<ContextualModelFactory<ExerciseVertex, ExerciseDto, ExerciseRetrievalContext>> exerciseModelFactoryProvider;

    @Override
    public List<ExerciseDto> handle(GetAllExercisesQuery query) {
        // Null indicates generic exercise model factory
        var factory = exerciseModelFactoryProvider.get(null);
        var vertices = ExerciseVertex.getAll(traversalSource);

        return vertices
                .stream()
                .map(e -> factory.create(e, ExerciseRetrievalContext.createDefault()))
                .toList();
    }
}
