package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ContentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchema;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseParameters;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseAbstractVertexFactory {
    @Autowired
    private PairVertexFactory pairVertexFactory;

    public VertexFactory<ExerciseVertex, ExerciseParameters> create(ExerciseSchema schema) {
        return (traversalSource, options) -> create(traversalSource, schema, options);
    }

    private ExerciseVertex create(GraphTraversalSource traversalSource, ExerciseSchema schema, ExerciseParameters options) {
        var vertex = ExerciseVertex.create(traversalSource);

        var id = options.getId() != null ? options.getId() : UUID.randomUUID().toString();
        vertex.setId(id);

        vertex.setType(schema.getExerciseType());

        if (options.getSession() != null)
            vertex.setSession(options.getSession());

        if (schema.getRequireTargetLanguage())
            vertex.setTargetLanguage(options.getTargetLanguage());

        if (schema.getContentType() != null)
            attachContent(vertex, schema, options);
        if (schema.getInputType() != null)
            attachInput(vertex, schema, options);

        return vertex;
    }

    private void attachContent(ExerciseVertex vertex, ExerciseSchema schema, ExerciseParameters options) {
        String contentType = schema.getContentType();

        if (contentType.equals(ContentTypes.FLASHCARD)) {
            vertex.setContent(options.getContent().getVertex());
        }
        else if (contentType.equals(ContentTypes.FLASHCARD_SIDE)) {
            vertex.setFlashcardContent(
                    (FlashcardVertex)options.getContent().getVertex(),
                    options.getContent().getFlashcardSide());
        }
        else if (contentType.equals(ContentTypes.AUDIO)) {
            vertex.setContent(options.getContent().getVertex());
        }
        else if (contentType.equals(ContentTypes.IMAGE)) {
            vertex.setContent(options.getContent().getVertex());
        }
        else if (contentType.equals(ContentTypes.TEXT)) {
            vertex.setContent(options.getContent().getVertex());
        }
        else if (contentType.equals(ContentTypes.VIDEO)) {
            vertex.setContent(options.getContent().getVertex());
        }
        else throw new IllegalArgumentException("Unsupported content type: " + contentType);
    }

    private void attachInput(ExerciseVertex vertex, ExerciseSchema schema, ExerciseParameters options) {
        String inputType = schema.getInputType();

        if (inputType.equals(ExerciseInputTypes.ARRANGE_TEXT_OPTIONS)) {
            var inputOptions = options.getArrangeTextOptionsInput();

            if (inputOptions == null)
                throw new IllegalArgumentException("ArrangeTextOptionsInput is required for input type: " + inputType);

            for (var option : inputOptions.getOptions()) {
                vertex.addOption(option);
                option.makeStatic();
            }
        }
        else if (inputType.equals(ExerciseInputTypes.SELECT_OPTIONS)) {
            var inputOptions = options.getSelectOptionsInput();

            if (inputOptions == null)
                throw new IllegalArgumentException("SelectOptionsInput is required for input type: " + inputType);

            for (var option : inputOptions.getCorrectOptions()) {
                vertex.addCorrectOption(option);
                option.makeStatic();
            }

            for (var option : inputOptions.getIncorrectOptions()) {
                vertex.addOption(option);
                option.makeStatic();
            }
        }
        else if (inputType.equals(ExerciseInputTypes.PAIR_OPTIONS)){
            var inputOptions = options.getPairOptionsInput();

            if (inputOptions == null)
                throw new IllegalArgumentException("PairOptionsInput is required for input type: " + inputType);

            for (var pairs : inputOptions.getPairs()) {
                var leftSide = pairs.get(0);
                var rightSide = pairs.get(1);
                var pairVertex = pairVertexFactory.create(
                        vertex.getUnderlyingSource(),
                        new PairVertexFactory.Options(UUID.randomUUID().toString(),  leftSide, rightSide));
                vertex.addOptionPair(pairVertex);
            }
        }
        else if (inputType.equals(ExerciseInputTypes.WRITE_TEXT)) {
            var inputText = options.getWriteTextInput();

            if (inputText != null)
                vertex.addCorrectOption(inputText.getCorrectOption());
        }
        else throw new IllegalArgumentException("Unsupported input type: " + inputType);
    }
}
