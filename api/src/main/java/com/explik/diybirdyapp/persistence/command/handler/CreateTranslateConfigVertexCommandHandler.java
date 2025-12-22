package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.command.CreateTranslateConfigVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateTranslateConfigVertexCommandHandler implements CommandHandler<CreateTranslateConfigVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateTranslateConfigVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateTranslateConfigVertexCommand command) {
        var vertex = ConfigurationVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setType(ConfigurationTypes.GOOGLE_TRANSLATE);
        vertex.setPropertyValue("languageCode", command.getLanguageCode());
        vertex.addLanguage(command.getLanguageVertex());
    }
}
