package com.explik.diybirdyapp.model.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentDto;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImportAttachmentModel {
    private ImportAttachmentDto attachment = new ImportAttachmentDto();
    private ImportAttachmentState state = ImportAttachmentState.PENDING_UPLOAD;
    private Integer totalChunks;
    private String storageKey;
    private Instant updatedAt = Instant.now();
    private final Map<Integer, ImportAttachmentChunkModel> chunks = new ConcurrentHashMap<>();

    public ImportAttachmentDto getAttachment() {
        return attachment;
    }

    public void setAttachment(ImportAttachmentDto attachment) {
        this.attachment = attachment == null ? new ImportAttachmentDto() : attachment;
    }

    public String getFileRef() {
        return attachment.getFileRef();
    }

    public void setFileRef(String fileRef) {
        attachment.setFileRef(fileRef);
    }

    public String getMimeType() {
        return attachment.getMimeType();
    }

    public void setMimeType(String mimeType) {
        attachment.setMimeType(mimeType);
    }

    public long getSizeBytes() {
        return attachment.getSizeBytes() == null ? 0L : attachment.getSizeBytes();
    }

    public void setSizeBytes(long sizeBytes) {
        attachment.setSizeBytes(sizeBytes);
    }

    public String getChecksum() {
        return attachment.getChecksum();
    }

    public void setChecksum(String checksum) {
        attachment.setChecksum(checksum);
    }

    public boolean isRequired() {
        return Boolean.TRUE.equals(attachment.getRequired());
    }

    public void setRequired(boolean required) {
        attachment.setRequired(required);
    }

    public ImportAttachmentState getState() {
        return state;
    }

    public void setState(ImportAttachmentState state) {
        this.state = state;
        this.updatedAt = Instant.now();
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<Integer, ImportAttachmentChunkModel> getChunks() {
        return chunks;
    }

    public int getReceivedChunks() {
        return chunks.size();
    }

    public boolean isComplete() {
        return totalChunks != null && totalChunks > 0 && chunks.size() == totalChunks;
    }
}