package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.exercise.*;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

@Component
public class ExerciseContentModelFactory implements ContextualModelFactory<ExerciseVertex, ExerciseContentModel, ExerciseRetrievalContext> {
    @Override
    public ExerciseContentModel create(ExerciseVertex exerciseVertex, ExerciseRetrievalContext context) {
        var vertex = exerciseVertex.getContent();

        if (vertex instanceof FlashcardVertex flashcardVertex) {
            var side = exerciseVertex.getFlashcardSide();
            return (side != null) ? createFlashcardSideModel(flashcardVertex, side, context) : createFlashcardModel(flashcardVertex, context);
        }
        return createBasicContentModel(vertex, context);
    }

    public ExerciseContentFlashcardModel createFlashcardModel(FlashcardVertex vertex, ExerciseRetrievalContext context) {
        var leftContent = createBasicContentModel(vertex.getLeftContent(), context);
        var rightContent = createBasicContentModel(vertex.getRightContent(), context);

        ExerciseContentFlashcardModel model = new ExerciseContentFlashcardModel();
        model.setId(vertex.getId());
        model.setFront(leftContent);
        model.setBack(rightContent);
        return model;
    }

    public ExerciseContentFlashcardSideModel createFlashcardSideModel(FlashcardVertex vertex, String side, ExerciseRetrievalContext context) {
        var content = createBasicContentModel(vertex.getSide(side), context);

        ExerciseContentFlashcardSideModel model = new ExerciseContentFlashcardSideModel();
        model.setId(vertex.getId());
        model.setContent(content);
        return model;
    }

    public ExerciseContentModel createBasicContentModel(ContentVertex contentVertex, ExerciseRetrievalContext context) {
        if (contentVertex instanceof AudioContentVertex audioContentVertex)
            return createAudioModel(audioContentVertex, context);
        if (contentVertex instanceof TextContentVertex textContentVertex)
            return createTextModel(textContentVertex, context);
        if (contentVertex instanceof ImageContentVertex imageContentVertex)
            return createImageModel(imageContentVertex, context);
        if (contentVertex instanceof VideoContentVertex videoContentVertex)
            return createVideoModel(videoContentVertex, context);

        throw new RuntimeException("Unknown content type " + contentVertex.getLabel());
    }

    public ExerciseContentAudioModel createAudioModel(AudioContentVertex vertex, ExerciseRetrievalContext context) {
        ExerciseContentAudioModel model = new ExerciseContentAudioModel();
        model.setId(vertex.getId());
        model.setAudioUrl(vertex.getUrl());

        return model;
    }

    public ExerciseContentTextModel createTextModel(TextContentVertex vertex, ExerciseRetrievalContext context) {
        ExerciseContentTextModel model = new ExerciseContentTextModel();
        model.setId(vertex.getId());
        model.setText(vertex.getValue());

        if (vertex.hasMainPronunciation())
            model.setPronunciationUrl(vertex.getMainPronunciation().getAudioContent().getUrl());

        return model;
    }

    public ExerciseContentImageModel createImageModel(ImageContentVertex vertex, ExerciseRetrievalContext context) {
        ExerciseContentImageModel model = new ExerciseContentImageModel();
        model.setId(vertex.getId());
        model.setImageUrl(vertex.getUrl());

        return model;
    }

    public ExerciseContentVideoModel createVideoModel(VideoContentVertex vertex, ExerciseRetrievalContext context) {
        ExerciseContentVideoModel model = new ExerciseContentVideoModel();
        model.setId(vertex.getId());
        model.setVideoUrl(vertex.getUrl());

        return model;
    }
}
