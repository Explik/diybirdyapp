package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PronunciationImportConceptHandler implements ImportConceptHandler {
    @Override
    public String getSupportedConceptType() {
        return "pronunciation";
    }

    @Override
    public void applyConcept(ImportConceptDto concept, ImportProcessingContext context) {
        var payload = concept.getPayload();
        var sourceContentRef = ImportSupport.requireString(payload, "sourceContentRef");
        var pronunciationContentRef = ImportSupport.requireString(payload, "pronunciationContentRef");

        var sourceContent = context.resolveContentRef(sourceContentRef);
        var pronunciationContent = context.resolveContentRef(pronunciationContentRef);

        if (!(sourceContent instanceof TextContentVertex sourceText)) {
            throw new IllegalArgumentException("Pronunciation concept requires text source content");
        }

        if (!(pronunciationContent instanceof AudioContentVertex pronunciationAudio)) {
            throw new IllegalArgumentException("Pronunciation concept requires audio pronunciation content");
        }

        if (!context.hasAudioLanguage(pronunciationAudio)) {
            pronunciationAudio.setLanguage(sourceText.getLanguage());
        }

        var pronunciationId = concept.getConceptId() != null ? concept.getConceptId() : UUID.randomUUID().toString();
        if (PronunciationVertex.findById(context.getTraversalSource(), pronunciationId) != null) {
            return;
        }

        var pronunciationVertex = PronunciationVertex.create(context.getTraversalSource());
        pronunciationVertex.setId(pronunciationId);
        pronunciationVertex.setTextContent(sourceText);
        pronunciationVertex.setAudioContent(pronunciationAudio);

        // Keep compatibility with existing crawlers expecting text -> pronunciation edges.
        var hasEdge = context.getTraversalSource()
                .V(sourceText.getUnderlyingVertex())
                .out(TextContentVertex.EDGE_PRONUNCIATION)
                .has(PronunciationVertex.PROPERTY_ID, pronunciationVertex.getId())
                .hasNext();

        if (!hasEdge) {
            context.getTraversalSource()
                    .V(sourceText.getUnderlyingVertex())
                    .addE(TextContentVertex.EDGE_PRONUNCIATION)
                    .to(pronunciationVertex.getUnderlyingVertex())
                    .iterate();
        }

        context.trackCreatedPronunciationId(pronunciationVertex.getId());
    }
}

