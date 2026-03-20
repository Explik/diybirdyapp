package com.explik.diybirdyapp.controller.model.imports;

import java.util.HashMap;
import java.util.Map;

public class ImportTargetDto {
    private String containerType;
    private Map<String, Object> metadata = new HashMap<>();

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
