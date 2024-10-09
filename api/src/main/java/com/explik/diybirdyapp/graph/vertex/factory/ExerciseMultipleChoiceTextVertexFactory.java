package com.explik.diybirdyapp.graph.vertex.factory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.graph.vertex.AbstractVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseTypes.MULTIPLE_CHOICE_TEXT + ComponentTypes.VERTEX_FACTORY)
public class ExerciseMultipleChoiceTextVertexFactory implements VertexFactory<ExerciseVertex, ExerciseMultipleChoiceTextVertexFactory.Options> {

    @Override
    public ExerciseVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(ExerciseVertex.LABEL).next();
        var vertex = new ExerciseVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setType("multiple-choice-text-exercise");

        for(var option : options.options) {
            vertex.addOption(option);
        }
        vertex.setCorrectOption(options.correctOption);

        return vertex;
    }

    public record Options (String id, List<AbstractVertex> options, AbstractVertex correctOption) {}
}
