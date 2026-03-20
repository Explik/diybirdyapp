package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportAttachmentModel;
import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportIssueDto;
import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.model.imports.ImportManifestModel;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ImportProcessingContext {
    private final ImportJobModel job;
    private final ImportManifestModel manifest;
    private final GraphTraversalSource traversalSource;
    private final ImportContentHandlerRegistry contentHandlerRegistry;
    private final ImportConceptHandlerRegistry conceptHandlerRegistry;
    private final FlashcardDeckVertex deckVertex;
    private final Map<String, ImportContentDto> contentById;
    private final Map<String, ImportConceptDto> conceptById;

    private final Map<String, ContentVertex> resolvedContentByRef = new ConcurrentHashMap<>();
    private final Set<String> appliedConceptIds = ConcurrentHashMap.newKeySet();

    private final Set<String> createdFlashcardIds = ConcurrentHashMap.newKeySet();
    private final Set<String> createdContentIds = ConcurrentHashMap.newKeySet();
    private final Set<String> createdPronunciationIds = ConcurrentHashMap.newKeySet();
    private final Set<String> createdTranscriptionIds = ConcurrentHashMap.newKeySet();

    public ImportProcessingContext(
            ImportJobModel job,
            ImportManifestModel manifest,
            GraphTraversalSource traversalSource,
            ImportContentHandlerRegistry contentHandlerRegistry,
            ImportConceptHandlerRegistry conceptHandlerRegistry,
            FlashcardDeckVertex deckVertex) {
        this.job = job;
        this.manifest = manifest;
        this.traversalSource = traversalSource;
        this.contentHandlerRegistry = contentHandlerRegistry;
        this.conceptHandlerRegistry = conceptHandlerRegistry;
        this.deckVertex = deckVertex;

        this.contentById = new HashMap<>();
        for (var content : manifest.getContents()) {
            this.contentById.put(content.getContentId(), content);
        }

        this.conceptById = new HashMap<>();
        for (var concept : manifest.getConcepts()) {
            this.conceptById.put(concept.getConceptId(), concept);
        }
    }

    public ImportJobModel getJob() {
        return job;
    }

    public ImportManifestModel getManifest() {
        return manifest;
    }

    public GraphTraversalSource getTraversalSource() {
        return traversalSource;
    }

    public FlashcardDeckVertex getDeckVertex() {
        return deckVertex;
    }

    public ContentVertex resolveContentRef(String contentRef) {
        if (contentRef == null || contentRef.isBlank()) {
            throw new IllegalArgumentException("contentRef is required");
        }

        var resolved = resolvedContentByRef.get(contentRef);
        if (resolved != null) {
            return resolved;
        }

        var content = contentById.get(contentRef);
        if (content == null) {
            throw new IllegalArgumentException("Unknown contentRef: " + contentRef);
        }

        var handler = contentHandlerRegistry.getRequired(content.getContentType());
        var created = handler.createContent(content, this);
        resolvedContentByRef.put(contentRef, created);
        return created;
    }

    public void applyConceptRef(String conceptRef) {
        if (conceptRef == null || conceptRef.isBlank()) {
            return;
        }

        if (appliedConceptIds.contains(conceptRef)) {
            return;
        }

        var concept = conceptById.get(conceptRef);
        if (concept == null) {
            throw new IllegalArgumentException("Unknown conceptRef: " + conceptRef);
        }

        var handler = conceptHandlerRegistry.getRequired(concept.getConceptType());
        handler.applyConcept(concept, this);
        appliedConceptIds.add(conceptRef);
    }

    public ImportAttachmentModel getDeclaredAttachment(String fileRef) {
        return job.getAttachments().get(ImportSupport.normalizeFileRef(fileRef));
    }

    public ImportAttachmentModel requireDeclaredAttachment(String fileRef) {
        var attachment = getDeclaredAttachment(fileRef);
        if (attachment == null) {
            throw new IllegalArgumentException("Undeclared fileRef: " + fileRef);
        }

        return attachment;
    }

    public LanguageVertex findLanguageById(String languageId) {
        var vertex = traversalSource.V()
                .has(LanguageVertex.LABEL, LanguageVertex.PROPERTY_ID, languageId)
                .tryNext()
                .orElse(null);

        if (vertex == null) {
            throw new IllegalArgumentException("Language not found: " + languageId);
        }

        return new LanguageVertex(traversalSource, vertex);
    }

    public boolean hasAudioLanguage(AudioContentVertex audioContentVertex) {
        return traversalSource.V(audioContentVertex.getUnderlyingVertex())
                .out(AudioContentVertex.EDGE_LANGUAGE)
                .hasNext();
    }

    public void addError(String code, String message, String recordId, String contentId) {
        var issue = new ImportIssueDto();
        issue.setCode(code);
        issue.setMessage(message);
        issue.setRecordId(recordId);
        issue.setContentId(contentId);
        job.getErrors().add(issue);
    }

    public Set<String> getCreatedFlashcardIds() {
        return createdFlashcardIds;
    }

    public Set<String> getCreatedContentIds() {
        return createdContentIds;
    }

    public Set<String> getCreatedPronunciationIds() {
        return createdPronunciationIds;
    }

    public Set<String> getCreatedTranscriptionIds() {
        return createdTranscriptionIds;
    }

    public void trackCreatedFlashcardId(String id) {
        createdFlashcardIds.add(id);
    }

    public void trackCreatedContentId(String id) {
        createdContentIds.add(id);
    }

    public void trackCreatedPronunciationId(String id) {
        createdPronunciationIds.add(id);
    }

    public void trackCreatedTranscriptionId(String id) {
        createdTranscriptionIds.add(id);
    }
}

