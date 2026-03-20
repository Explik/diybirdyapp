package com.explik.diybirdyapp.controller.model.imports;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ImportJobCreateRequestDto {
    @NotBlank(message = "schemaVersion.required")
    private String schemaVersion;

    private String clientRequestId;

    @NotBlank(message = "importType.required")
    private String importType;

    private ImportSourceDto source;

    private ImportTargetDto target;

    @NotNull(message = "contentSet.required")
    private ImportContentSetDto contentSet;

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

    public ImportContentSetDto getContentSet() {
        return contentSet;
    }

    public void setContentSet(ImportContentSetDto contentSet) {
        this.contentSet = contentSet;
    }

    public ImportOptionsDto getOptions() {
        return options;
    }

    public void setOptions(ImportOptionsDto options) {
        this.options = options;
    }
}
