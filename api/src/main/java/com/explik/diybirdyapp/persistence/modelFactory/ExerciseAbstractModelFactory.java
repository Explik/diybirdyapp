package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseContentModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
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

    @Autowired
    private ExerciseInputModelFactoryPairOptions pairOptionsModelFactory;

    public ContextualModelFactory<ExerciseVertex, ExerciseModel, ExerciseRetrievalContext> create(ExerciseSchema schema) {
        return (vertex, context) -> createExercise(vertex, context, schema);
    }

    private ExerciseModel createExercise(ExerciseVertex vertex, ExerciseRetrievalContext context, ExerciseSchema schema) {
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
            var content = createContent(vertex, context, schema);
            instance.setContent(content);
        }
        if (schema.getInputType() != null) {
            var input = createInput(vertex, context, schema);
            instance.setInput(input);
        }
        return instance;
    }

    private ExerciseContentModel createContent(ExerciseVertex vertex, ExerciseRetrievalContext context, ExerciseSchema schema) {
        return contentModelFactory.create(vertex, context);
    }

    private ExerciseInputModel createInput(ExerciseVertex vertex, ExerciseRetrievalContext context, ExerciseSchema schema) {
        var inputType = schema.getInputType();

        if (inputType.equals(ExerciseInputTypes.ARRANGE_TEXT_OPTIONS)) {
            return arrangeTextOptionsModelFactory.create(vertex, context);
        }
        if (inputType.equals(ExerciseInputTypes.SELECT_OPTIONS)) {
            return selectOptionsModelFactory.create(vertex, context);
        }
        if (inputType.equals(ExerciseInputTypes.PAIR_OPTIONS)) {
            return pairOptionsModelFactory.create(vertex, context);
        }
        return null;
    }
}
