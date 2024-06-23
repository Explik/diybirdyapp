package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.ExerciseAnswer;
import com.explik.diybirdyapp.graph.model.ExerciseFeedback;
import com.explik.diybirdyapp.graph.model.FirstAnswerExerciseFeedback;
import com.explik.diybirdyapp.graph.model.LastAnswerExerciseFeedback;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;

@Component
public class GetExerciseFeedbackQuery implements GenericQuery<GetExerciseFeedbackQuery.Options, ExerciseFeedback> {
    @Autowired
    private GetGenericExerciseAnswerQueryMapper mapper;

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public ExerciseFeedback apply(GraphTraversalSource traversal, Options options) {
        String exerciseId = options.getExerciseId();
        String answerId = options.getExerciseAnswer().getId();

        var otherAnswerVertices = traversal.V()
                .hasLabel("exercise")
                .has("id", exerciseId)
                .out("hasAnswer")
                .not(has("id", answerId))
                .toList();

        if (otherAnswerVertices.isEmpty())
            return new FirstAnswerExerciseFeedback();

        var exerciseAnswer = mapper.apply(traversal, otherAnswerVertices.getLast());
        return new LastAnswerExerciseFeedback(exerciseAnswer);
    }

    public static class Options {
        public final String exerciseId;
        public final ExerciseAnswer exerciseAnswer;

        public Options(String exerciseId, ExerciseAnswer exerciseAnswer) {
            this.exerciseId = exerciseId;
            this.exerciseAnswer = exerciseAnswer;
        }

        public String getExerciseId() {
            return exerciseId;
        }

        public ExerciseAnswer getExerciseAnswer() {
            return exerciseAnswer;
        }
    }
}
