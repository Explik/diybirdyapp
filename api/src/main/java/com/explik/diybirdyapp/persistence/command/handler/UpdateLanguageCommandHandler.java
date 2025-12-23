package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.command.UpdateLanguageCommand;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateLanguageCommandHandler implements CommandHandler<UpdateLanguageCommand> {
    private final GraphTraversalSource traversalSource;

    public UpdateLanguageCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(UpdateLanguageCommand command) {
        var vertex = LanguageVertex.findById(traversalSource, command.getId());
        if (vertex == null)
            throw new IllegalArgumentException("Language with id " + command.getId() + " does not exist");

        if (command.getName() != null)
            vertex.setName(command.getName());
        if (command.getIsoCode() != null)
            vertex.setIsoCode(command.getIsoCode());
        
        // Store result ID for query
        command.setResultId(vertex.getId());
    }

    private FlashcardLanguageDto createModel(LanguageVertex vertex) {
        var model = new FlashcardLanguageDto();
        model.setId(vertex.getId());
        model.setName(vertex.getName());
        model.setIsoCode(vertex.getIsoCode());
        return model;
    }
}
