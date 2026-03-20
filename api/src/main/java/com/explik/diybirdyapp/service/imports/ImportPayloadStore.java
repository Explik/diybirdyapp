package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportManifestModel;

public interface ImportPayloadStore {
    String put(ImportManifestModel manifestModel);

    ImportManifestModel get(String key);
}
