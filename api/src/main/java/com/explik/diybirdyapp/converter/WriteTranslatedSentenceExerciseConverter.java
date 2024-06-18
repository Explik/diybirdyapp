package com.explik.diybirdyapp.converter;

import com.explik.diybirdyapp.model.Exercise;
import com.explik.diybirdyapp.model.WriteTranslatedSentenceExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WriteTranslatedSentenceExerciseConverter implements ExerciseConverter<WriteTranslatedSentenceExercise> {
    @Override
    public String getExerciseType() {
        return "write-translated-sentence-exercise";
    }

    @Override
    public void create(GraphTraversalSource g, Map<String, Object> exercise) {
        Vertex exerciseVertex = g.addV("exercise")
                .property("id", exercise.get("id"))
                .property("exerciseType", exercise.get("exerciseType"))
                .property("targetLanguage", exercise.get("targetLanguage"))
                .next();

        Vertex textVertex = g.addV("text")
                .property("value", exercise.get("originalSentence"))
                .next();

        exerciseVertex.addEdge("basedOn", textVertex);
    }

    @Override
    public WriteTranslatedSentenceExercise get(GraphTraversalSource g, Vertex vertex) {
        WriteTranslatedSentenceExercise exercise = new WriteTranslatedSentenceExercise();
        exercise.setId(vertex.value("id"));
        exercise.setTargetLanguage(vertex.value("targetLanguage"));

        // Retrieve the connected Text vertex
        Vertex textVertex = g.V(vertex).out("basedOn").next();
        exercise.setOriginalSentence(textVertex.value("value").toString());

        return exercise;
    }
}
