package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import com.explik.diybirdyapp.graph.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseFactoryImpl implements ExerciseFactory {
    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        var instance = vertex.toLimitedExerciseModel();

        var vertexType = vertex.getType();
        switch (vertexType) {
            case "write-sentence-using-word-exercise" -> {
                instance.setProperty("word", vertex.getTextContent().getValue());
                return instance;
            }
            case "write-translated-sentence-exercise" -> {

                instance.setProperty("targetLanguage", vertex.getTargetLanguage());
                instance.setContent(vertex.getTextContent().toExerciseContentTextModel());
                return instance;
            }
            case "multiple-choice-text-exercise" -> {
                var input = new ExerciseInputMultipleChoiceTextModel();
                input.setType("multiple-choice-text-input");
                vertex.getOptions()
                        .forEach(v -> {
                            input.addOption(
                                    new ExerciseInputMultipleChoiceTextModel.Option(v.getId(), v.getValue()));
                        });

                instance.setInput(input);
                return instance;
            }
            case "review-flashcard-content-exercise" -> {
                instance.setContent(vertex.getFlashcardContent().toExerciseContentFlashcardModel());
                return instance;
            }
            default -> throw new RuntimeException("Unsupported vertex type: " + vertexType);
        }
    }

    @Override
    public ExerciseModel createLimited(ExerciseVertex vertex) {
        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());
        return instance;
    }
}
