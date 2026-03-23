package com.explik.diybirdyapp.controller.model.imports;

import java.util.HashMap;
import java.util.Map;

public class ImportConceptDto {
    private String conceptId;
    private String conceptType;
    private Map<String, Object> payload = new HashMap<>();

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptType() {
        return conceptType;
    }

    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
