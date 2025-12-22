package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateFlashcardDeckVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateFlashcardDeckVertexCommandHandler implements CommandHandler<CreateFlashcardDeckVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateFlashcardDeckVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateFlashcardDeckVertexCommand command) {
        var graphVertex = traversalSource.addV(FlashcardDeckVertex.LABEL).next();
        var vertex = new FlashcardDeckVertex(traversalSource, graphVertex);
        vertex.setId(command.getId());
        vertex.setName(command.getName());

        for(var flashcard : command.getFlashcards()) {
            vertex.addFlashcard(flashcard);
        }
    }
}
