package com.explik.diybirdyapp.controller.model.imports;

import java.util.ArrayList;
import java.util.List;

public class ImportAttachmentStatusDto {
    private int declared;
    private int ready;
    private int uploading;
    private int pending;
    private int failed;
    private double percent;
    private List<String> missingRequiredFileRefs = new ArrayList<>();

    public int getDeclared() {
        return declared;
    }

    public void setDeclared(int declared) {
        this.declared = declared;
    }

    public int getReady() {
        return ready;
    }

    public void setReady(int ready) {
        this.ready = ready;
    }

    public int getUploading() {
        return uploading;
    }

    public void setUploading(int uploading) {
        this.uploading = uploading;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public List<String> getMissingRequiredFileRefs() {
        return missingRequiredFileRefs;
    }

    public void setMissingRequiredFileRefs(List<String> missingRequiredFileRefs) {
        this.missingRequiredFileRefs = missingRequiredFileRefs;
    }
}
