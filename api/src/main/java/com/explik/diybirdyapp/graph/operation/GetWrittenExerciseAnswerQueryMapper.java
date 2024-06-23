package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.ExerciseAnswer;
import com.explik.diybirdyapp.graph.model.WriteSentenceUsingWordExercise;
import com.explik.diybirdyapp.graph.model.WrittenExerciseAnswer;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

@Component
public class GetWrittenExerciseAnswerQueryMapper implements GenericQueryMapper<ExerciseAnswer> {
    @Override
    public String getIdentifier() {
        return "written-exercise-answer";
    }

    @Override
    public WrittenExerciseAnswer apply(GraphTraversalSource traversal, Vertex vertex) {
        WrittenExerciseAnswer answer = new WrittenExerciseAnswer();
        answer.setId(vertex.value("id"));

        // Retrieve the connected Text vertex
        Vertex textVertex = traversal.V(vertex).in("basedOn").next();
        answer.setText(textVertex.value("value").toString());

        return answer;
    }
}
