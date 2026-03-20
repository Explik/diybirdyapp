package com.explik.diybirdyapp.controller.model.imports;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ImportJobCreateResponseDto {
    private String jobId;
    private String status;
    private Instant submittedAt;
    private int pollAfterMs;
    private ImportAttachmentStatusDto attachments;
    private Map<String, String> links = new HashMap<>();

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

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public int getPollAfterMs() {
        return pollAfterMs;
    }

    public void setPollAfterMs(int pollAfterMs) {
        this.pollAfterMs = pollAfterMs;
    }

    public ImportAttachmentStatusDto getAttachments() {
        return attachments;
    }

    public void setAttachments(ImportAttachmentStatusDto attachments) {
        this.attachments = attachments;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
