package com.explik.diybirdyapp.model.imports;

public enum ImportStage {
    VALIDATE_REQUEST,
    REGISTER_ATTACHMENTS,
    CREATE_TARGET_CONTAINER,
    IMPORT_RECORDS,
    WAIT_FOR_REQUIRED_MEDIA,
    FINALIZE,
    ROLLBACK
}