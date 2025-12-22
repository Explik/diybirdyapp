package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.command.UpdateLanguageCommand;
import com.explik.diybirdyapp.persistence.generalCommand.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateLanguageCommandHandler implements SyncCommandHandler<UpdateLanguageCommand, FlashcardLanguageDto> {
    private final GraphTraversalSource traversalSource;

    public UpdateLanguageCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardLanguageDto handle(UpdateLanguageCommand command) {
        var language = command.getLanguage();

        var vertex = LanguageVertex.findById(traversalSource, language.getId());
        if (vertex == null)
            throw new IllegalArgumentException("Language with id " + language.getId() + " does not exist");

        if (language.getName() != null)
            vertex.setName(language.getName());
        if (language.getIsoCode() != null)
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
