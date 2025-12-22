package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.command.AddLanguageCommand;
import com.explik.diybirdyapp.persistence.generalCommand.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddLanguageCommandHandler implements SyncCommandHandler<AddLanguageCommand, FlashcardLanguageDto> {
    private final GraphTraversalSource traversalSource;

    public AddLanguageCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardLanguageDto handle(AddLanguageCommand command) {
        var language = command.getLanguage();

        // Check for duplicates
        if (LanguageVertex.findById(traversalSource, language.getId()) != null)
            throw new IllegalArgumentException("Language with id " + language.getId() + " already exists");
        if (LanguageVertex.findByName(traversalSource, language.getName()) != null)
            throw new IllegalArgumentException("Language with name " + language.getName() + " already exists");
        if (LanguageVertex.findByIsoCode(traversalSource, language.getIsoCode()) != null)
            throw new IllegalArgumentException("Language with isoCode " + language.getIsoCode() + " already exists");

        // Add language to database
        var vertex = LanguageVertex.create(traversalSource);
        vertex.setId(language.getId());
        vertex.setName(language.getName());
        vertex.setIsoCode(language.getIsoCode());

        return createModel(vertex);
    }

    private FlashcardLanguageDto createModel(LanguageVertex vertex) {
        var model = new FlashcardLanguageDto();
        model.setId(vertex.getId());
        model.setName(vertex.getName());
        model.setIsoCode(vertex.getIsoCode());
        return model;
    }
}
