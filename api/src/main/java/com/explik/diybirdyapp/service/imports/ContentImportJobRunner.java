package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportAttachmentState;
import com.explik.diybirdyapp.model.imports.ImportExecutionState;
import com.explik.diybirdyapp.controller.model.imports.ImportIssueDto;
import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.model.imports.ImportJobState;
import com.explik.diybirdyapp.model.imports.ImportManifestModel;
import com.explik.diybirdyapp.model.imports.ImportResultModel;
import com.explik.diybirdyapp.model.imports.ImportStage;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TranscriptionVertex;
import com.explik.diybirdyapp.persistence.vertex.UserVertex;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class ContentImportJobRunner {
    private final ImportJobRepository importJobRepository;
    private final ImportPayloadStore importPayloadStore;
    private final ImportRecordHandlerRegistry importRecordHandlerRegistry;
    private final ImportContentHandlerRegistry importContentHandlerRegistry;
    private final ImportConceptHandlerRegistry importConceptHandlerRegistry;
    private final GraphTraversalSource traversalSource;

    public ContentImportJobRunner(
            ImportJobRepository importJobRepository,
            ImportPayloadStore importPayloadStore,
            ImportRecordHandlerRegistry importRecordHandlerRegistry,
            ImportContentHandlerRegistry importContentHandlerRegistry,
            ImportConceptHandlerRegistry importConceptHandlerRegistry,
            GraphTraversalSource traversalSource) {
        this.importJobRepository = importJobRepository;
        this.importPayloadStore = importPayloadStore;
        this.importRecordHandlerRegistry = importRecordHandlerRegistry;
        this.importContentHandlerRegistry = importContentHandlerRegistry;
        this.importConceptHandlerRegistry = importConceptHandlerRegistry;
        this.traversalSource = traversalSource;
    }

    @Async
    public void runJob(String jobId) {
        var optionalJob = importJobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            return;
        }

        var job = optionalJob.get();
        var manifest = importPayloadStore.get(job.getManifestKey());
        if (manifest == null) {
            failJob(job, "MANIFEST_NOT_FOUND", "Import manifest not found for job");
            importJobRepository.save(job);
            return;
        }

        ImportProcessingContext processingContext = null;
        try {
            if (job.isCancelRequested()) {
                cancelJob(job, null, manifest);
                importJobRepository.save(job);
                return;
            }

            transition(job, ImportJobState.VALIDATING, ImportStage.VALIDATE_REQUEST);
            job.setStartedAt(Instant.now());

            transition(job, ImportJobState.RUNNING, ImportStage.REGISTER_ATTACHMENTS);
            job.getExecutionState().setTotalRecords(manifest.getRecords().size());

            if (manifest.getOptions() != null && manifest.getOptions().isDryRun()) {
                completeDryRun(job);
                importJobRepository.save(job);
                return;
            }

            transition(job, ImportJobState.RUNNING, ImportStage.CREATE_TARGET_CONTAINER);
            var targetDeck = createTargetDeck(job, manifest);

            var result = new ImportResultModel();
            result.setCreatedRootType("flashcard-deck");
            result.setCreatedRootId(targetDeck.getId());
            job.setResult(result);

            processingContext = new ImportProcessingContext(
                    job,
                    manifest,
                    traversalSource,
                    importContentHandlerRegistry,
                    importConceptHandlerRegistry,
                    targetDeck);

            transition(job, ImportJobState.RUNNING, ImportStage.IMPORT_RECORDS);

            var failFast = isFailFast(manifest);
            for (var record : manifest.getRecords()) {
                if (job.isCancelRequested()) {
                    cancelJob(job, processingContext, manifest);
                    importJobRepository.save(job);
                    return;
                }

                try {
                    importRecordHandlerRegistry.getRequired(record.getRecordType()).importRecord(record, processingContext);
                    job.getExecutionState().setSuccessfulRecords(job.getExecutionState().getSuccessfulRecords() + 1);
                } catch (Exception ex) {
                    processingContext.addError("RECORD_IMPORT_FAILED", safeMessage(ex), record.getRecordId(), null);
                    job.getExecutionState().setFailedRecords(job.getExecutionState().getFailedRecords() + 1);
                    if (failFast) {
                        throw ex;
                    }
                } finally {
                    job.getExecutionState().setProcessedRecords(job.getExecutionState().getProcessedRecords() + 1);
                }
            }

            transition(job, ImportJobState.RUNNING, ImportStage.WAIT_FOR_REQUIRED_MEDIA);
            while (hasMissingRequiredAttachments(job)) {
                if (job.isCancelRequested()) {
                    cancelJob(job, processingContext, manifest);
                    importJobRepository.save(job);
                    return;
                }

                Thread.sleep(200);
            }

            transition(job, ImportJobState.RUNNING, ImportStage.FINALIZE);
            finalizeResult(job, processingContext);
            job.setStatus(ImportJobState.COMPLETED);
            job.setCompletedAt(Instant.now());
            importJobRepository.save(job);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            failJob(job, "INTERRUPTED", "Import job was interrupted");
            importJobRepository.save(job);
        } catch (Exception ex) {
            failJob(job, "IMPORT_FAILED", safeMessage(ex));
            importJobRepository.save(job);
        }
    }

    private void completeDryRun(ImportJobModel job) {
        transition(job, ImportJobState.RUNNING, ImportStage.FINALIZE);

        var result = new ImportResultModel();
        result.setCreatedRootType("flashcard-deck");
        result.setSummary(Map.of(
                "recordsCreated", 0,
                "mediaFilesStored", 0,
                "conceptsApplied", 0,
                "dryRun", true));

        job.setResult(result);
        job.setStatus(ImportJobState.COMPLETED);
        job.setCompletedAt(Instant.now());
    }

    private FlashcardDeckVertex createTargetDeck(ImportJobModel job, ImportManifestModel manifest) {
        var deck = FlashcardDeckVertex.create(traversalSource);
        deck.setId((manifest.getSetId() != null && !manifest.getSetId().isBlank())
                ? manifest.getSetId()
                : UUID.randomUUID().toString());

        var name = "Imported Deck";
        var description = "";
        if (manifest.getTarget() != null) {
            var targetMetadata = manifest.getTarget().getMetadata();
            if (targetMetadata != null) {
                if (targetMetadata.get("name") != null) {
                    name = String.valueOf(targetMetadata.get("name"));
                }
                if (targetMetadata.get("description") != null) {
                    description = String.valueOf(targetMetadata.get("description"));
                }
            }
        }

        deck.setName(name);
        deck.setDescription(description);

        if (job.getUserId() != null && !job.getUserId().isBlank()) {
            var owner = UserVertex.findWithEmail(traversalSource, job.getUserId());
            if (owner != null) {
                deck.setOwner(owner);
            }
        }

        return deck;
    }

    private boolean isFailFast(ImportManifestModel manifest) {
        if (manifest.getOptions() == null || manifest.getOptions().getOnError() == null) {
            return true;
        }

        return !"CONTINUE".equalsIgnoreCase(manifest.getOptions().getOnError());
    }

    private boolean hasMissingRequiredAttachments(ImportJobModel job) {
        return job.getAttachments().values().stream()
                .anyMatch(attachment -> attachment.isRequired() && attachment.getState() != ImportAttachmentState.READY);
    }

    private void finalizeResult(ImportJobModel job, ImportProcessingContext context) {
        var result = job.getResult() == null ? new ImportResultModel() : job.getResult();
        if (result.getCreatedRootType() == null) {
            result.setCreatedRootType("flashcard-deck");
        }

        var summary = new HashMap<String, Object>();
        summary.put("recordsCreated", context.getCreatedFlashcardIds().size());
        summary.put("mediaFilesStored", job.getAttachments().values().stream().filter(a -> a.getState() == ImportAttachmentState.READY).count());
        summary.put("conceptsApplied", context.getCreatedPronunciationIds().size() + context.getCreatedTranscriptionIds().size());
        result.setSummary(summary);
        job.setResult(result);
    }

    private void cancelJob(ImportJobModel job, ImportProcessingContext context, ImportManifestModel manifest) {
        job.setStatus(ImportJobState.CANCELLING);
        if (shouldRollbackOnCancel(manifest)) {
            job.getExecutionState().setStage(ImportStage.ROLLBACK);
            rollback(context, job);
        }

        job.setStatus(ImportJobState.CANCELLED);
        job.setCompletedAt(Instant.now());
    }

    private boolean shouldRollbackOnCancel(ImportManifestModel manifest) {
        if (manifest.getOptions() == null || manifest.getOptions().getOnCancel() == null) {
            return true;
        }

        return "ROLLBACK".equalsIgnoreCase(manifest.getOptions().getOnCancel());
    }

    private void rollback(ImportProcessingContext context, ImportJobModel job) {
        if (context != null) {
            dropVerticesByLabelAndProperty(PronunciationVertex.LABEL, PronunciationVertex.PROPERTY_ID, context.getCreatedPronunciationIds());
            dropVerticesByLabelAndProperty(TranscriptionVertex.LABEL, TranscriptionVertex.PROPERTY_ID, context.getCreatedTranscriptionIds());
            dropVerticesByLabelAndProperty(FlashcardVertex.LABEL, FlashcardVertex.PROPERTY_ID, context.getCreatedFlashcardIds());
            dropVerticesByProperty(ContentVertex.PROPERTY_ID, context.getCreatedContentIds());

            if (context.getDeckVertex() != null) {
                traversalSource.V(context.getDeckVertex().getUnderlyingVertex()).drop().iterate();
            }
            return;
        }

        if (job.getResult() != null && job.getResult().getCreatedRootId() != null) {
            traversalSource.V()
                    .has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, job.getResult().getCreatedRootId())
                    .drop()
                    .iterate();
        }
    }

    private void dropVerticesByLabelAndProperty(String label, String property, Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        traversalSource.V()
                .hasLabel(label)
                .has(property, P.within(ids))
                .drop()
                .iterate();
    }

    private void dropVerticesByProperty(String property, Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        traversalSource.V()
                .has(property, P.within(ids))
                .drop()
                .iterate();
    }

    private void transition(ImportJobModel job, ImportJobState state, ImportStage stage) {
        job.setStatus(state);

        var executionState = job.getExecutionState();
        if (executionState == null) {
            executionState = new ImportExecutionState();
            job.setExecutionState(executionState);
        }

        executionState.setStage(stage);
    }

    private void failJob(ImportJobModel job, String code, String message) {
        var issue = new ImportIssueDto();
        issue.setCode(code);
        issue.setMessage(message);
        job.getErrors().add(issue);

        job.setStatus(ImportJobState.FAILED);
        job.setCompletedAt(Instant.now());
    }

    private String safeMessage(Exception ex) {
        var message = ex.getMessage();
        return message == null || message.isBlank() ? ex.getClass().getSimpleName() : message;
    }
}

