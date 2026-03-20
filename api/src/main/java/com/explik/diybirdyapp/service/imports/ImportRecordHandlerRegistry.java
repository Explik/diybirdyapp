package com.explik.diybirdyapp.service.imports;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ImportRecordHandlerRegistry {
    private final Map<String, ImportRecordHandler> handlersByType;

    public ImportRecordHandlerRegistry(List<ImportRecordHandler> handlers) {
        this.handlersByType = handlers.stream()
                .collect(Collectors.toMap(ImportRecordHandler::getSupportedRecordType, Function.identity()));
    }

    public ImportRecordHandler getRequired(String recordType) {
        var handler = handlersByType.get(recordType);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported recordType: " + recordType);
        }

        return handler;
    }

    public boolean supports(String recordType) {
        return handlersByType.containsKey(recordType);
    }

    public List<String> supportedTypes() {
        return handlersByType.keySet().stream().sorted().toList();
    }
}
