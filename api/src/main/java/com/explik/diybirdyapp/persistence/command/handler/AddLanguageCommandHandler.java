package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.command.AddLanguageCommand;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddLanguageCommandHandler implements CommandHandler<AddLanguageCommand> {
    private final GraphTraversalSource traversalSource;

    public AddLanguageCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(AddLanguageCommand command) {
        // Check for duplicates
        if (LanguageVertex.findById(traversalSource, command.getId()) != null)
            throw new IllegalArgumentException("Language with id " + command.getId() + " already exists");
        if (LanguageVertex.findByName(traversalSource, command.getName()) != null)
            throw new IllegalArgumentException("Language with name " + command.getName() + " already exists");
        if (LanguageVertex.findByIsoCode(traversalSource, command.getIsoCode()) != null)
            throw new IllegalArgumentException("Language with isoCode " + command.getIsoCode() + " already exists");

        // Add language to database
        var vertex = LanguageVertex.create(traversalSource);
        vertex.setId(command.getId());
        vertex.setName(command.getName());
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
