package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordSlotDto;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FlashcardImportRecordHandler implements ImportRecordHandler {
    @Override
    public String getSupportedRecordType() {
        return "flashcard";
    }

    @Override
    public void importRecord(ImportRecordDto record, ImportProcessingContext context) {
        var frontSlot = findSlot(record, "front", "left");
        var backSlot = findSlot(record, "back", "right");
        if (frontSlot == null || backSlot == null) {
            throw new IllegalArgumentException("Flashcard record must contain front/back slots");
        }

        var frontContent = context.resolveContentRef(frontSlot.getContentRef());
        var backContent = context.resolveContentRef(backSlot.getContentRef());

        var flashcardVertex = FlashcardVertex.create(context.getTraversalSource());
        flashcardVertex.setId(record.getRecordId() != null ? record.getRecordId() : UUID.randomUUID().toString());
        flashcardVertex.setLeftContent(frontContent);
        flashcardVertex.setRightContent(backContent);

        context.getDeckVertex().addFlashcard(flashcardVertex);

        var deckOrder = ImportSupport.readInteger(record.getAttributes(), "deckOrder");
        if (deckOrder != null) {
            flashcardVertex.setDeckOrder(deckOrder);
        }

        if (frontSlot.getConceptRefs() != null) {
            for (var conceptRef : frontSlot.getConceptRefs()) {
                context.applyConceptRef(conceptRef);
            }
        }

        if (backSlot.getConceptRefs() != null) {
            for (var conceptRef : backSlot.getConceptRefs()) {
                context.applyConceptRef(conceptRef);
            }
        }

        context.trackCreatedFlashcardId(flashcardVertex.getId());
    }

    private ImportRecordSlotDto findSlot(ImportRecordDto record, String primary, String alternate) {
        if (record.getSlots() == null) {
            return null;
        }

        return record.getSlots().stream()
                .filter(slot -> primary.equals(slot.getSlotKey()) || alternate.equals(slot.getSlotKey()))
                .findFirst()
                .orElse(null);
    }
}

