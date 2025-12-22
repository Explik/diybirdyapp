package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.UpdateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateAudioContentVertexCommandHandler implements AtomicCommandHandler<UpdateAudioContentVertexCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateAudioContentVertexCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateAudioContentVertexCommand command) {
        var vertex = AudioContentVertex.getById(traversalSource, command.getId());
        if (vertex == null)
            throw new RuntimeException("AudioContentVertex with id " + command.getId() + " not found.");

        if (vertex.isStatic())
            vertex = copy(vertex);

        if (command.getUrl() != null)
            vertex.setUrl(command.getUrl());
        if (command.getLanguageVertex() != null)
            vertex.setLanguage(command.getLanguageVertex());
    }

    private AudioContentVertex copy(AudioContentVertex existingVertex) {
        var vertex = AudioContentVertex.create(traversalSource);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(existingVertex.getUrl());
        vertex.setLanguage(existingVertex.getLanguage());

        return vertex;
    }
}
