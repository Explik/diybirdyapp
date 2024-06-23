package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.Exercise;
import com.explik.diybirdyapp.graph.model.WriteSentenceUsingWordExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

@Component
public class GetWriteSentenceUsingWordExerciseQueryMapper implements GenericQueryMapper<Exercise<?>> {
    @Override
    public String getIdentifier() {
        return "write-sentence-using-word-exercise";
    }

    @Override
    public WriteSentenceUsingWordExercise apply(GraphTraversalSource g, Vertex vertex) {
        WriteSentenceUsingWordExercise exercise = new WriteSentenceUsingWordExercise();
        exercise.setId(vertex.value("id"));

        // Retrieve the connected Text vertex
        Vertex textVertex = g.V(vertex).out("basedOn").next();
        exercise.setWord(textVertex.value("value").toString());

        return exercise;
    }
}
