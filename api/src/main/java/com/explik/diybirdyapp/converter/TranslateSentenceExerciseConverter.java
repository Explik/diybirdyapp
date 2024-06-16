package com.explik.diybirdyapp.converter;

import com.explik.diybirdyapp.model.TranslateSentenceExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

@Component
public class TranslateSentenceExerciseConverter implements ExerciseConverter<TranslateSentenceExercise> {
    @Override
    public String getExerciseType() {
        return "translate-sentence-exercise";
    }

    @Override
    public TranslateSentenceExercise convert(GraphTraversalSource g, Vertex vertex) {
        TranslateSentenceExercise exercise = new TranslateSentenceExercise();
        exercise.setId(vertex.value("id"));
        exercise.setTargetLanguage(vertex.value("targetLanguage"));

        // Retrieve the connected Text vertex
        Vertex textVertex = g.V(vertex).out("basedOn").next();
        exercise.setOriginalSentence(textVertex.value("value").toString());

        return exercise;
    }
}
