package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerAudioCommand;
import com.explik.diybirdyapp.persistence.command.helper.ExerciseAnswerCommandHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateExerciseAnswerAudioCommandHandler implements CommandHandler<CreateExerciseAnswerAudioCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateExerciseAnswerAudioCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateExerciseAnswerAudioCommand command) {
        ExerciseAnswerCommandHelper.createWithAudioContent(
                traversalSource,
                command.getId(),
                command.getExerciseId(),
                command.getSessionId(),
                command.getAudioUrl()
        );
    }
}
