package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.model.imports.ImportAttachmentModel;
import com.explik.diybirdyapp.model.imports.ImportAttachmentState;
import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ContentImportAttachmentServiceUnitTest {
    @TempDir
    Path tempDir;

    @Test
    void givenSingleChunk_whenReceiveChunk_thenAttachmentBecomesReady() {
        var binaryStore = new FileSystemImportBinaryStore(tempDir.toString());
        var storageService = mock(BinaryStorageService.class);
        var service = new ContentImportAttachmentService(binaryStore, storageService, 1024 * 1024);

        var job = createJob("job-1");
        var attachment = createAttachment("media/sound.mp3");
        var bytes = "hello world".getBytes();
        var finalChecksum = ImportSupport.sha256WithPrefix(bytes);
        MultipartFile chunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", bytes);

        var response = service.receiveChunk(job, attachment, chunk, 0, 1, finalChecksum, finalChecksum);

        assertEquals("READY", response.getAttachmentStatus());
        assertEquals(1, response.getReceivedChunks());
        assertEquals(1, response.getTotalChunks());
        assertEquals(ImportAttachmentState.READY, attachment.getState());
        assertNotNull(attachment.getStorageKey());
        verify(storageService).set("sound.mp3", bytes);
    }

    @Test
    void givenInvalidChunkChecksum_whenReceiveChunk_thenThrowsUnprocessableEntity() {
        var binaryStore = new FileSystemImportBinaryStore(tempDir.toString());
        var storageService = mock(BinaryStorageService.class);
        var service = new ContentImportAttachmentService(binaryStore, storageService, 1024 * 1024);

        var job = createJob("job-2");
        var attachment = createAttachment("media/sound.mp3");
        var chunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", "payload".getBytes());

        var exception = assertThrows(ResponseStatusException.class,
                () -> service.receiveChunk(job, attachment, chunk, 0, 1, "sha256:deadbeef", null));

        assertEquals(422, exception.getStatusCode().value());
    }

    @Test
    void givenChunkTooLarge_whenReceiveChunk_thenThrowsPayloadTooLarge() {
        var binaryStore = new FileSystemImportBinaryStore(tempDir.toString());
        var storageService = mock(BinaryStorageService.class);
        var service = new ContentImportAttachmentService(binaryStore, storageService, 2);

        var job = createJob("job-3");
        var attachment = createAttachment("media/sound.mp3");
        var chunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", "123".getBytes());

        var exception = assertThrows(ResponseStatusException.class,
                () -> service.receiveChunk(job, attachment, chunk, 0, 1, null, null));

        assertEquals(413, exception.getStatusCode().value());
    }

    @Test
    void givenSameChunkUploadedTwice_whenReceiveChunk_thenIsIdempotent() {
        var binaryStore = new FileSystemImportBinaryStore(tempDir.toString());
        var storageService = mock(BinaryStorageService.class);
        var service = new ContentImportAttachmentService(binaryStore, storageService, 1024 * 1024);

        var job = createJob("job-4");
        var attachment = createAttachment("media/sound.mp3");
        var chunkBytes = "part-a".getBytes();
        var chunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", chunkBytes);

        var firstResponse = service.receiveChunk(job, attachment, chunk, 0, 2, null, null);
        var secondResponse = service.receiveChunk(job, attachment, chunk, 0, 2, null, null);

        assertEquals("UPLOADING", firstResponse.getAttachmentStatus());
        assertEquals("UPLOADING", secondResponse.getAttachmentStatus());
        assertEquals(1, attachment.getReceivedChunks());

        var lastChunkBytes = "part-b".getBytes();
        var allBytes = "part-apart-b".getBytes();
        var finalChecksum = ImportSupport.sha256WithPrefix(allBytes);
        var lastChunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", lastChunkBytes);

        var finalResponse = service.receiveChunk(job, attachment, lastChunk, 1, 2, null, finalChecksum);
        assertEquals("READY", finalResponse.getAttachmentStatus());
    }

    @Test
    void givenConflictingChunkAtSameIndex_whenReceiveChunk_thenThrowsUnprocessableEntity() {
        var binaryStore = new FileSystemImportBinaryStore(tempDir.toString());
        var storageService = mock(BinaryStorageService.class);
        var service = new ContentImportAttachmentService(binaryStore, storageService, 1024 * 1024);

        var job = createJob("job-5");
        var attachment = createAttachment("media/sound.mp3");

        var firstChunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", "part-a".getBytes());
        service.receiveChunk(job, attachment, firstChunk, 0, 2, null, null);

        var conflictingChunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", "different".getBytes());
        var exception = assertThrows(ResponseStatusException.class,
                () -> service.receiveChunk(job, attachment, conflictingChunk, 0, 2, null, null));

        assertEquals(422, exception.getStatusCode().value());
    }

    @Test
    void givenInconsistentTotalChunks_whenReceiveChunk_thenThrowsUnprocessableEntity() {
        var binaryStore = new FileSystemImportBinaryStore(tempDir.toString());
        var storageService = mock(BinaryStorageService.class);
        var service = new ContentImportAttachmentService(binaryStore, storageService, 1024 * 1024);

        var job = createJob("job-6");
        var attachment = createAttachment("media/sound.mp3");

        var firstChunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", "part-a".getBytes());
        service.receiveChunk(job, attachment, firstChunk, 0, 2, null, null);

        var secondChunk = new MockMultipartFile("chunk", "sound.mp3", "audio/mpeg", "part-b".getBytes());
        var exception = assertThrows(ResponseStatusException.class,
                () -> service.receiveChunk(job, attachment, secondChunk, 1, 3, null, null));

        assertEquals(422, exception.getStatusCode().value());
    }

    private ImportJobModel createJob(String jobId) {
        var job = new ImportJobModel();
        job.setJobId(jobId);
        return job;
    }

    private ImportAttachmentModel createAttachment(String fileRef) {
        var attachment = new ImportAttachmentModel();
        attachment.setFileRef(fileRef);
        attachment.setRequired(true);
        return attachment;
    }
}
