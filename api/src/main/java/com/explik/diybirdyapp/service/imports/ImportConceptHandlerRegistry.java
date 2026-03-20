package com.explik.diybirdyapp.service.imports;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ImportConceptHandlerRegistry {
    private final Map<String, ImportConceptHandler> handlersByType;

    public ImportConceptHandlerRegistry(List<ImportConceptHandler> handlers) {
        this.handlersByType = handlers.stream()
                .collect(Collectors.toMap(ImportConceptHandler::getSupportedConceptType, Function.identity()));
    }

    public ImportConceptHandler getRequired(String conceptType) {
        var handler = handlersByType.get(conceptType);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported conceptType: " + conceptType);
        }

        return handler;
    }

    public boolean supports(String conceptType) {
        return handlersByType.containsKey(conceptType);
    }

    public List<String> supportedTypes() {
        return handlersByType.keySet().stream().sorted().toList();
    }
}
