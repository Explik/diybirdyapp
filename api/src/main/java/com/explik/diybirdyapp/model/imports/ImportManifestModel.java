package com.explik.diybirdyapp.model.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportOptionsDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;
import com.explik.diybirdyapp.controller.model.imports.ImportSourceDto;
import com.explik.diybirdyapp.controller.model.imports.ImportTargetDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportManifestModel {
    private String schemaVersion;
    private String clientRequestId;
    private String importType;
    private ImportSourceDto source;
    private ImportTargetDto target;
    private String setType;
    private String setId;
    private Map<String, Object> setMetadata = new HashMap<>();
    private List<ImportRecordDto> records = new ArrayList<>();
    private List<ImportContentDto> contents = new ArrayList<>();
    private List<ImportConceptDto> concepts = new ArrayList<>();
    private List<ImportAttachmentModel> attachments = new ArrayList<>();
    private ImportOptionsDto options;

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public ImportSourceDto getSource() {
        return source;
    }

    public void setSource(ImportSourceDto source) {
        this.source = source;
    }

    public ImportTargetDto getTarget() {
        return target;
    }

    public void setTarget(ImportTargetDto target) {
        this.target = target;
    }

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

    public Map<String, Object> getSetMetadata() {
        return setMetadata;
    }

    public void setSetMetadata(Map<String, Object> setMetadata) {
        this.setMetadata = setMetadata;
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

    public List<ImportAttachmentModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ImportAttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public ImportOptionsDto getOptions() {
        return options;
    }

    public void setOptions(ImportOptionsDto options) {
        this.options = options;
    }
}
