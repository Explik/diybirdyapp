package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.persistence.vertex.VideoContentVertex;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VideoUploadImportContentHandler implements ImportContentHandler {
    @Override
    public String getSupportedContentType() {
        return "video-upload";
    }

    @Override
    public VideoContentVertex createContent(ImportContentDto content, ImportProcessingContext context) {
        var payload = content.getPayload();
        var fileRef = ImportSupport.requireString(payload, "fileRef");
        var languageId = ImportSupport.readString(payload, "languageId");

        context.requireDeclaredAttachment(fileRef);

        var vertex = VideoContentVertex.create(context.getTraversalSource());
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(ImportSupport.extractFileName(fileRef));

        if (languageId != null && !languageId.isBlank()) {
            vertex.setLanguage(context.findLanguageById(languageId));
        }

        context.trackCreatedContentId(vertex.getId());
        return vertex;
    }
}

