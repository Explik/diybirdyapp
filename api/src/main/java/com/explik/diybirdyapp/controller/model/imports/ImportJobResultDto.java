package com.explik.diybirdyapp.controller.model.imports;

import java.util.HashMap;
import java.util.Map;

public class ImportJobResultDto {
    private String createdRootType;
    private String createdRootId;
    private Map<String, Object> summary = new HashMap<>();

    public String getCreatedRootType() {
        return createdRootType;
    }

    public void setCreatedRootType(String createdRootType) {
        this.createdRootType = createdRootType;
    }

    public String getCreatedRootId() {
        return createdRootId;
    }

    public void setCreatedRootId(String createdRootId) {
        this.createdRootId = createdRootId;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }
}
