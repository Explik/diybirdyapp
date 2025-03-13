package com.explik.diybirdyapp.controller.dto;

import jakarta.validation.constraints.NotNull;

public class FileUploadResultDto {
    @NotNull
    private String url;

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}
