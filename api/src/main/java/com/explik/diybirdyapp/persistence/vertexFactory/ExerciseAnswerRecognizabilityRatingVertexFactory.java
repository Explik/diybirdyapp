package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseAnswerTypes;
import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component(ExerciseAnswerTypes.RECOGNIZABILITY_RATING + ComponentTypes.VERTEX_FACTORY)
public class ExerciseAnswerRecognizabilityRatingVertexFactory implements VertexFactory<ExerciseAnswerVertex, ExerciseAnswerModel> {

    @Override
    public ExerciseAnswerVertex create(GraphTraversalSource traversalSource, ExerciseAnswerModel exerciseAnswerModel) {
        var graphVertex = traversalSource.addV(ExerciseAnswerVertex.LABEL).next();
        var vertex = new ExerciseAnswerVertex(traversalSource, graphVertex);
        vertex.setId(exerciseAnswerModel.getId());
        vertex.setType(ExerciseAnswerTypes.RECOGNIZABILITY_RATING);
        return vertex;
    }
}
