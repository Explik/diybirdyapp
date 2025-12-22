package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerAudioCommand;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateExerciseAnswerAudioCommandHandler implements CommandHandler<CreateExerciseAnswerAudioCommand> {
    private final GraphTraversalSource traversalSource;
    private final CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler;

    public CreateExerciseAnswerAudioCommandHandler(
            @Autowired GraphTraversalSource traversalSource,
            @Autowired CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler) {
        this.traversalSource = traversalSource;
        this.createAudioContentVertexCommandHandler = createAudioContentVertexCommandHandler;
    }

    @Override
    public void handle(CreateExerciseAnswerAudioCommand command) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, command.getExerciseId());
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, command.getSessionId());

        // Get language from flashcard content
        var flashcardContent = exerciseVertex.getFlashcardContent();
        var textContent = (TextContentVertex)flashcardContent.getLeftContent();
        var language = textContent.getLanguage();

        // Create audio content
        var audioId = UUID.randomUUID().toString();
        var createAudioCommand = new CreateAudioContentVertexCommand();
        createAudioCommand.setId(audioId);
        createAudioCommand.setUrl(command.getAudioUrl());
        createAudioCommand.setLanguageVertexId(language.getId());
        createAudioContentVertexCommandHandler.handle(createAudioCommand);

        var audioVertex = AudioContentVertex.getById(traversalSource, audioId);

        // Create exercise answer vertex
        var answerId = (command.getId() != null) ? command.getId() : UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        answerVertex.setSession(sessionVertex);
        answerVertex.setContent(audioVertex);
    }
}
