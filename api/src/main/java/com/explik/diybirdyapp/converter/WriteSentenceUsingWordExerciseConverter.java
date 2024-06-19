package com.explik.diybirdyapp.converter;

import com.explik.diybirdyapp.model.Exercise;
import com.explik.diybirdyapp.model.WriteSentenceUsingWordExercise;
import com.explik.diybirdyapp.model.WrittenAnswer;
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
    public void create(GraphTraversalSource g, Exercise data) {
        var exercise = (WriteSentenceUsingWordExercise)data;

        Vertex exerciseVertex = g.addV("exercise")
                .property("id", exercise.getId())
                .property("exerciseType", exercise.getExerciseType())
                .next();

        Vertex textVertex = g.addV("text")
                .property("value", exercise.getWord())
                .next();

        exerciseVertex.addEdge("basedOn", textVertex);
    }

    @Override
    public void update(GraphTraversalSource g, Exercise data) {
        var graph = g.getGraph();
        var exercise = (WriteSentenceUsingWordExercise)data;
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
    public WriteSentenceUsingWordExercise get(GraphTraversalSource g, Vertex vertex) {
        WriteSentenceUsingWordExercise exercise = new WriteSentenceUsingWordExercise();
        exercise.setId(vertex.value("id"));

        // Retrieve the connected Text vertex
        Vertex textVertex = g.V(vertex).out("basedOn").next();
        exercise.setWord(textVertex.value("value").toString());

        return exercise;
    }
}
