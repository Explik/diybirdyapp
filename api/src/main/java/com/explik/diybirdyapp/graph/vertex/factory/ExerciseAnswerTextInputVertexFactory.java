package com.explik.diybirdyapp.graph.vertex.factory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseAnswerTypes;
import com.explik.diybirdyapp.graph.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseAnswerVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseAnswerTypes.TEXT + ComponentTypes.VERTEX_FACTORY)
public class ExerciseAnswerTextInputVertexFactory implements VertexFactory<ExerciseAnswerVertex, ExerciseAnswerModel> {
    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseAnswerModel exerciseAnswerModel) {
        var graphVertex = traversalSource.addV(ExerciseAnswerVertex.LABEL).next();
        var vertex = new ExerciseAnswerVertex(traversalSource, graphVertex);
        vertex.setId(exerciseAnswerModel.getId());
        vertex.setType(ExerciseAnswerTypes.TEXT);
        // TODO set text content
        return vertex;
    }
}
