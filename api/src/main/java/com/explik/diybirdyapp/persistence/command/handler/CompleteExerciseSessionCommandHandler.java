package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CompleteExerciseSessionCommand;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompleteExerciseSessionCommandHandler implements CommandHandler<CompleteExerciseSessionCommand> {
    private final GraphTraversalSource traversalSource;

    public CompleteExerciseSessionCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CompleteExerciseSessionCommand command) {
        var sessionId = command.getSessionId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, sessionId);
        if (sessionVertex == null)
            throw new IllegalArgumentException("No session with id " + sessionId);

        sessionVertex.setCompleted(true);
    }
}