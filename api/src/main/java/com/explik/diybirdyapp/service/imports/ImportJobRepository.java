package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportJobModel;

import java.util.Optional;

public interface ImportJobRepository {
    ImportJobModel save(ImportJobModel job);

    Optional<ImportJobModel> findById(String jobId);

    Optional<ImportJobModel> findByUserAndClientRequestId(String userId, String clientRequestId);
}
