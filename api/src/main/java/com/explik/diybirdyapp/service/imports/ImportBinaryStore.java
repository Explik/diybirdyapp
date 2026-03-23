package com.explik.diybirdyapp.service.imports;

public interface ImportBinaryStore {
    void writeChunk(String jobId, String fileRef, int chunkIndex, byte[] data);

    boolean chunkExists(String jobId, String fileRef, int chunkIndex);

    byte[] readChunk(String jobId, String fileRef, int chunkIndex);

    String assembleChunks(String jobId, String fileRef, int totalChunks);

    byte[] readAssembled(String jobId, String fileRef);
}
