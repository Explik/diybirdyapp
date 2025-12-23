package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerTextCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseAnswerCommandHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateExerciseAnswerTextCommandHandler implements CommandHandler<CreateExerciseAnswerTextCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateExerciseAnswerTextCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateExerciseAnswerTextCommand command) {
        ExerciseAnswerCommandHelper.createWithTextContent(
                traversalSource,
                command.getId(),
                command.getExerciseId(),
                command.getSessionId(),
                command.getText()
        );
    }
}
