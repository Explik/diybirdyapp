package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.model.imports.ImportManifestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportStoresUnitTest {
    @TempDir
    Path tempDir;

    @Test
    void givenSavedJob_whenFindById_thenReturnsJob() {
        var repository = new InMemoryImportJobRepository();
        var job = new ImportJobModel();
        job.setJobId("job-1");

        repository.save(job);

        var found = repository.findById("job-1");
        assertTrue(found.isPresent());
        assertEquals("job-1", found.get().getJobId());
    }

    @Test
    void givenClientRequestMapping_whenFindByUserAndClientRequestId_thenReturnsJob() {
        var repository = new InMemoryImportJobRepository();
        var job = new ImportJobModel();
        job.setJobId("job-2");
        job.setUserId("user-1");
        job.setClientRequestId("client-1");

        repository.save(job);

        var found = repository.findByUserAndClientRequestId("user-1", "client-1");
        assertTrue(found.isPresent());
        assertEquals("job-2", found.get().getJobId());
    }

    @Test
    void givenPayloadStore_whenPutAndGet_thenReturnsManifest() {
        var store = new InMemoryImportPayloadStore();
        var manifest = new ImportManifestModel();
        manifest.setSchemaVersion("1.0");

        var key = store.put(manifest);

        assertNotNull(key);
        assertEquals(manifest, store.get(key));
        assertNull(store.get("unknown-key"));
    }

    @Test
    void givenChunks_whenAssemble_thenReturnsConcatenatedContent() {
        var store = new FileSystemImportBinaryStore(tempDir.toString());

        store.writeChunk("job-1", "media/file.bin", 0, "hello ".getBytes());
        store.writeChunk("job-1", "media/file.bin", 1, "world".getBytes());

        var assembledPath = store.assembleChunks("job-1", "media/file.bin", 2);
        var assembled = store.readAssembled("job-1", "media/file.bin");

        assertNotNull(assembledPath);
        assertArrayEquals("hello world".getBytes(), assembled);
    }

    @Test
    void givenMissingChunk_whenAssemble_thenThrows() {
        var store = new FileSystemImportBinaryStore(tempDir.toString());
        store.writeChunk("job-2", "media/file.bin", 0, "part1".getBytes());

        var exception = assertThrows(IllegalArgumentException.class,
                () -> store.assembleChunks("job-2", "media/file.bin", 2));

        assertEquals("Missing chunk 1 for fileRef media/file.bin", exception.getMessage());
    }

    @Test
    void givenFileRefWithTraversal_whenWriteChunk_thenThrows() {
        var store = new FileSystemImportBinaryStore(tempDir.toString());

        assertThrows(IllegalArgumentException.class,
                () -> store.writeChunk("job-3", "../unsafe.bin", 0, new byte[] {1}));
    }
}
