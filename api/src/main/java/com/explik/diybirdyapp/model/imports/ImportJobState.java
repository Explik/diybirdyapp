package com.explik.diybirdyapp.model.imports;

public enum ImportJobState {
    QUEUED,
    VALIDATING,
    RUNNING,
    CANCELLING,
    CANCELLED,
    COMPLETED,
    FAILED
}