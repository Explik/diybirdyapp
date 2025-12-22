package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.command.CreateSpeechToTextConfigVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateSpeechToTextConfigVertexCommandHandler implements CommandHandler<CreateSpeechToTextConfigVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateSpeechToTextConfigVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateSpeechToTextConfigVertexCommand command) {
        var vertex = ConfigurationVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setType(ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT);
        vertex.setPropertyValue("languageCode", command.getLanguageCode());
        vertex.addLanguage(command.getLanguageVertex());
    }
}
