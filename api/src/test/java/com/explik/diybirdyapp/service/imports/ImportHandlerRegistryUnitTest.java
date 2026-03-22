package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportHandlerRegistryUnitTest {
    @Test
    void givenRecordHandlers_whenLookup_thenSupportsAndSorts() {
        var handlerA = new TestRecordHandler("flashcard");
        var handlerB = new TestRecordHandler("custom");
        var registry = new ImportRecordHandlerRegistry(List.of(handlerA, handlerB));

        assertTrue(registry.supports("flashcard"));
        assertFalse(registry.supports("missing"));
        assertSame(handlerA, registry.getRequired("flashcard"));
        assertEquals(List.of("custom", "flashcard"), registry.supportedTypes());
        assertThrows(IllegalArgumentException.class, () -> registry.getRequired("missing"));
    }

    @Test
    void givenContentHandlers_whenLookup_thenSupportsAndSorts() {
        var handlerA = new TestContentHandler("text");
        var handlerB = new TestContentHandler("audio-upload");
        var registry = new ImportContentHandlerRegistry(List.of(handlerA, handlerB));

        assertTrue(registry.supports("text"));
        assertFalse(registry.supports("missing"));
        assertSame(handlerA, registry.getRequired("text"));
        assertEquals(List.of("audio-upload", "text"), registry.supportedTypes());
        assertThrows(IllegalArgumentException.class, () -> registry.getRequired("missing"));
    }

    @Test
    void givenConceptHandlers_whenLookup_thenSupportsAndSorts() {
        var handlerA = new TestConceptHandler("pronunciation");
        var handlerB = new TestConceptHandler("transcription");
        var registry = new ImportConceptHandlerRegistry(List.of(handlerA, handlerB));

        assertTrue(registry.supports("pronunciation"));
        assertFalse(registry.supports("missing"));
        assertSame(handlerA, registry.getRequired("pronunciation"));
        assertEquals(List.of("pronunciation", "transcription"), registry.supportedTypes());
        assertThrows(IllegalArgumentException.class, () -> registry.getRequired("missing"));
    }

    private static class TestRecordHandler implements ImportRecordHandler {
        private final String supportedType;

        private TestRecordHandler(String supportedType) {
            this.supportedType = supportedType;
        }

        @Override
        public String getSupportedRecordType() {
            return supportedType;
        }

        @Override
        public void importRecord(ImportRecordDto record, ImportProcessingContext context) {
        }
    }

    private static class TestContentHandler implements ImportContentHandler {
        private final String supportedType;

        private TestContentHandler(String supportedType) {
            this.supportedType = supportedType;
        }

        @Override
        public String getSupportedContentType() {
            return supportedType;
        }

        @Override
        public ContentVertex createContent(ImportContentDto content, ImportProcessingContext context) {
            return null;
        }
    }

    private static class TestConceptHandler implements ImportConceptHandler {
        private final String supportedType;

        private TestConceptHandler(String supportedType) {
            this.supportedType = supportedType;
        }

        @Override
        public String getSupportedConceptType() {
            return supportedType;
        }

        @Override
        public void applyConcept(ImportConceptDto concept, ImportProcessingContext context) {
        }
    }
}

