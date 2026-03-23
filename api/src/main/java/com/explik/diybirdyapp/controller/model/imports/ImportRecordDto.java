package com.explik.diybirdyapp.controller.model.imports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportRecordDto {
    private String recordType;
    private String recordId;
    private Map<String, Object> attributes = new HashMap<>();
    private List<ImportRecordSlotDto> slots = new ArrayList<>();

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<ImportRecordSlotDto> getSlots() {
        return slots;
    }

    public void setSlots(List<ImportRecordSlotDto> slots) {
        this.slots = slots;
    }
}
