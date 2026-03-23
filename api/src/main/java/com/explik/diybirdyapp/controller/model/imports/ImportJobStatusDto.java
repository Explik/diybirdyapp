package com.explik.diybirdyapp.controller.model.imports;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ImportJobStatusDto {
    private String jobId;
    private String status;
    private boolean cancellable;
    private boolean cancelRequested;
    private Instant submittedAt;
    private Instant startedAt;
    private Instant updatedAt;
    private Instant completedAt;
    private String stage;
    private ImportJobProgressDto progress;
    private ImportAttachmentStatusDto attachments;
    private ImportJobResultDto result;
    private List<ImportIssueDto> warnings = new ArrayList<>();
    private List<ImportIssueDto> errors = new ArrayList<>();
    private int pollAfterMs;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public boolean isCancelRequested() {
        return cancelRequested;
    }

    public void setCancelRequested(boolean cancelRequested) {
        this.cancelRequested = cancelRequested;
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
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public ImportJobProgressDto getProgress() {
        return progress;
    }

    public void setProgress(ImportJobProgressDto progress) {
        this.progress = progress;
    }

    public ImportAttachmentStatusDto getAttachments() {
        return attachments;
    }

    public void setAttachments(ImportAttachmentStatusDto attachments) {
        this.attachments = attachments;
    }

    public ImportJobResultDto getResult() {
        return result;
    }

    public void setResult(ImportJobResultDto result) {
        this.result = result;
    }

    public List<ImportIssueDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ImportIssueDto> warnings) {
        this.warnings = warnings;
    }

    public List<ImportIssueDto> getErrors() {
        return errors;
    }

    public void setErrors(List<ImportIssueDto> errors) {
        this.errors = errors;
    }

    public int getPollAfterMs() {
        return pollAfterMs;
    }

    public void setPollAfterMs(int pollAfterMs) {
        this.pollAfterMs = pollAfterMs;
    }
}
