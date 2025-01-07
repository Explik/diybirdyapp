package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.WRITE_TRANSLATED_SENTENCE + ComponentTypes.VERTEX_FACTORY)
public class ExerciseWriteTranslatedSentenceVertexFactory implements VertexFactory<ExerciseVertex, ExerciseWriteTranslatedSentenceVertexFactory.Options> {
    @Override
    public ExerciseVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(ExerciseVertex.LABEL).next();
        var vertex = new ExerciseVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setType("write-translated-sentence-exercise");
        vertex.setTargetLanguage(options.targetLanguage);
        vertex.setContent(options.content);

        // Make the content vertex static so it can't be changed later
        options.content.makeStatic();

        return vertex;
    }

    public record Options (String id, String targetLanguage, ContentVertex content) {}
}
