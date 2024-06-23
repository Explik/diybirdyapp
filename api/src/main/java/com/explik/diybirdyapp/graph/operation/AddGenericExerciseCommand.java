package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.Exercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AddGenericExerciseCommand implements GenericCommand<AddGenericExerciseCommand.Options> {
    private final Map<String, GenericCommand<AddGenericExerciseCommand.Options>> commands = new HashMap<>();

    public AddGenericExerciseCommand(@Autowired List<GenericCommand<Options>> commands) {
        commands.forEach(c -> this.commands.put(c.getIdentifier(), c));
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public void execute(GraphTraversalSource g, AddGenericExerciseCommand.Options options) {
        var exerciseType = options.getExercise().getExerciseType();

        if (!commands.containsKey(exerciseType))
            throw new IllegalArgumentException("Unsupported exercise type " + exerciseType);

        commands.get(exerciseType).execute(g, options);
    }

    public static class Options {
        private final Exercise<?> exercise;

        public Options(Exercise<?> exercise) {
            this.exercise = exercise;
        }

        public Exercise<?> getExercise() {
            return exercise;
        }
    }
}


