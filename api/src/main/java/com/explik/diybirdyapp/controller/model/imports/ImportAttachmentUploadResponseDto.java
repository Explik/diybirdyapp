package com.explik.diybirdyapp.controller.model.imports;

public class ImportAttachmentUploadResponseDto {
    private String jobId;
    private String fileRef;
    private String attachmentStatus;
    private Integer receivedChunks;
    private Integer totalChunks;
    private Integer pollAfterMs;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFileRef() {
        return fileRef;
    }

    public void setFileRef(String fileRef) {
        this.fileRef = fileRef;
    }

    public String getAttachmentStatus() {
        return attachmentStatus;
    }

    public void setAttachmentStatus(String attachmentStatus) {
        this.attachmentStatus = attachmentStatus;
    }

    public Integer getReceivedChunks() {
        return receivedChunks;
    }

    public void setReceivedChunks(Integer receivedChunks) {
        this.receivedChunks = receivedChunks;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public Integer getPollAfterMs() {
        return pollAfterMs;
    }

    public void setPollAfterMs(Integer pollAfterMs) {
        this.pollAfterMs = pollAfterMs;
    }
}
