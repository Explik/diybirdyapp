package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.command.HandleFlashcardPronunciationExerciseAnswerCommand;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Command handler for processing pronunciation exercise answers.
 * Validates the exercise type and creates a pronunciation vertex for the answer.
 */
@Component
public class HandleFlashcardPronunciationExerciseAnswerCommandHandler implements CommandHandler<HandleFlashcardPronunciationExerciseAnswerCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandHandler;

    @Override
    public void handle(HandleFlashcardPronunciationExerciseAnswerCommand command) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, command.getExerciseId());
        if (exerciseVertex == null) {
            throw new RuntimeException("Exercise not found: " + command.getExerciseId());
        }

        if (!exerciseVertex.getExerciseType().getId().equals(ExerciseTypes.PRONOUNCE_FLASHCARD)) {
            throw new RuntimeException("Exercise is not a pronunciation exercise");
        }

        var textContentVertex = (TextContentVertex)exerciseVertex.getContent();

        var createCommand = new CreatePronunciationVertexCommand();
        createCommand.setId(command.getAnswerId());
        // TODO: change to audio content vertex
        createCommand.setSourceVertex(textContentVertex.getId());
        createPronunciationVertexCommandHandler.handle(createCommand);
    }
}
