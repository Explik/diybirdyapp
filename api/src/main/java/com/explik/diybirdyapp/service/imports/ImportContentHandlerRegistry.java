package com.explik.diybirdyapp.service.imports;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ImportContentHandlerRegistry {
    private final Map<String, ImportContentHandler> handlersByType;

    public ImportContentHandlerRegistry(List<ImportContentHandler> handlers) {
        this.handlersByType = handlers.stream()
                .collect(Collectors.toMap(ImportContentHandler::getSupportedContentType, Function.identity()));
    }

    public ImportContentHandler getRequired(String contentType) {
        var handler = handlersByType.get(contentType);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported contentType: " + contentType);
        }

        return handler;
    }

    public boolean supports(String contentType) {
        return handlersByType.containsKey(contentType);
    }

    public List<String> supportedTypes() {
        return handlersByType.keySet().stream().sorted().toList();
    }
}
