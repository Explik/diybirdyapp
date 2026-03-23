package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AudioUploadImportContentHandler implements ImportContentHandler {
    @Override
    public String getSupportedContentType() {
        return "audio-upload";
    }

    @Override
    public AudioContentVertex createContent(ImportContentDto content, ImportProcessingContext context) {
        var payload = content.getPayload();
        var fileRef = ImportSupport.requireString(payload, "fileRef");
        var languageId = ImportSupport.readString(payload, "languageId");

        context.requireDeclaredAttachment(fileRef);

        var vertex = AudioContentVertex.create(context.getTraversalSource());
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(ImportSupport.extractFileName(fileRef));

        if (languageId != null && !languageId.isBlank()) {
            vertex.setLanguage(context.findLanguageById(languageId));
        }

        context.trackCreatedContentId(vertex.getId());
        return vertex;
    }
}

