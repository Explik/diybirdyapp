package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportManifestModel;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryImportPayloadStore implements ImportPayloadStore {
    private final ConcurrentMap<String, ImportManifestModel> payloads = new ConcurrentHashMap<>();

    @Override
    public String put(ImportManifestModel manifestModel) {
        var key = "imp_manifest_" + UUID.randomUUID().toString().replace("-", "");
        payloads.put(key, manifestModel);
        return key;
    }

    @Override
    public ImportManifestModel get(String key) {
        return payloads.get(key);
    }
}
