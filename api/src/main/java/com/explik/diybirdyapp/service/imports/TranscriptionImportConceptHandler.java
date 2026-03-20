package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TranscriptionSystemVertex;
import com.explik.diybirdyapp.persistence.vertex.TranscriptionVertex;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TranscriptionImportConceptHandler implements ImportConceptHandler {
    @Override
    public String getSupportedConceptType() {
        return "transcription";
    }

    @Override
    public void applyConcept(ImportConceptDto concept, ImportProcessingContext context) {
        var payload = concept.getPayload();
        var sourceContentRef = ImportSupport.requireString(payload, "sourceContentRef");
        var transcriptionText = ImportSupport.requireString(payload, "transcription");
        var transcriptionSystem = ImportSupport.requireString(payload, "transcriptionSystem");

        var sourceContent = context.resolveContentRef(sourceContentRef);
        if (!(sourceContent instanceof TextContentVertex sourceText)) {
            throw new IllegalArgumentException("Transcription concept requires text source content");
        }

        var transcriptionId = concept.getConceptId() != null ? concept.getConceptId() : UUID.randomUUID().toString();
        if (TranscriptionVertex.findById(context.getTraversalSource(), transcriptionId) != null) {
            return;
        }

        var transcriptionSystemVertex = TranscriptionSystemVertex.findById(context.getTraversalSource(), transcriptionSystem);
        if (transcriptionSystemVertex == null) {
            transcriptionSystemVertex = TranscriptionSystemVertex.create(context.getTraversalSource());
            transcriptionSystemVertex.setId(transcriptionSystem);
        }

        var transcriptionTextContent = TextContentVertex.create(context.getTraversalSource());
        transcriptionTextContent.setId(UUID.randomUUID().toString());
        transcriptionTextContent.setValue(transcriptionText);
        transcriptionTextContent.setLanguage(sourceText.getLanguage());

        var transcriptionVertex = TranscriptionVertex.create(context.getTraversalSource());
        transcriptionVertex.setId(transcriptionId);
        transcriptionVertex.setSourceContent(sourceText);
        transcriptionVertex.setTextContent(transcriptionTextContent);
        transcriptionVertex.setSystem(transcriptionSystemVertex);

        context.trackCreatedTranscriptionId(transcriptionVertex.getId());
        context.trackCreatedContentId(transcriptionTextContent.getId());
    }
}

