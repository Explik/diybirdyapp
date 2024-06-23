package com.explik.diybirdyapp.graph.operation;
import com.explik.diybirdyapp.graph.model.ExerciseAnswer;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AddGenericExerciseAnswerCommand implements GenericCommand<AddGenericExerciseAnswerCommand.Options> {
    private final Map<String, GenericCommand<Options>> commands = new HashMap<>();

    public AddGenericExerciseAnswerCommand(@Autowired List<GenericCommand<Options>> commands) {
        commands.forEach(c -> this.commands.put(c.getIdentifier(), c));
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public void execute(GraphTraversalSource g, AddGenericExerciseAnswerCommand.Options options) {
        var answerType = options.getAnswer().getAnswerType();

        if (!commands.containsKey(answerType))
            throw new IllegalArgumentException("Unsupported exercise type " + answerType);

        commands.get(answerType).execute(g, options);
    }

    public static class Options {
        private final String exerciseId;
        private final ExerciseAnswer answer;

        public Options(String exerciseId, ExerciseAnswer exerciseAnswer) {
            this.exerciseId = exerciseId;
            this.answer = exerciseAnswer;
        }

        public ExerciseAnswer getAnswer() {
            return answer;
        }

        public String getExerciseId() {
            return exerciseId;
        }
    }
}