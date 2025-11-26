package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ExerciseTypeVertex extends AbstractVertex{
    public ExerciseTypeVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "exerciseType";

    public final static String PROPERTY_ID = "id";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public static ExerciseTypeVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new ExerciseTypeVertex(traversalSource, vertex);
    }

    public static ExerciseTypeVertex findById(GraphTraversalSource traversalSource, String id) {
        var vertex = traversalSource.V().hasLabel(LABEL).has(PROPERTY_ID, id).tryNext();
        return vertex.map(value -> new ExerciseTypeVertex(traversalSource, value)).orElse(null);
    }
}
