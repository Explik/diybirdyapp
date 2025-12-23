package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchema;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseParameters;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory for creating exercises using the command pattern.
 * This class delegates to ExerciseCreationService to generate
 * a domain command, then executes it via CommandHandler.
 */
@Component
public class ExerciseAbstractVertexFactory {
    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    public VertexFactory<ExerciseVertex, ExerciseParameters> create(ExerciseSchema schema) {
        return (traversalSource, options) -> create(traversalSource, schema, options);
    }

    private ExerciseVertex create(GraphTraversalSource traversalSource, ExerciseSchema schema, ExerciseParameters options) {
        // Generate the domain command
        var command = exerciseCreationService.createExerciseCommand(schema, options);
        
        // Execute the command
        createExerciseCommandHandler.handle(command);
        
        // Retrieve and return the created exercise vertex
        var exerciseId = options.getId();
        var vertex = ExerciseVertex.getById(traversalSource, exerciseId);
        
        if (vertex == null) {
            throw new RuntimeException("Failed to create exercise with ID: " + exerciseId);
        }
        
        return vertex;
    }
}
