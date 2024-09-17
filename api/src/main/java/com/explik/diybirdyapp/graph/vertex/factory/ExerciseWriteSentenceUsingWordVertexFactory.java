package com.explik.diybirdyapp.graph.vertex.factory;

import com.explik.diybirdyapp.graph.vertex.AbstractVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class ExerciseWriteSentenceUsingWordVertexFactory implements VertexFactory<ExerciseVertex, ExerciseWriteSentenceUsingWordVertexFactory.Options> {
    @Override
    public ExerciseVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(ExerciseVertex.LABEL).next();
        var vertex = new ExerciseVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setType("write-sentence-using-word-exercise");
        vertex.setTargetLanguage(options.targetLanguage);
        vertex.setContent(options.content);

        return vertex;
    }

    public record Options (String id, String targetLanguage, AbstractVertex content) {}
}
