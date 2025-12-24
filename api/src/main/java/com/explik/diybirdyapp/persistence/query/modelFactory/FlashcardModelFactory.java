package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.model.content.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

@Component
public class FlashcardModelFactory implements ModelFactory<FlashcardVertex, FlashcardDto> {
    @Override
    public FlashcardDto create(FlashcardVertex vertex) {
        var leftContent = vertex.getLeftContent();
        var rightContent = vertex.getRightContent();

        FlashcardDto model = new FlashcardDto();
        model.setId(vertex.getId());
        model.setDeckId(vertex.getDeck().getId());
        model.setDeckOrder(vertex.getDeckOrder());

        switch (leftContent.getLabel()) {
            case AudioContentVertex.LABEL -> model.setFrontContent(createAudioContent((AudioContentVertex) leftContent));
            case ImageContentVertex.LABEL -> model.setFrontContent(createImageContent((ImageContentVertex)leftContent));
            case TextContentVertex.LABEL -> model.setFrontContent(createTextContent((TextContentVertex)leftContent));
            case VideoContentVertex.LABEL -> model.setFrontContent(createVideoContent((VideoContentVertex)leftContent));
            default -> throw new IllegalArgumentException("Invalid content type: " + leftContent.getLabel());
        }

        switch (rightContent.getLabel()) {
            case AudioContentVertex.LABEL -> model.setBackContent(createAudioContent((AudioContentVertex) rightContent));
            case ImageContentVertex.LABEL -> model.setBackContent(createImageContent((ImageContentVertex)rightContent));
            case TextContentVertex.LABEL -> model.setBackContent(createTextContent((TextContentVertex)rightContent));
            case VideoContentVertex.LABEL -> model.setBackContent(createVideoContent((VideoContentVertex)rightContent));
            default -> throw new IllegalArgumentException("Invalid content type: " + rightContent.getLabel());
        }

        return model;
    }

    public FlashcardContentAudioDto createAudioContent(AudioContentVertex vertex) {
        var model = new FlashcardContentAudioDto();
        model.setId(vertex.getId());
        model.setAudioUrl(vertex.getUrl());
        return model;
    }

    public FlashcardContentImageDto createImageContent(ImageContentVertex vertex) {
        var model = new FlashcardContentImageDto();
        model.setId(vertex.getId());
        model.setImageUrl(vertex.getUrl());
        return model;
    }

    public FlashcardContentTextDto createTextContent(TextContentVertex vertex) {
        var model = new FlashcardContentTextDto();
        model.setId(vertex.getId());
        model.setText(vertex.getValue());
        model.setLanguageId(vertex.getLanguage().getId());
        return model;
    }

    public FlashcardContentVideoDto createVideoContent(VideoContentVertex vertex) {
        var model = new FlashcardContentVideoDto();
        model.setId(vertex.getId());
        model.setVideoUrl(vertex.getUrl());
        return model;
    }
}
