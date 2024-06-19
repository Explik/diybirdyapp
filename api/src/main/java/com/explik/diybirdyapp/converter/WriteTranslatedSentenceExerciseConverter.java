package com.explik.diybirdyapp.converter;

import com.explik.diybirdyapp.model.Exercise;
import com.explik.diybirdyapp.model.WriteSentenceUsingWordExercise;
import com.explik.diybirdyapp.model.WriteTranslatedSentenceExercise;
import com.explik.diybirdyapp.model.WrittenAnswer;
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
    public void create(GraphTraversalSource g, Exercise data) {
        var exercise = (WriteTranslatedSentenceExercise)data;

        Vertex exerciseVertex = g.addV("exercise")
                .property("id", exercise.getId())
                .property("exerciseType", exercise.getExerciseType())
                .property("targetLanguage", exercise.getTargetLanguage())
                .next();

        Vertex textVertex = g.addV("text")
                .property("value", exercise.getOriginalSentence())
                .next();

        exerciseVertex.addEdge("basedOn", textVertex);
    }

    @Override
    public void update(GraphTraversalSource g, Exercise data) {
        var graph = g.getGraph();
        var exercise = (WriteTranslatedSentenceExercise)data;
        var answer = (WrittenAnswer)exercise.getExerciseAnswer();

        if (answer != null) {
            Vertex exerciseVertex = g.V().has("exercise", "id", exercise.getId()).next();

            Vertex answerVertex = graph.addVertex("exerciseAnswer");
            answerVertex.property("id", answer.getId());
            answerVertex.addEdge("basedOn", exerciseVertex);

            Vertex answerValueVertex = graph.addVertex("text");
            answerValueVertex.property("value", answer.getText());
            answerValueVertex.addEdge("basedOn", answerVertex);
        }
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
