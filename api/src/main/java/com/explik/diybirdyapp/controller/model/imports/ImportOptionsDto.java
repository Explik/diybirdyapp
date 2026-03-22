package com.explik.diybirdyapp.controller.model.imports;

public class ImportOptionsDto {
    private boolean dryRun;
    private String onError;
    private String onCancel;

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public String getOnError() {
        return onError;
    }

    public void setOnError(String onError) {
        this.onError = onError;
    }

    public String getOnCancel() {
        return onCancel;
    }

    public void setOnCancel(String onCancel) {
        this.onCancel = onCancel;
    }
}
