package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.persistence.command.UpdateConfigurationCommand;
import com.explik.diybirdyapp.persistence.generalCommand.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.service.helper.ConfigurationMappingHelper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateConfigurationCommandHandler implements SyncCommandHandler<UpdateConfigurationCommand, ConfigurationDto> {
    private final GraphTraversalSource traversalSource;

    public UpdateConfigurationCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public ConfigurationDto handle(UpdateConfigurationCommand command) {
        var configModel = command.getConfiguration();

        var configurationVertex = ConfigurationVertex.findById(traversalSource, configModel.getId());
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + configModel.getId() + " does not exist");

        ConfigurationMappingHelper.updateConfigVertex(configurationVertex, configModel);

        return ConfigurationMappingHelper.createConfigModel(null, configurationVertex);
    }
}
