package com.explik.diybirdyapp.graph.operation;

import com.explik.diybirdyapp.graph.model.WriteTranslatedSentenceExercise;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Component;

@Component
public class AddWriteTranslatedSentenceExerciseCommand implements GenericCommand<AddGenericExerciseCommand.Options>{
    @Override
    public String getIdentifier() {
        return "write-translated-sentence-exercise";
    }

    public void execute(GraphTraversalSource g, AddGenericExerciseCommand.Options options) {
        var exercise = (WriteTranslatedSentenceExercise)options.getExercise();

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
}
