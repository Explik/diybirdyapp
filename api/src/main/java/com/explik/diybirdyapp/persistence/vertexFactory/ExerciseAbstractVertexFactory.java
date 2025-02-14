package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.ContentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchema;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.ExerciseParameters;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExerciseAbstractVertexFactory {
    public VertexFactory<ExerciseVertex, ExerciseParameters> create(ExerciseSchema schema) {
        return new ConcreteFactory(schema);
    }

    record ConcreteFactory(ExerciseSchema schema) implements VertexFactory<ExerciseVertex, ExerciseParameters> {
        @Override
        public ExerciseVertex create(GraphTraversalSource traversalSource, ExerciseParameters options) {
            var vertex = ExerciseVertex.create(traversalSource);

            var id = options.getId() != null ? options.getId() : UUID.randomUUID().toString();
            vertex.setId(id);

            vertex.setType(this.schema.getExerciseType());

            if (options.getSession() != null)
                vertex.setSession(options.getSession());

            if (this.schema.getRequireTargetLanguage())
                vertex.setTargetLanguage(options.getTargetLanguage());

            if (this.schema.getContentType() != null)
                attachContent(vertex, options);
            if (this.schema.getInputType() != null)
                attachInput(vertex, options);

            return vertex;
        }

        private void attachContent(ExerciseVertex vertex, ExerciseParameters options) {
            String contentType = this.schema.getContentType();

            if (contentType.equals(ContentTypes.FLASHCARD)) {
                vertex.setContent(options.getContent().getVertex());
            }
            else if (contentType.equals(ContentTypes.FLASHCARD_SIDE)) {
                vertex.setContent(options.getContent().getVertex());
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

        private void attachInput(ExerciseVertex vertex, ExerciseParameters options) {
            String inputType = this.schema.getInputType();

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
            } else if (inputType.equals(ExerciseInputTypes.WRITE_TEXT)) {
                var inputText = options.getWriteTextInput();

                if (inputText != null)
                    vertex.addCorrectOption(inputText.getCorrectOption());
            }
            else throw new IllegalArgumentException("Unsupported input type: " + inputType);
        }
    }
}
