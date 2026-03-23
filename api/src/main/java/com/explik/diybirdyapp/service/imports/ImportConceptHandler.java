package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportConceptDto;

public interface ImportConceptHandler {
    String getSupportedConceptType();

    void applyConcept(ImportConceptDto concept, ImportProcessingContext context);
}

