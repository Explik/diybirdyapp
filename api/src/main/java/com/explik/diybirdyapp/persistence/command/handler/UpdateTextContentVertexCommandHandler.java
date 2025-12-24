package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.UpdateTextContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateTextContentVertexCommandHandler implements CommandHandler<UpdateTextContentVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateTextContentVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateTextContentVertexCommand command) {
        var vertex = TextContentVertex.findById(traversalSource, command.getId());
        if (vertex == null)
            throw new RuntimeException("TextContentVertex with id " + command.getId() + " not found.");

        if (vertex.isStatic())
            vertex = copy(vertex);

        if (command.getValue() != null)
            vertex.setValue(command.getValue());
        if (command.getLanguageId() != null) {
            var languageVertex = LanguageVertex.findById(traversalSource, command.getLanguageId());
            vertex.setLanguage(languageVertex);
        }
    }

    private TextContentVertex copy(TextContentVertex existingVertex) {
        var vertex = TextContentVertex.create(traversalSource);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setValue(existingVertex.getValue());
        vertex.setLanguage(existingVertex.getLanguage());

        return vertex;
    }
}
