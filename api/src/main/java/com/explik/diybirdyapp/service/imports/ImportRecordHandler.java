package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;

public interface ImportRecordHandler {
    String getSupportedRecordType();

    void importRecord(ImportRecordDto record, ImportProcessingContext context);
}

