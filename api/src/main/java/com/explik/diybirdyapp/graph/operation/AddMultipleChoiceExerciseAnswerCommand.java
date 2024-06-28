package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.WrittenExerciseAnswer;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

@Component
public class AddMultipleChoiceExerciseAnswerCommand implements GenericCommand<AddMultipleTextChoiceExerciseCommand.Options> {
    @Override
    public String getIdentifier() {
        return "written-exercise-answer";
    }

    @Override
    public void execute(GraphTraversalSource g, AddGenericExerciseAnswerCommand.Options options) {
        var answer = (WrittenExerciseAnswer)options.getAnswer();
        var graph = g.getGraph();

        Vertex exerciseVertex = g.V().has("exercise", "id", options.getExerciseId()).next();

        Vertex answerVertex = graph.addVertex("exerciseAnswer");
        answerVertex.property("id", answer.getId());
        answerVertex.property("answerType", answer.getAnswerType());
        exerciseVertex.addEdge("hasAnswer", answerVertex);

        Vertex answerValueVertex = graph.addVertex("text");
        answerValueVertex.property("value", answer.getText());
        answerValueVertex.addEdge("basedOn", answerVertex);
    }
}
