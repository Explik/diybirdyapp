package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateLanguageVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateLanguageVertexCommandHandler implements AtomicCommandHandler<CreateLanguageVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateLanguageVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateLanguageVertexCommand command) {
        var graphVertex = traversalSource.addV(LanguageVertex.LABEL).next();
        var vertex = new LanguageVertex(traversalSource, graphVertex);
        vertex.setId(command.getId());
        vertex.setName(command.getName());
        vertex.setIsoCode(command.getIsoCode());
    }
}
