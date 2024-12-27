package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.persistence.vertex.RecognizabilityRatingVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseInputTypes.RECOGNIZABILITY_RATING + ComponentTypes.VERTEX_FACTORY)
public class RecognizabilityRatingVertexFactory implements VertexFactory<RecognizabilityRatingVertex, RecognizabilityRatingVertexFactory.Options> {
    @Override
    public RecognizabilityRatingVertex create(GraphTraversalSource traversalSource, Options options) {
        var vertex = RecognizabilityRatingVertex.create(traversalSource);
        vertex.setId(options.id);
        vertex.setRating(options.rating);

        return vertex;
    }

    public record Options (String id, String rating) {}
}
