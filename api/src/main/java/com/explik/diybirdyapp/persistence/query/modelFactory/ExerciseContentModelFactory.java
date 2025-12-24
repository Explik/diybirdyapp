package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.model.exercise.*;
import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import com.explik.diybirdyapp.model.internal.VoiceModel;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.query.GetTextContentByIdQuery;
import com.explik.diybirdyapp.persistence.query.GetVoiceByLanguageIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.service.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseContentModelFactory implements ContextualModelFactory<ExerciseVertex, ExerciseContentDto, ExerciseRetrievalContext> {
    @Autowired
    private TextToSpeechService textToSpeechService;

    @Autowired
    private QueryHandler<GetVoiceByLanguageIdQuery, VoiceModel> generateVoiceConfigQueryHandler;

    @Override
    public ExerciseContentDto create(ExerciseVertex exerciseVertex, ExerciseRetrievalContext context) {
        var vertex = exerciseVertex.getContent();

        if (vertex instanceof FlashcardVertex flashcardVertex) {
            var side = exerciseVertex.getFlashcardSide();
            return (side != null) ? createFlashcardSideModel(flashcardVertex, side, context) : createFlashcardModel(flashcardVertex, context);
        }
        return createBasicContentModel(vertex, context);
    }

    public ExerciseContentFlashcardDto createFlashcardModel(FlashcardVertex vertex, ExerciseRetrievalContext context) {
        var leftContent = createBasicContentModel(vertex.getLeftContent(), context);
        var rightContent = createBasicContentModel(vertex.getRightContent(), context);

        ExerciseContentFlashcardDto model = new ExerciseContentFlashcardDto();
        model.setId(vertex.getId());
        model.setFront(leftContent);
        model.setBack(rightContent);

        if (context.getInitialFlashcardLanguageId() != null) {
            if (vertex.getLeftContent() instanceof TextContentVertex leftTextContent &&
                leftTextContent.getLanguage().getId().equals(context.getInitialFlashcardLanguageId())) {
                model.setInitialSide("front");
            }
            else if (vertex.getRightContent() instanceof TextContentVertex rightTextContent &&
                     rightTextContent.getLanguage().getId().equals(context.getInitialFlashcardLanguageId())) {
                model.setInitialSide("back");
            }
        }

        return model;
    }

    public ExerciseContentFlashcardSideDto createFlashcardSideModel(FlashcardVertex vertex, String side, ExerciseRetrievalContext context) {
        var content = createBasicContentModel(vertex.getSide(side), context);

        ExerciseContentFlashcardSideDto model = new ExerciseContentFlashcardSideDto();
        model.setId(vertex.getId());
        model.setContent(content);
        return model;
    }

    public ExerciseContentDto createBasicContentModel(ContentVertex contentVertex, ExerciseRetrievalContext context) {
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

    public ExerciseContentAudioDto createAudioModel(AudioContentVertex vertex, ExerciseRetrievalContext context) {
        ExerciseContentAudioDto model = new ExerciseContentAudioDto();
        model.setId(vertex.getId());
        model.setAudioUrl(vertex.getUrl());

        return model;
    }

    public ExerciseContentTextDto createTextModel(TextContentVertex vertex, ExerciseRetrievalContext context) {
        ExerciseContentTextDto model = new ExerciseContentTextDto();
        model.setId(vertex.getId());
        model.setText(vertex.getValue());

        var pronunciationVertex = PronunciationVertex.findByTextContentId(vertex.getUnderlyingSource(), vertex.getId());
        if (pronunciationVertex != null) {
            model.setPronunciationUrl(pronunciationVertex.getAudioContent().getUrl());
        }
        else if (context.getTextToSpeechEnabled()) {
            var query = new GetVoiceByLanguageIdQuery();
            query.setLanguageId(vertex.getLanguage().getId());
            var voiceConfig = generateVoiceConfigQueryHandler.handle(query);
            if (voiceConfig == null)
                return model;

            var textToSpeechModel = TextToSpeechModel.create(
                    vertex.getValue(),
                    voiceConfig);

            var filePath = vertex.getId() + ".wav";
            try {
                textToSpeechService.generateAudioFile(textToSpeechModel, filePath);
                model.setPronunciationUrl(filePath);
            }
            catch (Exception e) { }
        }

        var transcriptionVertex = TranscriptionVertex.findBySourceContentId(
                vertex.getUnderlyingSource(),
                vertex.getId());
        if (transcriptionVertex != null) {
            model.setTranscription(transcriptionVertex.getTextContent().getValue());
        }

        return model;
    }

    public ExerciseContentImageDto createImageModel(ImageContentVertex vertex, ExerciseRetrievalContext context) {
        ExerciseContentImageDto model = new ExerciseContentImageDto();
        model.setId(vertex.getId());
        model.setImageUrl(vertex.getUrl());

        return model;
    }

    public ExerciseContentVideoDto createVideoModel(VideoContentVertex vertex, ExerciseRetrievalContext context) {
        ExerciseContentVideoDto model = new ExerciseContentVideoDto();
        model.setId(vertex.getId());
        model.setVideoUrl(vertex.getUrl());

        return model;
    }

}
