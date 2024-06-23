package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.Exercise;
import com.explik.diybirdyapp.graph.model.WriteTranslatedSentenceExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

@Component
public class GetTranslatedSentenceExerciseQueryMapper implements GenericQueryMapper<Exercise<?>>  {
    @Override
    public String getIdentifier() {
        return "write-translated-sentence-exercise";
    }

    @Override
    public WriteTranslatedSentenceExercise apply(GraphTraversalSource g, Vertex vertex) {
        WriteTranslatedSentenceExercise exercise = new WriteTranslatedSentenceExercise();
        exercise.setId(vertex.value("id"));
        exercise.setTargetLanguage(vertex.value("targetLanguage"));

        // Retrieve the connected Text vertex
        Vertex textVertex = g.V(vertex).out("basedOn").next();
        exercise.setOriginalSentence(textVertex.value("value").toString());

        return exercise;
    }
}
