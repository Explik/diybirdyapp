package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateExerciseFeedbackCommand;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseFeedbackVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateExerciseFeedbackCommandHandler implements CommandHandler<CreateExerciseFeedbackCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateExerciseFeedbackCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateExerciseFeedbackCommand command) {
        // Fetch the answer vertex
        var answerVertex = ExerciseAnswerVertex.getById(traversalSource, command.getExerciseAnswerId());

        // Create feedback vertex
        var feedbackVertex = ExerciseFeedbackVertex.create(traversalSource);
        feedbackVertex.setType(command.getType());
        feedbackVertex.setStatus(command.getStatus());
        feedbackVertex.setAnswer(answerVertex);
    }
}
