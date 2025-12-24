package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateWordVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.WordVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateWordVertexCommandHandler implements CommandHandler<CreateWordVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateWordVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateWordVertexCommand command) {
        var graphVertex = traversalSource.addV(WordVertex.LABEL).next();
        var vertex = new WordVertex(traversalSource, graphVertex);
        vertex.setId(command.getId());
        vertex.setValues(new String[] { command.getValue() });
        var mainExample = com.explik.diybirdyapp.persistence.vertex.TextContentVertex.findById(traversalSource, command.getMainExampleId());
        vertex.addExample(mainExample);
        vertex.setTextContent(mainExample);
        var languageVertex = LanguageVertex.findById(traversalSource, command.getLanguageVertexId());
        vertex.setLanguage(languageVertex);

        // Make the main example vertex static so it can't be changed later
        mainExample.makeStatic();
    }
}
