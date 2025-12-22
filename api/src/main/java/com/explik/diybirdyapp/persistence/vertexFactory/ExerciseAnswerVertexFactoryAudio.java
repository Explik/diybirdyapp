package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseInputRecordAudioDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseAnswerVertexFactoryAudio implements VertexFactory<ExerciseAnswerVertex, ExerciseAnswerModel<ExerciseInputRecordAudioDto>> {
    @Autowired
    CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler;

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseAnswerModel<ExerciseInputRecordAudioDto> answerModel) {
        var answerInput = answerModel.getInput();

        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answerModel.getSessionId());

        var flashcardContent = exerciseVertex.getFlashcardContent();
        var textContent = (TextContentVertex)flashcardContent.getLeftContent();
        var language = textContent.getLanguage();

        var audioId = UUID.randomUUID().toString();
        var createAudioCommand = new CreateAudioContentVertexCommand();
        createAudioCommand.setId(audioId);
        createAudioCommand.setUrl(answerInput.getUrl());
        createAudioCommand.setLanguageVertex(language);
        createAudioContentVertexCommandHandler.handle(createAudioCommand);

        var audioVertex = AudioContentVertex.getById(traversalSource, audioId);

        var answerId = (answerInput.getId() != null) ? answerInput.getId() : UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        answerVertex.setSession(sessionVertex);
        answerVertex.setContent(audioVertex);

        return answerVertex;
    }
}
