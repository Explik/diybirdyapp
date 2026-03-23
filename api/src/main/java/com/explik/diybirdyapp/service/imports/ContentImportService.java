package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentStatusDto;
import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentUploadResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportIssueDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateRequestDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobProgressDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobResultDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobStatusDto;
import com.explik.diybirdyapp.controller.model.imports.ImportOptionsDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;
import com.explik.diybirdyapp.controller.model.imports.ImportSourceDto;
import com.explik.diybirdyapp.controller.model.imports.ImportTargetDto;
import com.explik.diybirdyapp.model.imports.ImportAttachmentModel;
import com.explik.diybirdyapp.model.imports.ImportAttachmentState;
import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.model.imports.ImportJobState;
import com.explik.diybirdyapp.model.imports.ImportManifestModel;
import com.explik.diybirdyapp.model.imports.ImportStage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class ContentImportService {
    private static final String SUPPORTED_SCHEMA_VERSION = "1.0";

    private final ImportJobRepository importJobRepository;
    private final ImportPayloadStore importPayloadStore;
    private final ContentImportJobRunner contentImportJobRunner;
    private final ContentImportAttachmentService contentImportAttachmentService;
    private final ImportRecordHandlerRegistry importRecordHandlerRegistry;
    private final ImportContentHandlerRegistry importContentHandlerRegistry;
    private final ImportConceptHandlerRegistry importConceptHandlerRegistry;
    private final ObjectMapper objectMapper;

    private final long maxPayloadBytes;
    private final long maxAttachmentChunkBytes;
    private final long maxTotalAttachmentBytesPerJob;

    public ContentImportService(
            ImportJobRepository importJobRepository,
            ImportPayloadStore importPayloadStore,
            ContentImportJobRunner contentImportJobRunner,
            ContentImportAttachmentService contentImportAttachmentService,
            ImportRecordHandlerRegistry importRecordHandlerRegistry,
            ImportContentHandlerRegistry importContentHandlerRegistry,
            ImportConceptHandlerRegistry importConceptHandlerRegistry,
            ObjectMapper objectMapper,
            @Value("${content-import.max-payload-bytes:52428800}") long maxPayloadBytes,
            @Value("${content-import.max-attachment-chunk-bytes:10485760}") long maxAttachmentChunkBytes,
            @Value("${content-import.max-total-attachment-bytes-per-job:262144000}") long maxTotalAttachmentBytesPerJob) {
        this.importJobRepository = importJobRepository;
        this.importPayloadStore = importPayloadStore;
        this.contentImportJobRunner = contentImportJobRunner;
        this.contentImportAttachmentService = contentImportAttachmentService;
        this.importRecordHandlerRegistry = importRecordHandlerRegistry;
        this.importContentHandlerRegistry = importContentHandlerRegistry;
        this.importConceptHandlerRegistry = importConceptHandlerRegistry;
        this.objectMapper = objectMapper;
        this.maxPayloadBytes = maxPayloadBytes;
        this.maxAttachmentChunkBytes = maxAttachmentChunkBytes;
        this.maxTotalAttachmentBytesPerJob = maxTotalAttachmentBytesPerJob;
    }

    public ImportJobCreateResponseDto submitJob(String userId, ImportJobCreateRequestDto request) {
        byte[] payloadBytes;
        try {
            payloadBytes = objectMapper.writeValueAsBytes(request);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed import payload");
        }

        if (payloadBytes.length > maxPayloadBytes) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Manifest payload exceeds configured limit");
        }

        var manifest = mapToManifest(request);
        validateManifest(manifest);

        var declaredAttachmentBytes = manifest.getAttachments().stream()
                .mapToLong(ImportAttachmentModel::getSizeBytes)
                .sum();
        if (declaredAttachmentBytes > maxTotalAttachmentBytesPerJob) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Declared attachment size exceeds per-job limit");
        }

        var payloadHash = ImportSupport.md5Hex(new String(payloadBytes, StandardCharsets.UTF_8));

        if (manifest.getClientRequestId() != null && !manifest.getClientRequestId().isBlank()) {
            var existing = importJobRepository.findByUserAndClientRequestId(userId, manifest.getClientRequestId());
            if (existing.isPresent()) {
                if (!payloadHash.equals(existing.get().getPayloadHash())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate clientRequestId with incompatible payload");
                }

                return toCreateResponse(existing.get());
            }
        }

        var job = new ImportJobModel();
        job.setJobId("imp_" + UUID.randomUUID().toString().replace("-", ""));
        job.setUserId(userId);
        job.setClientRequestId(manifest.getClientRequestId());
        job.setPayloadHash(payloadHash);
        job.getExecutionState().setStage(ImportStage.VALIDATE_REQUEST);

        for (var attachment : manifest.getAttachments()) {
            job.getAttachments().put(attachment.getFileRef(), attachment);
        }

        var manifestKey = importPayloadStore.put(manifest);
        job.setManifestKey(manifestKey);

        importJobRepository.save(job);
        contentImportJobRunner.runJob(job.getJobId());

        return toCreateResponse(job);
    }

    public ImportAttachmentUploadResponseDto uploadAttachment(
            String userId,
            String jobId,
            String fileRef,
            MultipartFile chunk,
            Integer chunkIndex,
            Integer totalChunks,
            String chunkChecksum,
            String finalChecksum) {
        var job = getOwnedJob(userId, jobId);
        if (!job.isUploadAllowed()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Job is not in upload-allowed state");
        }

        var normalizedFileRef = ImportSupport.normalizeFileRef(fileRef);
        var attachment = job.getAttachments().get(normalizedFileRef);
        if (attachment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Undeclared fileRef: " + fileRef);
        }

        var response = contentImportAttachmentService.receiveChunk(
                job,
                attachment,
                chunk,
                chunkIndex,
                totalChunks,
                chunkChecksum,
                finalChecksum);

        importJobRepository.save(job);
        return response;
    }

    public ImportJobStatusDto getJobStatus(String userId, String jobId) {
        var job = getOwnedJob(userId, jobId);
        return toStatusResponse(job);
    }

    public ImportJobStatusDto cancelJob(String userId, String jobId) {
        var job = getOwnedJob(userId, jobId);
        if (job.isTerminal()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Job is already in terminal state");
        }

        job.setCancelRequested(true);
        if (job.getStatus() == ImportJobState.QUEUED
                || job.getStatus() == ImportJobState.VALIDATING
                || job.getStatus() == ImportJobState.RUNNING) {
            job.setStatus(ImportJobState.CANCELLING);
        }
        job.setPollAfterMs(1000);

        importJobRepository.save(job);
        return toStatusResponse(job);
    }

    public Map<String, Object> getCapabilities() {
        var response = new HashMap<String, Object>();
        response.put("schemaVersions", List.of(SUPPORTED_SCHEMA_VERSION));
        response.put("recordTypes", importRecordHandlerRegistry.supportedTypes());
        response.put("contentTypes", importContentHandlerRegistry.supportedTypes());
        response.put("conceptTypes", importConceptHandlerRegistry.supportedTypes());
        response.put("supportsDeferredMediaUpload", true);
        response.put("maxPayloadBytes", maxPayloadBytes);
        response.put("maxAttachmentChunkBytes", maxAttachmentChunkBytes);
        response.put("maxTotalAttachmentBytesPerJob", maxTotalAttachmentBytesPerJob);
        return response;
    }

    private ImportJobModel getOwnedJob(String userId, String jobId) {
        var optionalJob = importJobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Import job not found");
        }

        var job = optionalJob.get();
        if (!safeEquals(job.getUserId(), userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Import job not found");
        }

        return job;
    }

    private boolean safeEquals(String left, String right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        return left.equals(right);
    }

    private void validateManifest(ImportManifestModel manifest) {
        if (!SUPPORTED_SCHEMA_VERSION.equals(manifest.getSchemaVersion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported schemaVersion");
        }

        if (manifest.getRecords() == null) {
            manifest.setRecords(new ArrayList<>());
        }
        if (manifest.getContents() == null) {
            manifest.setContents(new ArrayList<>());
        }
        if (manifest.getConcepts() == null) {
            manifest.setConcepts(new ArrayList<>());
        }
        if (manifest.getAttachments() == null) {
            manifest.setAttachments(new ArrayList<>());
        }

        var dryRun = manifest.getOptions() != null && manifest.getOptions().isDryRun();
        if (!dryRun && manifest.getRecords().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "contentSet.records must not be empty");
        }

        ensureUnique("recordId", manifest.getRecords().stream().map(ImportRecordDto::getRecordId).toList());
        ensureUnique("contentId", manifest.getContents().stream().map(ImportContentDto::getContentId).toList());
        ensureUnique("conceptId", manifest.getConcepts().stream().map(ImportConceptDto::getConceptId).toList());
        ensureUnique("attachment.fileRef", manifest.getAttachments().stream().map(ImportAttachmentModel::getFileRef).toList());

        var contentIds = new HashSet<>(manifest.getContents().stream().map(ImportContentDto::getContentId).toList());
        var conceptIds = new HashSet<>(manifest.getConcepts().stream().map(ImportConceptDto::getConceptId).toList());
        var declaredFileRefs = new HashSet<>(manifest.getAttachments().stream().map(ImportAttachmentModel::getFileRef).toList());

        for (var record : manifest.getRecords()) {
            if (record.getRecordType() == null || record.getRecordType().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recordType is required");
            }
            if (!importRecordHandlerRegistry.supports(record.getRecordType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No handler registered for recordType: " + record.getRecordType());
            }

            if (record.getSlots() == null) {
                continue;
            }

            for (var slot : record.getSlots()) {
                if (slot.getContentRef() == null || !contentIds.contains(slot.getContentRef())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown contentRef: " + slot.getContentRef());
                }

                if (slot.getConceptRefs() == null) {
                    continue;
                }

                for (var conceptRef : slot.getConceptRefs()) {
                    if (!conceptIds.contains(conceptRef)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown conceptRef: " + conceptRef);
                    }
                }
            }
        }

        for (var content : manifest.getContents()) {
            if (!importContentHandlerRegistry.supports(content.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No handler registered for contentType: " + content.getContentType());
            }

            var payloadFileRefs = new HashSet<String>();
            collectFileRefs(content.getPayload(), payloadFileRefs);
            for (var fileRef : payloadFileRefs) {
                if (!declaredFileRefs.contains(fileRef)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fileRef used in payload is not declared: " + fileRef);
                }
            }
        }

        for (var concept : manifest.getConcepts()) {
            if (!importConceptHandlerRegistry.supports(concept.getConceptType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No handler registered for conceptType: " + concept.getConceptType());
            }

            var payloadFileRefs = new HashSet<String>();
            collectFileRefs(concept.getPayload(), payloadFileRefs);
            for (var fileRef : payloadFileRefs) {
                if (!declaredFileRefs.contains(fileRef)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fileRef used in payload is not declared: " + fileRef);
                }
            }
        }
    }

    private void ensureUnique(String fieldName, List<String> values) {
        var encountered = new HashSet<String>();
        for (var value : values) {
            if (value == null || value.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " values must be non-empty");
            }

            if (!encountered.add(value)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate " + fieldName + ": " + value);
            }
        }
    }

    private void collectFileRefs(Object value, Set<String> output) {
        if (value == null) {
            return;
        }

        if (value instanceof Map<?, ?> map) {
            for (var entry : map.entrySet()) {
                if ("fileRef".equals(String.valueOf(entry.getKey())) && entry.getValue() != null) {
                    output.add(ImportSupport.normalizeFileRef(String.valueOf(entry.getValue())));
                }

                collectFileRefs(entry.getValue(), output);
            }
            return;
        }

        if (value instanceof Iterable<?> iterable) {
            for (var item : iterable) {
                collectFileRefs(item, output);
            }
        }
    }

    private ImportManifestModel mapToManifest(ImportJobCreateRequestDto request) {
        var manifest = new ImportManifestModel();
        manifest.setSchemaVersion(request.getSchemaVersion());
        manifest.setClientRequestId(request.getClientRequestId());
        manifest.setImportType(request.getImportType());
        manifest.setSource(mapSource(request.getSource()));
        manifest.setTarget(mapTarget(request.getTarget()));

        if (request.getContentSet() != null) {
            manifest.setSetType(request.getContentSet().getSetType());
            manifest.setSetId(request.getContentSet().getSetId());
            manifest.setSetMetadata(request.getContentSet().getMetadata());

            if (request.getContentSet().getRecords() != null) {
                manifest.setRecords(request.getContentSet().getRecords());
            }
            if (request.getContentSet().getContents() != null) {
                manifest.setContents(request.getContentSet().getContents());
            }
            if (request.getContentSet().getConcepts() != null) {
                manifest.setConcepts(request.getContentSet().getConcepts());
            }
            if (request.getContentSet().getAttachments() != null) {
                manifest.setAttachments(request.getContentSet().getAttachments().stream().map(this::mapAttachment).toList());
            }
        }

        var optionsModel = new ImportOptionsDto();
        if (request.getOptions() != null) {
            optionsModel.setDryRun(request.getOptions().isDryRun());
            optionsModel.setOnError(request.getOptions().getOnError());
            optionsModel.setOnCancel(request.getOptions().getOnCancel());
        }
        if (optionsModel.getOnError() == null) {
            optionsModel.setOnError("FAIL_FAST");
        }
        if (optionsModel.getOnCancel() == null) {
            optionsModel.setOnCancel("ROLLBACK");
        }
        manifest.setOptions(optionsModel);

        return manifest;
    }

    private ImportSourceDto mapSource(ImportSourceDto source) {
        if (source == null) {
            return null;
        }

        var mapped = new ImportSourceDto();
        mapped.setSourceSystem(source.getSourceSystem());
        mapped.setSourceVersion(source.getSourceVersion());
        mapped.setSourceReference(source.getSourceReference());
        return mapped;
    }

    private ImportTargetDto mapTarget(ImportTargetDto target) {
        if (target == null) {
            return null;
        }

        var mapped = new ImportTargetDto();
        mapped.setContainerType(target.getContainerType());
        mapped.setMetadata(target.getMetadata());
        return mapped;
    }

    private ImportAttachmentModel mapAttachment(ImportAttachmentDto dto) {
        var mappedDto = new ImportAttachmentDto();
        mappedDto.setFileRef(ImportSupport.normalizeFileRef(dto.getFileRef()));
        mappedDto.setMimeType(dto.getMimeType());
        mappedDto.setSizeBytes(dto.getSizeBytes() == null ? 0L : dto.getSizeBytes());
        mappedDto.setChecksum(dto.getChecksum());
        mappedDto.setRequired(Boolean.TRUE.equals(dto.getRequired()));

        var model = new ImportAttachmentModel();
        model.setAttachment(mappedDto);
        model.setState(ImportAttachmentState.PENDING_UPLOAD);
        return model;
    }

    private ImportJobCreateResponseDto toCreateResponse(ImportJobModel job) {
        var response = new ImportJobCreateResponseDto();
        response.setJobId(job.getJobId());
        response.setStatus(job.getStatus().name());
        response.setSubmittedAt(job.getSubmittedAt());
        response.setPollAfterMs(job.getPollAfterMs());
        response.setAttachments(toAttachmentStatus(job));

        var links = new HashMap<String, String>();
        links.put("status", "/content-import/jobs/" + job.getJobId());
        links.put("cancel", "/content-import/jobs/" + job.getJobId() + "/cancel");
        links.put("uploadAttachment", "/content-import/jobs/" + job.getJobId() + "/attachments");
        response.setLinks(links);

        return response;
    }

    private ImportJobStatusDto toStatusResponse(ImportJobModel job) {
        var response = new ImportJobStatusDto();
        response.setJobId(job.getJobId());
        response.setStatus(job.getStatus().name());
        response.setCancellable(job.getStatus() == ImportJobState.QUEUED
                || job.getStatus() == ImportJobState.VALIDATING
                || job.getStatus() == ImportJobState.RUNNING);
        response.setCancelRequested(job.isCancelRequested());
        response.setSubmittedAt(job.getSubmittedAt());
        response.setStartedAt(job.getStartedAt());
        response.setUpdatedAt(job.getUpdatedAt());
        response.setCompletedAt(job.getCompletedAt());
        response.setStage(job.getExecutionState().getStage().name());
        response.setProgress(toProgress(job));
        response.setAttachments(toAttachmentStatus(job));
        response.setResult(toResult(job));
        response.setWarnings(job.getWarnings().stream().map(this::toIssue).toList());
        response.setErrors(job.getErrors().stream().map(this::toIssue).toList());
        response.setPollAfterMs(job.getPollAfterMs());
        return response;
    }

    private ImportJobProgressDto toProgress(ImportJobModel job) {
        var progress = new ImportJobProgressDto();
        progress.setTotalRecords(job.getExecutionState().getTotalRecords());
        progress.setProcessedRecords(job.getExecutionState().getProcessedRecords());
        progress.setSuccessfulRecords(job.getExecutionState().getSuccessfulRecords());
        progress.setFailedRecords(job.getExecutionState().getFailedRecords());
        progress.setPercent(round2(job.getExecutionState().getPercent()));
        return progress;
    }

    private ImportAttachmentStatusDto toAttachmentStatus(ImportJobModel job) {
        var status = new ImportAttachmentStatusDto();

        var attachments = job.getAttachments().values();
        status.setDeclared(attachments.size());

        int ready = 0;
        int uploading = 0;
        int pending = 0;
        int failed = 0;
        var missingRequired = new ArrayList<String>();

        for (var attachment : attachments) {
            if (attachment.getState() == ImportAttachmentState.READY) {
                ready++;
            } else if (attachment.getState() == ImportAttachmentState.UPLOADING) {
                uploading++;
            } else if (attachment.getState() == ImportAttachmentState.FAILED) {
                failed++;
            } else {
                pending++;
            }

            if (attachment.isRequired() && attachment.getState() != ImportAttachmentState.READY) {
                missingRequired.add(attachment.getFileRef());
            }
        }

        status.setReady(ready);
        status.setUploading(uploading);
        status.setPending(pending);
        status.setFailed(failed);
        status.setPercent(status.getDeclared() == 0 ? 100.0 : round2(((double) ready / (double) status.getDeclared()) * 100.0));
        status.setMissingRequiredFileRefs(missingRequired);
        return status;
    }

    private ImportJobResultDto toResult(ImportJobModel job) {
        if (job.getResult() == null) {
            return null;
        }

        var result = new ImportJobResultDto();
        result.setCreatedRootType(job.getResult().getCreatedRootType());
        result.setCreatedRootId(job.getResult().getCreatedRootId());
        result.setSummary(job.getResult().getSummary());
        return result;
    }

    private ImportIssueDto toIssue(ImportIssueDto issueModel) {
        var issue = new ImportIssueDto();
        issue.setCode(issueModel.getCode());
        issue.setMessage(issueModel.getMessage());
        issue.setRecordId(issueModel.getRecordId());
        issue.setContentId(issueModel.getContentId());
        return issue;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

