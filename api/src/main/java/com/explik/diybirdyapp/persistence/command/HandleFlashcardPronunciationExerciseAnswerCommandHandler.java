package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.PronunciationVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.VertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HandleFlashcardPronunciationExerciseAnswerCommandHandler implements CommandHandler<HandleFlashcardPronunciationExerciseAnswerCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Override
    public void handle(HandleFlashcardPronunciationExerciseAnswerCommand command) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, command.getExerciseId());
        if (!exerciseVertex.getType().equals(ExerciseTypes.PRONOUNCE_FLASHCARD))
            throw new RuntimeException("Exercise is not a pronunciation exercise");

        var textContentVertex = (TextContentVertex)exerciseVertex.getContent();
        var audioContent = AudioContentVertex.getById(traversalSource, command.getAnswerId());

        pronunciationVertexFactory.create(
                traversalSource,
                new PronunciationVertexFactory.Options(
                        command.getAnswerId(),
                        textContentVertex,
                        audioContent
                )
        );
    }
}
