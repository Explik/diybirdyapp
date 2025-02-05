package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.WRITE_TRANSLATED_SENTENCE + ComponentTypes.MODEL_FACTORY)
public class ExerciseModelFactoryWriteTranslatedSentence implements ExerciseModelFactory {
    @Autowired
    ExerciseContentModelFactory exerciseContentModelFactory;

    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");
        if (!vertex.getType().equals(ExerciseTypes.WRITE_TRANSLATED_SENTENCE))
            throw new RuntimeException("Vertex type is not WRITE_TRANSLATED_SENTENCE");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());
        instance.setProperty("targetLanguage", vertex.getTargetLanguage());

        var content = exerciseContentModelFactory.create(vertex.getContent());
        instance.setContent(content);

        return instance;
    }
}
