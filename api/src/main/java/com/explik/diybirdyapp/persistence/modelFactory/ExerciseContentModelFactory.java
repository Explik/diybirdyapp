package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

@Component
public class ExerciseContentModelFactory implements ModelFactory<ExerciseContentModel, ContentVertex> {
    @Override
    public ExerciseContentModel create(ContentVertex vertex) {
        if (vertex.getLabel().equals(AudioContentVertex.LABEL))
            return createAudioModel(new AudioContentVertex(vertex));
        if (vertex.getLabel().equals(TextContentVertex.LABEL))
            return createTextModel(new TextContentVertex(vertex));
        if (vertex.getLabel().equals(ImageContentVertex.LABEL))
            return createImageModel(new ImageContentVertex(vertex));
        if (vertex.getLabel().equals(FlashcardVertex.LABEL))
            return createFlashcardModel((FlashcardVertex) vertex);

        throw new RuntimeException("Unknown content type " + vertex.getClass().getName());
    }

    public ExerciseContentAudioModel createAudioModel(AudioContentVertex vertex) {
        ExerciseContentAudioModel model = new ExerciseContentAudioModel();
        model.setId(vertex.getId());
        model.setAudioUrl(vertex.getUrl());

        return model;
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
