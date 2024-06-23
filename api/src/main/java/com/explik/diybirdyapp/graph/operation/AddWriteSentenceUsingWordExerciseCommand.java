package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.WriteSentenceUsingWordExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

@Component
public class AddWriteSentenceUsingWordExerciseCommand implements GenericCommand<AddGenericExerciseCommand.Options> {
    @Override
    public String getIdentifier() {
        return "write-sentence-using-word-exercise";
    }

    public void execute(GraphTraversalSource g, AddGenericExerciseCommand.Options options) {
        var exercise = (WriteSentenceUsingWordExercise)options.getExercise();

        Vertex exerciseVertex = g.addV("exercise")
                .property("id", exercise.getId())
                .property("exerciseType", exercise.getExerciseType())
                .next();

        Vertex textVertex = g.addV("text")
                .property("value", exercise.getWord())
                .next();

        exerciseVertex.addEdge("basedOn", textVertex);
    }
}
