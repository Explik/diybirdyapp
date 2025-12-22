package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.persistence.command.CreateLanguageConfigCommand;
import com.explik.diybirdyapp.persistence.generalCommand.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.service.helper.ConfigurationMappingHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateLanguageConfigCommandHandler implements SyncCommandHandler<CreateLanguageConfigCommand, ConfigurationDto> {
    private final GraphTraversalSource traversalSource;

    public CreateLanguageConfigCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ConfigurationDto handle(CreateLanguageConfigCommand command) {
        var languageId = command.getLanguageId();
        var configModel = command.getConfig();

        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        if (configModel.getId() == null)
            configModel.setId(UUID.randomUUID().toString());

        var configurationVertex = ConfigurationVertex.create(traversalSource);
        configurationVertex.setId(configModel.getId());
        ConfigurationMappingHelper.updateConfigVertex(configurationVertex, configModel);
        configurationVertex.addLanguage(languageVertex);

        return ConfigurationMappingHelper.createConfigModel(languageId, configurationVertex);
    }
}
