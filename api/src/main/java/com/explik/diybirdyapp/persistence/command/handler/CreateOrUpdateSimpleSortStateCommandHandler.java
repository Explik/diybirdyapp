package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateOrUpdateSimpleSortStateCommand;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionStateVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrUpdateSimpleSortStateCommandHandler implements CommandHandler<CreateOrUpdateSimpleSortStateCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateOrUpdateSimpleSortStateCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateOrUpdateSimpleSortStateCommand command) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, command.getSessionId());
        if (sessionVertex == null)
            throw new IllegalArgumentException("Session with ID " + command.getSessionId() + " does not exist");

        var contentVertex = ContentVertex.getById(traversalSource, command.getContentId());
        if (contentVertex == null)
            throw new IllegalArgumentException("Content with ID " + command.getContentId() + " does not exist");

        var stateVertex = ExerciseSessionStateVertex.findBy(traversalSource, "SimpleSort", contentVertex, sessionVertex);
        if (stateVertex == null) {
            stateVertex = ExerciseSessionStateVertex.create(traversalSource);
            stateVertex.setType("SimpleSort");
            stateVertex.setContent(contentVertex);
            sessionVertex.addState(stateVertex);
        }

        stateVertex.setPropertyValue("pile", command.getPile());
    }
}
