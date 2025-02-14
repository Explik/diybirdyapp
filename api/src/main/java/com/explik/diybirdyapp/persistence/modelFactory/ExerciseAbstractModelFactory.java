package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ContentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseContentFlashcardSideModel;
import com.explik.diybirdyapp.model.exercise.ExerciseContentModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchema;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseAbstractModelFactory {
    @Autowired
    private ExerciseContentModelFactory contentModelFactory;

    @Autowired
    private ExerciseInputModelFactoryArrangeTextOptions arrangeTextOptionsModelFactory;

    @Autowired
    private ExerciseInputModelFactoryMultipleChoice selectOptionsModelFactory;

    public ModelFactory<ExerciseVertex, ExerciseModel> create(ExerciseSchema schema) {
        return vertex -> createExercise(vertex, schema);
    }

    private ExerciseModel createExercise(ExerciseVertex vertex, ExerciseSchema schema) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");
        if (schema == null)
            throw new RuntimeException("Schema is null");
        if (!vertex.getType().equals(schema.getExerciseType()))
            throw new RuntimeException("Vertex type does not match schema type (Expected: " + schema.getExerciseType() + ", Actual: " + vertex.getType() + ")");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());

        if (schema.getContentType() != null) {
            var content = createContent(vertex, schema);
            instance.setContent(content);
        }
        if (schema.getInputType() != null) {
            var input = createInput(vertex, schema);
            instance.setInput(input);
        }
        return instance;
    }

    private ExerciseContentModel createContent(ExerciseVertex vertex, ExerciseSchema schema) {
        var contentType = schema.getContentType();

        if (contentType.equals(ContentTypes.FLASHCARD_SIDE)) {
            var content = contentModelFactory.create(vertex.getContent());
            return ExerciseContentFlashcardSideModel.create(content);
        } else {
            return contentModelFactory.create(vertex.getContent());
        }
    }

    private ExerciseInputModel createInput(ExerciseVertex vertex, ExerciseSchema schema) {
        var inputType = schema.getInputType();

        if (inputType.equals(ExerciseInputTypes.ARRANGE_TEXT_OPTIONS)) {
            return arrangeTextOptionsModelFactory.create(vertex);
        }
        if (inputType.equals(ExerciseInputTypes.SELECT_OPTIONS)) {
            return selectOptionsModelFactory.create(vertex);
        }
        else throw new RuntimeException("Unsupported input type: " + inputType);
    }
}
