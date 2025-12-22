package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseInputWriteTextDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.command.CreateTextContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseAnswerVertexFactoryText implements VertexFactory<ExerciseAnswerVertex, ExerciseAnswerModel<ExerciseInputWriteTextDto>> {
    @Autowired
    private CommandHandler<CreateTextContentVertexCommand> createTextContentVertexCommandHandler;

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseAnswerModel<ExerciseInputWriteTextDto> answerModel) {
        var answerInput = answerModel.getInput();

        var exerciseVertex = ExerciseVertex.getById(traversalSource, answerModel.getExerciseId());
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answerModel.getSessionId());

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
            // TODO implement audio answer handling
            return null;
        }
        else throw new RuntimeException("Unsupported content type for exercise: " + content.getClass().getSimpleName());

        var textId = UUID.randomUUID().toString();
        var createCommand = new CreateTextContentVertexCommand();
        createCommand.setId(textId);
        createCommand.setValue(answerInput.getText());
        createCommand.setLanguage(languageVertex.getId());
        createTextContentVertexCommandHandler.handle(createCommand);
        var textVertex = TextContentVertex.findById(traversalSource, textId);

        var answerId = (answerInput.getId() != null) ? answerInput.getId() : UUID.randomUUID().toString();
        var exerciseAnswerVertex = ExerciseAnswerVertex.create(traversalSource);
        exerciseAnswerVertex.setId(answerId);
        exerciseAnswerVertex.setExercise(exerciseVertex);
        exerciseAnswerVertex.setSession(sessionVertex);
        exerciseAnswerVertex.setContent(textVertex);

        return exerciseAnswerVertex;
    }
}
