package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import com.explik.diybirdyapp.graph.vertex.TextVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseFactoryImpl implements ExerciseFactory {
    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        var vertexType = vertex.getType();

        if (vertexType.equals("write-sentence-using-word-exercise")) {
            var instance = vertex.toLimitedExerciseModel();
            instance.setProperty("word", vertex.getTextContent().getValue());
            return instance;
        }
        else if (vertexType.equals("write-translated-sentence-exercise")) {
            var instance = vertex.toLimitedExerciseModel();
            instance.setProperty("targetLanguage", vertex.getProperty("targetLanguage"));
            instance.setContent(vertex.getTextContent().toExerciseContentTextModel());
            return instance;
        }
        else if (vertexType.equals("multiple-choice-text-exercise")) {
            var input = new ExerciseInputMultipleChoiceTextModel();
            input.setType("multiple-choice-text-input");
            vertex.traverse(g -> g.out("hasOption"))
                    .toListExplicit(TextVertex.class)
                    .forEach(v -> {
                        input.addOption(
                            new ExerciseInputMultipleChoiceTextModel.Option(v.getId(), v.getValue()));
                    });


            var instance = vertex.toLimitedExerciseModel();
            instance.setInput(input);
            return instance;
        }
        else if (vertexType.equals("review-flashcard-content-exercise")) {
            var instance = vertex.toLimitedExerciseModel();
            instance.setContent(vertex.getFlashcardContent().toExerciseContentFlashcardModel());
            return instance;
        }
        else throw new RuntimeException("Unsupported vertex type: " + vertexType);
    }

    @Override
    public ExerciseModel createLimited(ExerciseVertex vertex) {

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());
        return instance;
    }
}
