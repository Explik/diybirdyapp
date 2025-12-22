package com.explik.diybirdyapp.persistence.generalCommand;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HandleFlashcardPronunciationExerciseAnswerCommandHandler implements AsyncCommandHandler<HandleFlashcardPronunciationExerciseAnswerCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandCommandHandler;

    @Override
    public void handleAsync(HandleFlashcardPronunciationExerciseAnswerCommand command) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, command.getExerciseId());
        if (!exerciseVertex.getExerciseType().getId().equals(ExerciseTypes.PRONOUNCE_FLASHCARD))
            throw new RuntimeException("Exercise is not a pronunciation exercise");

        var textContentVertex = (TextContentVertex)exerciseVertex.getContent();

        var createCommand = new CreatePronunciationVertexCommand();
        createCommand.setId(command.getAnswerId());
        // TODO : change to audio content vertex
        createCommand.setSourceVertex(textContentVertex);
        createPronunciationVertexCommandCommandHandler.handle(createCommand);
    }
}
