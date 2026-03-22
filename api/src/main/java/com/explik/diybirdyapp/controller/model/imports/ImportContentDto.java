package com.explik.diybirdyapp.controller.model.imports;

import java.util.HashMap;
import java.util.Map;

public class ImportContentDto {
    private String contentId;
    private String contentType;
    private Map<String, Object> payload = new HashMap<>();

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
