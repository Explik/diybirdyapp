package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.Exercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetGenericExerciseQuery implements GenericQuery<GetGenericExerciseQuery.Options, Exercise<?>> {
    @Autowired
    private GetGenericExerciseQueryMapper mapper;

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public Exercise<?> apply(GraphTraversalSource traversal, Options options) {
        var exerciseId = options.getExerciseId();
        var exerciseVertex = traversal.V()
                .hasLabel("exercise")
                .has("id", exerciseId)
                .tryNext();

        if (exerciseVertex.isEmpty())
            throw new IllegalArgumentException("Exercise with id " + exerciseId + " was not found");

        return mapper.apply(traversal, exerciseVertex.get());
    }

    public static class Options {
        private final String exerciseId;

        public Options(String exerciseId) {
            this.exerciseId = exerciseId;
        }

        public String getExerciseId() {
            return exerciseId;
        }
    }
}
