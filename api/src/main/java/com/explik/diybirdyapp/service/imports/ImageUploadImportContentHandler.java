package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.persistence.vertex.ImageContentVertex;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ImageUploadImportContentHandler implements ImportContentHandler {
    @Override
    public String getSupportedContentType() {
        return "image-upload";
    }

    @Override
    public ImageContentVertex createContent(ImportContentDto content, ImportProcessingContext context) {
        var payload = content.getPayload();
        var fileRef = ImportSupport.requireString(payload, "fileRef");

        context.requireDeclaredAttachment(fileRef);

        var vertex = ImageContentVertex.create(context.getTraversalSource());
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(ImportSupport.extractFileName(fileRef));

        context.trackCreatedContentId(vertex.getId());
        return vertex;
    }
}

