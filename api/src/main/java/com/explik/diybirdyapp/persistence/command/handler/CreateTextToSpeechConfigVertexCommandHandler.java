package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.command.CreateTextToSpeechConfigVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateTextToSpeechConfigVertexCommandHandler implements AtomicCommandHandler<CreateTextToSpeechConfigVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateTextToSpeechConfigVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateTextToSpeechConfigVertexCommand command) {
        var vertex = ConfigurationVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setType(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        vertex.setPropertyValue("languageCode", command.getLanguageCode());
        vertex.setPropertyValue("voiceName", command.getVoiceName());
        vertex.addLanguage(command.getLanguageVertex());
    }
}
