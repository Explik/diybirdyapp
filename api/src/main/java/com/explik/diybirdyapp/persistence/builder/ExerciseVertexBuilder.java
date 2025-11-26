package com.explik.diybirdyapp.persistence.builder;

import java.util.UUID;

import com.explik.diybirdyapp.persistence.vertex.ExerciseTypeVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class ExerciseVertexBuilder extends VertexBuilderBase<ExerciseVertex>  {
    private String id;
    private String type;

    public ExerciseVertexBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ExerciseVertexBuilder withType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public ExerciseVertex build(GraphTraversalSource traversalSource) {
        if (this.factories == null)
            throw new RuntimeException("Factories were not");
        if (this.type == null)
            throw new RuntimeException("Type was not set");

        var id = (this.id != null) ? this.id : UUID.randomUUID().toString();

        var typeVertex = ExerciseTypeVertex.findById(traversalSource, this.type);
        if (typeVertex == null) {
            typeVertex = ExerciseTypeVertex.create(traversalSource);
            typeVertex.setId(this.type);
        }

        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setId(id);
        vertex.setExerciseType(typeVertex);

        return vertex;
    }
}
