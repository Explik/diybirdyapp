package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateAudioContentVertexCommandHandler implements CommandHandler<CreateAudioContentVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateAudioContentVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateAudioContentVertexCommand options) {
        var vertex = AudioContentVertex.create(traversalSource);
        vertex.setId(options.getId());
        vertex.setUrl(options.getUrl());
        
        var languageVertex = LanguageVertex.findById(traversalSource, options.getLanguageVertexId());
        vertex.setLanguage(languageVertex);
    }
}
