package com.explik.diybirdyapp.graph.vertex.modelFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.WRITE_SENTENCE_USING_WORD + ComponentTypes.MODEL_FACTORY)
public class ExerciseWriteSentenceUsingWordModelFactory implements ExerciseModelFactory {
    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");
        if (!vertex.getType().equals(ExerciseTypes.WRITE_SENTENCE_USING_WORD))
            throw new RuntimeException("Vertex type is not WRITE_SENTENCE_USING_WORD");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());
        instance.setProperty("word", vertex.getTextContent().getValue());
        return instance;
    }
}
