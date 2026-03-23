package com.explik.diybirdyapp.service.imports;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileSystemImportBinaryStore implements ImportBinaryStore {
    private final Path storageRoot;

    public FileSystemImportBinaryStore(@Value("${content-import.storage-path:uploads/content-import}") String storageRoot) {
        this.storageRoot = Paths.get(storageRoot);

        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to initialize import binary store", ex);
        }
    }

    @Override
    public void writeChunk(String jobId, String fileRef, int chunkIndex, byte[] data) {
        var path = chunkPath(jobId, fileRef, chunkIndex);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, data);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write chunk", ex);
        }
    }

    @Override
    public boolean chunkExists(String jobId, String fileRef, int chunkIndex) {
        return Files.exists(chunkPath(jobId, fileRef, chunkIndex));
    }

    @Override
    public byte[] readChunk(String jobId, String fileRef, int chunkIndex) {
        var path = chunkPath(jobId, fileRef, chunkIndex);
        if (!Files.exists(path)) {
            return null;
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read chunk", ex);
        }
    }

    @Override
    public String assembleChunks(String jobId, String fileRef, int totalChunks) {
        var assembledPath = assembledPath(jobId, fileRef);

        try {
            Files.createDirectories(assembledPath.getParent());

            var stream = new ByteArrayOutputStream();
            for (int i = 0; i < totalChunks; i++) {
                var chunkPath = chunkPath(jobId, fileRef, i);
                if (!Files.exists(chunkPath)) {
                    throw new IllegalArgumentException("Missing chunk " + i + " for fileRef " + fileRef);
                }

                stream.write(Files.readAllBytes(chunkPath));
            }

            Files.write(assembledPath, stream.toByteArray());
            return assembledPath.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to assemble chunks", ex);
        }
    }

    @Override
    public byte[] readAssembled(String jobId, String fileRef) {
        var path = assembledPath(jobId, fileRef);
        if (!Files.exists(path)) {
            return null;
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read assembled file", ex);
        }
    }

    private Path chunkPath(String jobId, String fileRef, int chunkIndex) {
        var safeFileName = ImportSupport.toChunkSafeName(fileRef) + ".chunk." + chunkIndex;
        return storageRoot.resolve(jobId).resolve("chunks").resolve(safeFileName);
    }

    private Path assembledPath(String jobId, String fileRef) {
        var safeFileName = ImportSupport.toChunkSafeName(fileRef) + ".assembled";
        return storageRoot.resolve(jobId).resolve("assembled").resolve(safeFileName);
    }
}
