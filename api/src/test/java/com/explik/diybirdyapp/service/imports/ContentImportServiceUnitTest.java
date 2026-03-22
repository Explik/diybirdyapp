package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentUploadResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentSetDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateRequestDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordSlotDto;
import com.explik.diybirdyapp.model.imports.ImportAttachmentModel;
import com.explik.diybirdyapp.model.imports.ImportAttachmentState;
import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.model.imports.ImportJobState;
import com.explik.diybirdyapp.model.imports.ImportStage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ContentImportServiceUnitTest {
    @Mock
    private ImportJobRepository importJobRepository;

    @Mock
    private ImportPayloadStore importPayloadStore;

    @Mock
    private ContentImportJobRunner contentImportJobRunner;

    @Mock
    private ContentImportAttachmentService contentImportAttachmentService;

    @Mock
    private ImportRecordHandlerRegistry importRecordHandlerRegistry;

    @Mock
    private ImportContentHandlerRegistry importContentHandlerRegistry;

    @Mock
    private ImportConceptHandlerRegistry importConceptHandlerRegistry;

    private ContentImportService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        when(importRecordHandlerRegistry.supports(anyString())).thenReturn(true);
        when(importContentHandlerRegistry.supports(anyString())).thenReturn(true);
        when(importConceptHandlerRegistry.supports(anyString())).thenReturn(true);

        when(importJobRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(importPayloadStore.put(any())).thenReturn("manifest-key");

        service = new ContentImportService(
                importJobRepository,
                importPayloadStore,
                contentImportJobRunner,
                contentImportAttachmentService,
                importRecordHandlerRegistry,
                importContentHandlerRegistry,
                importConceptHandlerRegistry,
                objectMapper,
                1024 * 1024,
                512 * 1024,
                1024 * 1024 * 5);
    }

    @Test
    void givenValidRequest_whenSubmitJob_thenSavesAndRunsJob() {
        var request = createValidRequest("client-1");
        when(importJobRepository.findByUserAndClientRequestId("user-1", "client-1"))
                .thenReturn(Optional.empty());

        var response = service.submitJob("user-1", request);

        assertNotNull(response.getJobId());
        assertTrue(response.getJobId().startsWith("imp_"));
        assertEquals("QUEUED", response.getStatus());
        assertNotNull(response.getLinks().get("status"));

        var jobCaptor = ArgumentCaptor.forClass(ImportJobModel.class);
        verify(importJobRepository).save(jobCaptor.capture());
        verify(contentImportJobRunner).runJob(jobCaptor.getValue().getJobId());

        assertEquals("user-1", jobCaptor.getValue().getUserId());
        assertEquals("manifest-key", jobCaptor.getValue().getManifestKey());
        assertEquals(ImportStage.VALIDATE_REQUEST, jobCaptor.getValue().getExecutionState().getStage());
    }

    @Test
    void givenDuplicateClientRequestIdWithSamePayload_whenSubmitJob_thenReturnsExistingJob() throws Exception {
        var request = createValidRequest("client-2");
        var payloadHash = ImportSupport.md5Hex(new String(objectMapper.writeValueAsBytes(request), StandardCharsets.UTF_8));

        var existing = new ImportJobModel();
        existing.setJobId("imp_existing");
        existing.setUserId("user-1");
        existing.setClientRequestId("client-2");
        existing.setPayloadHash(payloadHash);

        when(importJobRepository.findByUserAndClientRequestId("user-1", "client-2"))
                .thenReturn(Optional.of(existing));

        var response = service.submitJob("user-1", request);

        assertEquals("imp_existing", response.getJobId());
        verify(importPayloadStore, never()).put(any());
        verify(contentImportJobRunner, never()).runJob(anyString());
    }

    @Test
    void givenDuplicateClientRequestIdWithDifferentPayload_whenSubmitJob_thenThrowsConflict() {
        var request = createValidRequest("client-3");

        var existing = new ImportJobModel();
        existing.setJobId("imp_existing");
        existing.setUserId("user-1");
        existing.setClientRequestId("client-3");
        existing.setPayloadHash("different-hash");

        when(importJobRepository.findByUserAndClientRequestId("user-1", "client-3"))
                .thenReturn(Optional.of(existing));

        var exception = assertThrows(ResponseStatusException.class,
                () -> service.submitJob("user-1", request));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void givenPayloadAboveLimit_whenSubmitJob_thenThrowsPayloadTooLarge() {
        var lowLimitService = new ContentImportService(
                importJobRepository,
                importPayloadStore,
                contentImportJobRunner,
                contentImportAttachmentService,
                importRecordHandlerRegistry,
                importContentHandlerRegistry,
                importConceptHandlerRegistry,
                objectMapper,
                32,
                512 * 1024,
                1024 * 1024 * 5);

        var request = createValidRequest("client-4");

        var exception = assertThrows(ResponseStatusException.class,
                () -> lowLimitService.submitJob("user-1", request));

        assertEquals(413, exception.getStatusCode().value());
    }

    @Test
    void givenUnsupportedRecordType_whenSubmitJob_thenThrowsBadRequest() {
        when(importRecordHandlerRegistry.supports("flashcard")).thenReturn(false);
        var request = createValidRequest(null);

        var exception = assertThrows(ResponseStatusException.class,
                () -> service.submitJob("user-1", request));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void givenUploadRequest_whenUploadAttachment_thenDelegatesToAttachmentService() {
        var job = new ImportJobModel();
        job.setJobId("job-1");
        job.setUserId("user-1");
        job.setStatus(ImportJobState.RUNNING);
        var attachment = new ImportAttachmentModel();
        attachment.setFileRef("media/file.mp3");
        attachment.setState(ImportAttachmentState.PENDING_UPLOAD);
        job.getAttachments().put("media/file.mp3", attachment);

        when(importJobRepository.findById("job-1")).thenReturn(Optional.of(job));

        var attachmentResponse = new ImportAttachmentUploadResponseDto();
        attachmentResponse.setAttachmentStatus("UPLOADING");
        when(contentImportAttachmentService.receiveChunk(eq(job), eq(attachment), any(), eq(0), eq(1), eq(null), eq(null)))
                .thenReturn(attachmentResponse);

        var multipart = new MockMultipartFile("chunk", "file.mp3", "audio/mpeg", "data".getBytes());
        var response = service.uploadAttachment("user-1", "job-1", "media/file.mp3", multipart, 0, 1, null, null);

        assertEquals("UPLOADING", response.getAttachmentStatus());
        verify(importJobRepository, times(1)).save(job);
    }

    @Test
    void givenCompletedJob_whenUploadAttachment_thenThrowsConflict() {
        var job = new ImportJobModel();
        job.setJobId("job-2");
        job.setUserId("user-1");
        job.setStatus(ImportJobState.COMPLETED);

        when(importJobRepository.findById("job-2")).thenReturn(Optional.of(job));

        var multipart = new MockMultipartFile("chunk", "file.mp3", "audio/mpeg", "data".getBytes());
        var exception = assertThrows(ResponseStatusException.class,
                () -> service.uploadAttachment("user-1", "job-2", "media/file.mp3", multipart, 0, 1, null, null));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void givenJobOwnedByDifferentUser_whenGetJobStatus_thenThrowsNotFound() {
        var job = new ImportJobModel();
        job.setJobId("job-3");
        job.setUserId("owner");

        when(importJobRepository.findById("job-3")).thenReturn(Optional.of(job));

        var exception = assertThrows(ResponseStatusException.class,
                () -> service.getJobStatus("other-user", "job-3"));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void givenRunningJob_whenCancelJob_thenMarksJobAsCancelling() {
        var job = new ImportJobModel();
        job.setJobId("job-4");
        job.setUserId("user-1");
        job.setStatus(ImportJobState.RUNNING);

        when(importJobRepository.findById("job-4")).thenReturn(Optional.of(job));

        var response = service.cancelJob("user-1", "job-4");

        assertEquals("CANCELLING", response.getStatus());
        assertTrue(response.isCancelRequested());
        assertFalse(response.isCancellable());
        assertEquals(1000, response.getPollAfterMs());
        verify(importJobRepository).save(job);
    }

    @Test
    void givenTerminalJob_whenCancelJob_thenThrowsConflict() {
        var job = new ImportJobModel();
        job.setJobId("job-5");
        job.setUserId("user-1");
        job.setStatus(ImportJobState.COMPLETED);

        when(importJobRepository.findById("job-5")).thenReturn(Optional.of(job));

        var exception = assertThrows(ResponseStatusException.class,
                () -> service.cancelJob("user-1", "job-5"));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void whenGetCapabilities_thenContainsLimitsAndSupportedTypes() {
        when(importRecordHandlerRegistry.supportedTypes()).thenReturn(List.of("flashcard"));
        when(importContentHandlerRegistry.supportedTypes()).thenReturn(List.of("audio-upload", "text"));
        when(importConceptHandlerRegistry.supportedTypes()).thenReturn(List.of("pronunciation", "transcription"));

        var capabilities = service.getCapabilities();

        assertEquals(List.of("1.0"), capabilities.get("schemaVersions"));
        assertEquals(List.of("flashcard"), capabilities.get("recordTypes"));
        assertEquals(List.of("audio-upload", "text"), capabilities.get("contentTypes"));
        assertEquals(List.of("pronunciation", "transcription"), capabilities.get("conceptTypes"));
        assertEquals(1024 * 1024L, capabilities.get("maxPayloadBytes"));
        assertEquals(512 * 1024L, capabilities.get("maxAttachmentChunkBytes"));
        assertEquals(1024 * 1024 * 5L, capabilities.get("maxTotalAttachmentBytesPerJob"));
    }

    private ImportJobCreateRequestDto createValidRequest(String clientRequestId) {
        var frontContent = new ImportContentDto();
        frontContent.setContentId("content-front");
        frontContent.setContentType("text");
        frontContent.setPayload(Map.of("text", "front", "languageId", "lang-1"));

        var backContent = new ImportContentDto();
        backContent.setContentId("content-back");
        backContent.setContentType("text");
        backContent.setPayload(Map.of("text", "back", "languageId", "lang-1"));

        var frontSlot = new ImportRecordSlotDto();
        frontSlot.setSlotKey("front");
        frontSlot.setContentRef("content-front");

        var backSlot = new ImportRecordSlotDto();
        backSlot.setSlotKey("back");
        backSlot.setContentRef("content-back");

        var record = new ImportRecordDto();
        record.setRecordType("flashcard");
        record.setRecordId("record-1");
        record.setSlots(List.of(frontSlot, backSlot));

        var contentSet = new ImportContentSetDto();
        contentSet.setSetType("flashcard-deck");
        contentSet.setSetId("deck-1");
        contentSet.setRecords(List.of(record));
        contentSet.setContents(List.of(frontContent, backContent));

        var request = new ImportJobCreateRequestDto();
        request.setSchemaVersion("1.0");
        request.setClientRequestId(clientRequestId);
        request.setImportType("content-set");
        request.setContentSet(contentSet);
        return request;
    }
}
