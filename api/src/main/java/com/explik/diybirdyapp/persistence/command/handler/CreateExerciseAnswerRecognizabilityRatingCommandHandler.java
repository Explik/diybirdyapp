package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerRecognizabilityRatingCommand;
import com.explik.diybirdyapp.persistence.command.CreateRecognizabilityRatingVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.RecognizabilityRatingVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateExerciseAnswerRecognizabilityRatingCommandHandler implements CommandHandler<CreateExerciseAnswerRecognizabilityRatingCommand> {
    private final GraphTraversalSource traversalSource;
    private final CommandHandler<CreateRecognizabilityRatingVertexCommand> createRecognizabilityRatingVertexCommandHandler;

    public CreateExerciseAnswerRecognizabilityRatingCommandHandler(
            @Autowired GraphTraversalSource traversalSource,
            @Autowired CommandHandler<CreateRecognizabilityRatingVertexCommand> createRecognizabilityRatingVertexCommandHandler) {
        this.traversalSource = traversalSource;
        this.createRecognizabilityRatingVertexCommandHandler = createRecognizabilityRatingVertexCommandHandler;
    }

    @Override
    public void handle(CreateExerciseAnswerRecognizabilityRatingCommand command) {
        var exerciseVertex = ExerciseVertex.getById(traversalSource, command.getExerciseId());
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, command.getSessionId());

        // Create recognizability rating
        var ratingId = UUID.randomUUID().toString();
        var createRatingCommand = new CreateRecognizabilityRatingVertexCommand();
        createRatingCommand.setId(ratingId);
        createRatingCommand.setRating(command.getRating());
        createRecognizabilityRatingVertexCommandHandler.handle(createRatingCommand);
        var ratingVertex = RecognizabilityRatingVertex.findById(traversalSource, ratingId);

        // Create exercise answer vertex
        var answerId = (command.getId() != null) ? command.getId() : UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        answerVertex.setSession(sessionVertex);
        answerVertex.setRecognizabilityRating(ratingVertex);
    }
}
