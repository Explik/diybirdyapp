package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;

public interface ImportContentHandler {
    String getSupportedContentType();

    ContentVertex createContent(ImportContentDto content, ImportProcessingContext context);
}

