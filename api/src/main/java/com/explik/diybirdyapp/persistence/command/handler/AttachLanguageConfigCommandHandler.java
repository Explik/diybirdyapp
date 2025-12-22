package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.persistence.command.AttachLanguageConfigCommand;
import com.explik.diybirdyapp.persistence.generalCommand.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.service.helper.ConfigurationMappingHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttachLanguageConfigCommandHandler implements SyncCommandHandler<AttachLanguageConfigCommand, ConfigurationDto> {
    private final GraphTraversalSource traversalSource;

    public AttachLanguageConfigCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ConfigurationDto handle(AttachLanguageConfigCommand command) {
        var languageId = command.getLanguageId();
        var configId = command.getConfigId();

        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        var configurationVertex = ConfigurationVertex.findById(traversalSource, configId);
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + configId + " does not exist");

        configurationVertex.addLanguage(languageVertex);

        return ConfigurationMappingHelper.createConfigModel(languageId, configurationVertex);
    }
}
