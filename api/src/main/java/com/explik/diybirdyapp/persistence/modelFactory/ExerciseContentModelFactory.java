package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.exercise.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

@Component
public class ExerciseContentModelFactory implements ModelFactory<ExerciseVertex, ExerciseContentModel> {
    @Override
    public ExerciseContentModel create(ExerciseVertex vertex) {
        var contentVertex = vertex.getContent();

        if (contentVertex instanceof FlashcardVertex flashcardVertex) {
            var flashcardSide = vertex.getFlashcardSide();

            if (flashcardSide == null)
                return createFlashcardModel(flashcardVertex);
            else if (vertex.getFlashcardSide().equals("front"))
                return createFlashcardModelWithOnlyLeftSide(flashcardVertex);
            else if (vertex.getFlashcardSide().equals("back"))
                return createFlashcardModelWithOnlyRightSide(flashcardVertex);
            else
                throw new RuntimeException("Unknown flashcard side " + vertex.getFlashcardSide());
        }
        return createNonFlashcardModel(contentVertex);
    }

    public ExerciseContentFlashcardModel createFlashcardModel(FlashcardVertex vertex) {
        var leftContent = createNonFlashcardModel(vertex.getLeftContent());
        var rightContent = createNonFlashcardModel(vertex.getRightContent());

        ExerciseContentFlashcardModel model = new ExerciseContentFlashcardModel();
        model.setId(vertex.getId());
        model.setFront(leftContent);
        model.setBack(rightContent);
        return model;
    }

    public ExerciseContentFlashcardSideModel createFlashcardModelWithOnlyLeftSide(FlashcardVertex vertex) {
        var leftContent = createNonFlashcardModel(vertex.getLeftContent());

        ExerciseContentFlashcardSideModel model = new ExerciseContentFlashcardSideModel();
        model.setId(vertex.getId());
        model.setContent(leftContent);
        return model;
    }

    public ExerciseContentFlashcardSideModel createFlashcardModelWithOnlyRightSide(FlashcardVertex vertex) {
        var rightContent = createNonFlashcardModel(vertex.getRightContent());

        ExerciseContentFlashcardSideModel model = new ExerciseContentFlashcardSideModel();
        model.setId(vertex.getId());
        model.setContent(rightContent);
        return model;
    }

    public ExerciseContentModel createNonFlashcardModel(ContentVertex contentVertex) {
        if (contentVertex instanceof AudioContentVertex audioContentVertex)
            return createAudioModel(audioContentVertex);
        if (contentVertex instanceof TextContentVertex textContentVertex)
            return createTextModel(textContentVertex);
        if (contentVertex instanceof ImageContentVertex imageContentVertex)
            return createImageModel(imageContentVertex);
        if (contentVertex instanceof VideoContentVertex videoContentVertex)
            return createVideoModel(videoContentVertex);

        throw new RuntimeException("Unknown content type " + contentVertex.getLabel());
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

    public ExerciseContentVideoModel createVideoModel(VideoContentVertex vertex) {
        ExerciseContentVideoModel model = new ExerciseContentVideoModel();
        model.setId(vertex.getId());
        model.setVideoUrl(vertex.getUrl());

        return model;
    }
}
