package com.explik.diybirdyapp.model.imports;

public class ImportExecutionState {
    private ImportStage stage = ImportStage.VALIDATE_REQUEST;
    private int totalRecords;
    private int processedRecords;
    private int successfulRecords;
    private int failedRecords;

    public ImportStage getStage() {
        return stage;
    }

    public void setStage(ImportStage stage) {
        this.stage = stage;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getProcessedRecords() {
        return processedRecords;
    }

    public void setProcessedRecords(int processedRecords) {
        this.processedRecords = processedRecords;
    }

    public int getSuccessfulRecords() {
        return successfulRecords;
    }

    public void setSuccessfulRecords(int successfulRecords) {
        this.successfulRecords = successfulRecords;
    }

    public int getFailedRecords() {
        return failedRecords;
    }

    public void setFailedRecords(int failedRecords) {
        this.failedRecords = failedRecords;
    }

    public double getPercent() {
        if (totalRecords <= 0) {
            return 0.0;
        }

        return Math.min(100.0, ((double) processedRecords / (double) totalRecords) * 100.0);
    }
}