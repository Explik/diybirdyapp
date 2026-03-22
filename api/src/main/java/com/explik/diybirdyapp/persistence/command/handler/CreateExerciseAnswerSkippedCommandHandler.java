package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerSkippedCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseAnswerCommandHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateExerciseAnswerSkippedCommandHandler implements CommandHandler<CreateExerciseAnswerSkippedCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateExerciseAnswerSkippedCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateExerciseAnswerSkippedCommand command) {
        ExerciseAnswerCommandHelper.createSkippedAnswer(
                traversalSource,
                command.getId(),
                command.getExerciseId(),
                command.getSessionId());
    }
}