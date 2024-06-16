package com.explik.diybirdyapp.converter;

import com.explik.diybirdyapp.model.WriteSentenceUsingWordExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

@Component
public class WriteSentenceUsingWordExerciseConverter implements ExerciseConverter<WriteSentenceUsingWordExercise> {
    @Override
    public String getExerciseType() {
        return "write-sentence-using-word-exercise";
    }

    @Override
    public WriteSentenceUsingWordExercise convert(GraphTraversalSource g, Vertex vertex) {
        WriteSentenceUsingWordExercise exercise = new WriteSentenceUsingWordExercise();
        exercise.setId(vertex.value("id"));

        // Retrieve the connected Text vertex
        Vertex textVertex = g.V(vertex).out("basedOn").next();
        exercise.setWord(textVertex.value("value").toString());

        return exercise;
    }
}
