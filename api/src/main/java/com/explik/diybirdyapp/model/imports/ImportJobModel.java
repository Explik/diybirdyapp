package com.explik.diybirdyapp.model.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportIssueDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImportJobModel {
    private String jobId;
    private String userId;
    private String clientRequestId;
    private String payloadHash;
    private String manifestKey;
    private ImportJobState status = ImportJobState.QUEUED;
    private boolean cancelRequested;
    private Instant submittedAt = Instant.now();
    private Instant startedAt;
    private Instant updatedAt = Instant.now();
    private Instant completedAt;
    private ImportExecutionState executionState = new ImportExecutionState();
    private ImportResultModel result;
    private int pollAfterMs = 1500;
    private final Map<String, ImportAttachmentModel> attachments = new ConcurrentHashMap<>();
    private final List<ImportIssueDto> warnings = new ArrayList<>();
    private final List<ImportIssueDto> errors = new ArrayList<>();

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }

    public String getPayloadHash() {
        return payloadHash;
    }

    public void setPayloadHash(String payloadHash) {
        this.payloadHash = payloadHash;
    }

    public String getManifestKey() {
        return manifestKey;
    }

    public void setManifestKey(String manifestKey) {
        this.manifestKey = manifestKey;
    }

    public ImportJobState getStatus() {
        return status;
    }

    public void setStatus(ImportJobState status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public boolean isCancelRequested() {
        return cancelRequested;
    }

    public void setCancelRequested(boolean cancelRequested) {
        this.cancelRequested = cancelRequested;
        this.updatedAt = Instant.now();
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
        this.updatedAt = Instant.now();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
        this.updatedAt = Instant.now();
    }

    public ImportExecutionState getExecutionState() {
        return executionState;
    }

    public void setExecutionState(ImportExecutionState executionState) {
        this.executionState = executionState;
    }

    public ImportResultModel getResult() {
        return result;
    }

    public void setResult(ImportResultModel result) {
        this.result = result;
        this.updatedAt = Instant.now();
    }

    public int getPollAfterMs() {
        return pollAfterMs;
    }

    public void setPollAfterMs(int pollAfterMs) {
        this.pollAfterMs = pollAfterMs;
    }

    public Map<String, ImportAttachmentModel> getAttachments() {
        return attachments;
    }

    public List<ImportIssueDto> getWarnings() {
        return warnings;
    }

    public List<ImportIssueDto> getErrors() {
        return errors;
    }

    public boolean isTerminal() {
        return status == ImportJobState.COMPLETED || status == ImportJobState.FAILED || status == ImportJobState.CANCELLED;
    }

    public boolean isUploadAllowed() {
        return status == ImportJobState.QUEUED || status == ImportJobState.VALIDATING || status == ImportJobState.RUNNING;
    }
}
