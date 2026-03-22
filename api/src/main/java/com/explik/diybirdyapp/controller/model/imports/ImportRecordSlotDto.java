package com.explik.diybirdyapp.controller.model.imports;

import java.util.ArrayList;
import java.util.List;

public class ImportRecordSlotDto {
    private String slotKey;
    private String contentRef;
    private List<String> conceptRefs = new ArrayList<>();

    public String getSlotKey() {
        return slotKey;
    }

    public void setSlotKey(String slotKey) {
        this.slotKey = slotKey;
    }

    public String getContentRef() {
        return contentRef;
    }

    public void setContentRef(String contentRef) {
        this.contentRef = contentRef;
    }

    public List<String> getConceptRefs() {
        return conceptRefs;
    }

    public void setConceptRefs(List<String> conceptRefs) {
        this.conceptRefs = conceptRefs;
    }
}
