package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.WRITE_FLASHCARD + ComponentTypes.VERTEX_FACTORY)
public class ExerciseVertexFactoryWriteFlashcard implements VertexFactory<ExerciseVertex, ExerciseVertexFactoryWriteFlashcard.Options> {
    @Override
    public ExerciseVertex create(GraphTraversalSource traversalSource, Options options) {
        var graphVertex = traversalSource.addV(ExerciseVertex.LABEL).next();
        var vertex = new ExerciseVertex(traversalSource, graphVertex);
        vertex.setId(options.id);
        vertex.setType(ExerciseTypes.WRITE_FLASHCARD);
        vertex.setSession(options.sessionVertex);
        vertex.setContent(options.contentVertex);
        vertex.addCorrectOption(options.answerVertex);

        // Make the flashcard vertex static so it can't be changed later
        options.contentVertex.makeStatic();

        return vertex;
    }

    public record Options (String id, AbstractVertex sessionVertex, ContentVertex contentVertex, ContentVertex answerVertex) { }

}
