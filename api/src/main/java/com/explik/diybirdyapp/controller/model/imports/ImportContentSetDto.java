package com.explik.diybirdyapp.controller.model.imports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportContentSetDto {
    private String setType;
    private String setId;
    private Map<String, Object> metadata = new HashMap<>();
    private List<ImportRecordDto> records = new ArrayList<>();
    private List<ImportContentDto> contents = new ArrayList<>();
    private List<ImportConceptDto> concepts = new ArrayList<>();
    private List<ImportAttachmentDto> attachments = new ArrayList<>();

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public List<ImportRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<ImportRecordDto> records) {
        this.records = records;
    }

    public List<ImportContentDto> getContents() {
        return contents;
    }

    public void setContents(List<ImportContentDto> contents) {
        this.contents = contents;
    }

    public List<ImportConceptDto> getConcepts() {
        return concepts;
    }

    public void setConcepts(List<ImportConceptDto> concepts) {
        this.concepts = concepts;
    }

    public List<ImportAttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ImportAttachmentDto> attachments) {
        this.attachments = attachments;
    }
}
