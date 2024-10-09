package com.explik.diybirdyapp.graph.vertex.manager;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.model.ExerciseSessionModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public interface ExerciseSessionManager {
    ExerciseSessionModel init(GraphTraversalSource traversalSource, ExerciseSessionModel model);

    ExerciseModel next(GraphTraversalSource traversalSource, String modelId);
}
