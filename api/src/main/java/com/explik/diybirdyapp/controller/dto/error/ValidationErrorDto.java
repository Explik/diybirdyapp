package com.explik.diybirdyapp.controller.dto.error;

import java.util.Map;

public class ValidationErrorDto {
    private String type = "validation-error";
    private Map<String, String> fields;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
