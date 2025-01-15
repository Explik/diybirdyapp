package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.ExerciseContentFlashcardModel;
import com.explik.diybirdyapp.model.ExerciseContentImageModel;
import com.explik.diybirdyapp.model.ExerciseContentModel;
import com.explik.diybirdyapp.model.ExerciseContentTextModel;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.ImageContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseContentModelFactory implements ModelFactory<ExerciseContentModel, ContentVertex> {
    @Override
    public ExerciseContentModel create(ContentVertex vertex) {
        if (vertex.getLabel().equals(TextContentVertex.LABEL))
            return createTextModel(new TextContentVertex(vertex));
        if (vertex.getLabel().equals(ImageContentVertex.LABEL))
            return createImageModel(new ImageContentVertex(vertex));
        if (vertex.getLabel().equals(FlashcardVertex.LABEL))
            return createFlashcardModel((FlashcardVertex) vertex);

        throw new RuntimeException("Unknown content type " + vertex.getClass().getName());
    }

    public ExerciseContentTextModel createTextModel(TextContentVertex vertex) {
        ExerciseContentTextModel model = new ExerciseContentTextModel();
        model.setId(vertex.getId());
        model.setText(vertex.getValue());

        if (vertex.hasMainPronunciation())
            model.setPronunciationUrl(vertex.getMainPronunciation().getAudioContent().getUrl());

        return model;
    }

    public ExerciseContentImageModel createImageModel(ImageContentVertex vertex) {
        ExerciseContentImageModel model = new ExerciseContentImageModel();
        model.setId(vertex.getId());
        model.setImageUrl(vertex.getUrl());

        return model;
    }

    public ExerciseContentFlashcardModel createFlashcardModel(FlashcardVertex vertex) {
        var leftContent = create(vertex.getLeftContent());
        var rightContent = create(vertex.getRightContent());

        ExerciseContentFlashcardModel model = new ExerciseContentFlashcardModel();
        model.setFront(leftContent);
        model.setBack(rightContent);
        return model;
    }

    public ExerciseContentFlashcardModel createFlashcardModelWithOnlyLeftSide(FlashcardVertex vertex) {
        var leftContent = create(vertex.getLeftContent());

        ExerciseContentFlashcardModel model = new ExerciseContentFlashcardModel();
        model.setFront(leftContent);
        return model;
    }

    public ExerciseContentFlashcardModel createFlashcardModelWithOnlyRightSide(FlashcardVertex vertex) {
        var rightContent = create(vertex.getRightContent());

        ExerciseContentFlashcardModel model = new ExerciseContentFlashcardModel();
        model.setBack(rightContent);
        return model;
    }
}
