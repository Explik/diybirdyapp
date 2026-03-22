package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TextImportContentHandler implements ImportContentHandler {
    @Override
    public String getSupportedContentType() {
        return "text";
    }

    @Override
    public TextContentVertex createContent(ImportContentDto content, ImportProcessingContext context) {
        var payload = content.getPayload();
        var text = ImportSupport.readString(payload, "text");
        var languageId = ImportSupport.requireString(payload, "languageId");

        var languageVertex = context.findLanguageById(languageId);

        var vertex = TextContentVertex.create(context.getTraversalSource());
        vertex.setId(UUID.randomUUID().toString());
        vertex.setValue(text == null ? "" : text);
        vertex.setLanguage(languageVertex);

        context.trackCreatedContentId(vertex.getId());
        return vertex;
    }
}

