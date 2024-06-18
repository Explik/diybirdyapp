package com.explik.diybirdyapp.converter;

import com.explik.diybirdyapp.model.Exercise;
import com.explik.diybirdyapp.model.WriteSentenceUsingWordExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WriteSentenceUsingWordExerciseConverter implements ExerciseConverter<WriteSentenceUsingWordExercise>{
    @Override
    public String getExerciseType() {
        return "write-sentence-using-word-exercise";
    }

    @Override
    public void create(GraphTraversalSource g, Map<String, Object> exercise) {
        Vertex exerciseVertex = g.addV("exercise")
                .property("id", exercise.get("id"))
                .property("exerciseType", exercise.get("exerciseType"))
                .next();

        Vertex textVertex = g.addV("text")
                .property("value", exercise.get("word"))
                .next();

        exerciseVertex.addEdge("basedOn", textVertex);
    }

    @Override
    public WriteSentenceUsingWordExercise get(GraphTraversalSource g, Vertex vertex) {
        WriteSentenceUsingWordExercise exercise = new WriteSentenceUsingWordExercise();
        exercise.setId(vertex.value("id"));

        // Retrieve the connected Text vertex
        Vertex textVertex = g.V(vertex).out("basedOn").next();
        exercise.setWord(textVertex.value("value").toString());

        return exercise;
    }
}