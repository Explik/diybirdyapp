package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateRecognizabilityRatingVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.RecognizabilityRatingVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateRecognizabilityRatingVertexCommandHandler implements AtomicCommandHandler<CreateRecognizabilityRatingVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateRecognizabilityRatingVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateRecognizabilityRatingVertexCommand command) {
        var vertex = RecognizabilityRatingVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setRating(command.getRating());
    }
}
