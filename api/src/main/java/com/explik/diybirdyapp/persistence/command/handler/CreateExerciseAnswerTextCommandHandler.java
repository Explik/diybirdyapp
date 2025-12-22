package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerTextCommand;
import com.explik.diybirdyapp.persistence.command.CreateTextContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateExerciseAnswerTextCommandHandler implements CommandHandler<CreateExerciseAnswerTextCommand> {
    private final GraphTraversalSource traversalSource;
    private final CommandHandler<CreateTextContentVertexCommand> createTextContentVertexCommandHandler;

    public CreateExerciseAnswerTextCommandHandler(
            @Autowired GraphTraversalSource traversalSource,
            @Autowired CommandHandler<CreateTextContentVertexCommand> createTextContentVertexCommandHandler) {
        this.traversalSource = traversalSource;
        this.createTextContentVertexCommandHandler = createTextContentVertexCommandHandler;
    }

    @Override
    public void handle(CreateExerciseAnswerTextCommand command) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, command.getExerciseId());
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, command.getSessionId());

        // Determine language from exercise content
        LanguageVertex languageVertex;
        var content = exerciseVertex.getContent();
        if (content instanceof TextContentVertex textContentVertex) {
            languageVertex = textContentVertex.getLanguage();
        }
        else if (content instanceof FlashcardVertex flashcardVertex){
            var flashcardSide = exerciseVertex.getFlashcardSide();
            var textContentVertex = (TextContentVertex)flashcardVertex.getSide(flashcardSide);
            languageVertex = textContentVertex.getLanguage();
        }
        else if (content instanceof AudioContentVertex) {
            throw new RuntimeException("Audio content not supported for text answers");
        }
        else {
            throw new RuntimeException("Unsupported content type for exercise: " + content.getClass().getSimpleName());
        }

        // Create text content for the answer
        var textId = UUID.randomUUID().toString();
        var createTextCommand = new CreateTextContentVertexCommand();
        createTextCommand.setId(textId);
        createTextCommand.setValue(command.getText());
        createTextCommand.setLanguage(languageVertex.getId());
        createTextContentVertexCommandHandler.handle(createTextCommand);
        var textVertex = TextContentVertex.findById(traversalSource, textId);

        // Create exercise answer vertex
        var answerId = (command.getId() != null) ? command.getId() : UUID.randomUUID().toString();
        var exerciseAnswerVertex = ExerciseAnswerVertex.create(traversalSource);
        exerciseAnswerVertex.setId(answerId);
        exerciseAnswerVertex.setExercise(exerciseVertex);
        exerciseAnswerVertex.setSession(sessionVertex);
        exerciseAnswerVertex.setContent(textVertex);
    }
}
