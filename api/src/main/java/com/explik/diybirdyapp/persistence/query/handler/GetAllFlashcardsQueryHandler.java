package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.FlashcardContentAudioDto;
import com.explik.diybirdyapp.model.content.FlashcardContentDto;
import com.explik.diybirdyapp.model.content.FlashcardContentImageDto;
import com.explik.diybirdyapp.model.content.FlashcardContentTextDto;
import com.explik.diybirdyapp.model.content.FlashcardContentVideoDto;
import com.explik.diybirdyapp.model.content.FlashcardDto;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardsQuery;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.ImageContentVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.VideoContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetAllFlashcardsQueryHandler implements QueryHandler<GetAllFlashcardsQuery, List<FlashcardDto>> {
    private static final String FIELD_ID = "id";
    private static final String FIELD_DECK_ID = "deckId";
    private static final String FIELD_DECK_ORDER = "deckOrder";
    private static final String FIELD_FRONT = "front";
    private static final String FIELD_BACK = "back";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_URL = "url";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_LANGUAGE_ID = "languageId";

    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public List<FlashcardDto> handle(GetAllFlashcardsQuery query) {
        var rawFlashcards = getBaseTraversal(query)
                .project(FIELD_ID, FIELD_DECK_ID, FIELD_DECK_ORDER, FIELD_FRONT, FIELD_BACK)
                .by(__.values(FlashcardVertex.PROPERTY_ID))
                .by(__.in(FlashcardDeckVertex.EDGE_FLASHCARD).values(FlashcardDeckVertex.PROPERTY_ID).limit(1))
                .by(__.inE(FlashcardDeckVertex.EDGE_FLASHCARD).values(FlashcardDeckVertex.EDGE_FLASHCARD_PROPERTY_ORDER).limit(1))
                .by(createContentProjection(FlashcardVertex.EDGE_LEFT_CONTENT))
                .by(createContentProjection(FlashcardVertex.EDGE_RIGHT_CONTENT))
                .toList();

        return rawFlashcards.stream()
                .map(this::createFlashcardModel)
                .toList();
    }

    private GraphTraversal<?, ?> getBaseTraversal(GetAllFlashcardsQuery query) {
        if (query.getDeckId() == null) {
            return traversalSource.V().hasLabel(FlashcardVertex.LABEL);
        }

        return traversalSource.V()
                .has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, query.getDeckId())
                .out(FlashcardDeckVertex.EDGE_FLASHCARD);
    }

    private GraphTraversal<?, Map<String, Object>> createContentProjection(String edgeLabel) {
        return __.out(edgeLabel)
                .limit(1)
                .project(FIELD_TYPE, FIELD_ID, FIELD_URL, FIELD_TEXT, FIELD_LANGUAGE_ID)
                .by(__.label())
                .by(__.values(ContentVertex.PROPERTY_ID))
                .by(__.coalesce(
                        __.values(AudioContentVertex.PROPERTY_URL),
                        __.values(ImageContentVertex.PROPERTY_URL),
                        __.values(VideoContentVertex.PROPERTY_URL),
                        __.constant(null)))
                .by(__.coalesce(__.values(TextContentVertex.PROPERTY_VALUE), __.constant(null)))
                .by(__.coalesce(
                        __.out(TextContentVertex.EDGE_LANGUAGE).values(LanguageVertex.PROPERTY_ID),
                        __.constant(null)));
    }

    private FlashcardDto createFlashcardModel(Map<String, Object> rawFlashcard) {
        var model = new FlashcardDto();
        model.setId((String) rawFlashcard.get(FIELD_ID));
        model.setDeckId((String) rawFlashcard.get(FIELD_DECK_ID));
        model.setDeckOrder(((Number) rawFlashcard.get(FIELD_DECK_ORDER)).intValue());
        model.setFrontContent(createContentModel(rawFlashcard.get(FIELD_FRONT)));
        model.setBackContent(createContentModel(rawFlashcard.get(FIELD_BACK)));
        return model;
    }

    private FlashcardContentDto createContentModel(Object rawContent) {
        var content = toStringObjectMap(rawContent);
        var contentType = (String) content.get(FIELD_TYPE);

        return switch (contentType) {
            case AudioContentVertex.LABEL -> {
                var model = new FlashcardContentAudioDto();
                model.setId((String) content.get(FIELD_ID));
                model.setAudioUrl((String) content.get(FIELD_URL));
                yield model;
            }
            case ImageContentVertex.LABEL -> {
                var model = new FlashcardContentImageDto();
                model.setId((String) content.get(FIELD_ID));
                model.setImageUrl((String) content.get(FIELD_URL));
                yield model;
            }
            case TextContentVertex.LABEL -> {
                var model = new FlashcardContentTextDto();
                model.setId((String) content.get(FIELD_ID));
                model.setText((String) content.get(FIELD_TEXT));
                model.setLanguageId((String) content.get(FIELD_LANGUAGE_ID));
                yield model;
            }
            case VideoContentVertex.LABEL -> {
                var model = new FlashcardContentVideoDto();
                model.setId((String) content.get(FIELD_ID));
                model.setVideoUrl((String) content.get(FIELD_URL));
                yield model;
            }
            default -> throw new IllegalArgumentException("Invalid content type: " + contentType);
        };
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toStringObjectMap(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalStateException("Expected map content projection but received: " + value);
        }

        return (Map<String, Object>) map;
    }
}
