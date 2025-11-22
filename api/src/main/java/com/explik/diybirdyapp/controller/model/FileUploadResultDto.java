package com.explik.diybirdyapp.controller.dto;

import jakarta.validation.constraints.NotNull;

public class FileUploadResultDto {
    @NotNull(message = "url.required")
    private String url;

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}
