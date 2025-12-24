package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.DetachLanguageConfigCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetachLanguageConfigCommandHandler implements CommandHandler<DetachLanguageConfigCommand> {
    private final GraphTraversalSource traversalSource;

    public DetachLanguageConfigCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(DetachLanguageConfigCommand command) {
        var languageId = command.getLanguageId();
        var configId = command.getConfigId();

        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        var configurationVertex = ConfigurationVertex.findById(traversalSource, configId);
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + configId + " does not exist");

        configurationVertex.removeLanguage(languageVertex);
    }
}
