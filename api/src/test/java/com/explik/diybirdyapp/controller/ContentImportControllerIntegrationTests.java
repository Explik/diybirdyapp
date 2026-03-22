package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.IntegrationTestConfigurations;
import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentUploadResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentDto;
import com.explik.diybirdyapp.controller.model.imports.ImportContentSetDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateRequestDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobStatusDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordDto;
import com.explik.diybirdyapp.controller.model.imports.ImportRecordSlotDto;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.service.imports.ImportSupport;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ContentImportControllerIntegrationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private GraphTraversalSource traversalSource;

    @BeforeEach
    void setUp() {
        traversalSource.V().drop().iterate();

        Assumptions.assumeTrue(
            applicationContext.containsBean("contentImportController"),
            "ContentImportController bean is not available in this runtime");

        var language = LanguageVertex.create(traversalSource);
        language.setId("lang-1");
        language.setIsoCode("en");
        language.setName("English");
    }

    @Test
    void givenTextOnlyRequest_whenCreateJob_thenJobCompletes() throws Exception {
        var request = createTextOnlyRequest("client-text-only");

        var createResponse = invokeCreateJob(request);

        assertEquals(202, createResponse.getStatusCode().value());
        assertNotNull(createResponse.getBody());

        var finalStatus = waitForTerminalState(createResponse.getBody().getJobId());

        assertEquals("COMPLETED", finalStatus.getStatus());
        assertEquals("FINALIZE", finalStatus.getStage());
        assertEquals(1, finalStatus.getProgress().getSuccessfulRecords());
        assertEquals(0, finalStatus.getAttachments().getDeclared());
    }

    @Test
    void givenRequestWithAttachment_whenUploaded_thenJobCompletes() throws Exception {
        var bytes = "audio-content".getBytes(StandardCharsets.UTF_8);
        var checksum = ImportSupport.sha256WithPrefix(bytes);
        var request = createRequestWithRequiredAttachment("client-with-attachment", checksum);

        var createResponse = invokeCreateJob(request);

        assertEquals(202, createResponse.getStatusCode().value());
        assertNotNull(createResponse.getBody());

        var jobId = createResponse.getBody().getJobId();
        var uploadResponse = invokeUploadAttachment(jobId, "media/audio.mp3", bytes, checksum);

        assertEquals(200, uploadResponse.getStatusCode().value());
        assertNotNull(uploadResponse.getBody());
        assertEquals("READY", uploadResponse.getBody().getAttachmentStatus());

        var finalStatus = waitForTerminalState(jobId);

        assertEquals("COMPLETED", finalStatus.getStatus());
        assertEquals(1, finalStatus.getAttachments().getReady());
        assertTrue(finalStatus.getAttachments().getMissingRequiredFileRefs().isEmpty());
    }

    @Test
    void whenGetCapabilities_thenReturnsImportCapabilities() throws Exception {
        var response = invokeCapabilities();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(List.of("1.0"), response.getBody().get("schemaVersions"));
        assertTrue((Boolean) response.getBody().get("supportsDeferredMediaUpload"));
    }

    private ImportJobStatusDto waitForTerminalState(String jobId) throws Exception {
        ImportJobStatusDto status = null;
        for (int i = 0; i < 120; i++) {
            var response = invokeGetStatus(jobId);
            status = response.getBody();
            assertNotNull(status);

            if ("COMPLETED".equals(status.getStatus())
                    || "FAILED".equals(status.getStatus())
                    || "CANCELLED".equals(status.getStatus())) {
                return status;
            }

            Thread.sleep(50);
        }

        throw new AssertionError("Job did not reach terminal state in time");
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<ImportJobCreateResponseDto> invokeCreateJob(ImportJobCreateRequestDto request) throws Exception {
        Method method = getControllerBean().getClass().getMethod(
                "createJob",
                Authentication.class,
                ImportJobCreateRequestDto.class);

        return (ResponseEntity<ImportJobCreateResponseDto>) method.invoke(getControllerBean(), null, request);
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<ImportAttachmentUploadResponseDto> invokeUploadAttachment(
            String jobId,
            String fileRef,
            byte[] bytes,
            String checksum) throws Exception {
        Method method = getControllerBean().getClass().getMethod(
                "uploadAttachment",
                Authentication.class,
                String.class,
                String.class,
                MultipartFile.class,
                Integer.class,
                Integer.class,
                String.class,
                String.class);

        return (ResponseEntity<ImportAttachmentUploadResponseDto>) method.invoke(
                getControllerBean(),
                null,
                jobId,
                fileRef,
                new MockMultipartFile("chunk", "audio.mp3", "audio/mpeg", bytes),
                0,
                1,
                null,
                checksum);
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<ImportJobStatusDto> invokeGetStatus(String jobId) throws Exception {
        Method method = getControllerBean().getClass().getMethod(
                "getStatus",
                Authentication.class,
                String.class);

        return (ResponseEntity<ImportJobStatusDto>) method.invoke(getControllerBean(), null, jobId);
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Map<String, Object>> invokeCapabilities() throws Exception {
        Method method = getControllerBean().getClass().getMethod("capabilities");
        return (ResponseEntity<Map<String, Object>>) method.invoke(getControllerBean());
    }

    private Object getControllerBean() {
        return applicationContext.getBean("contentImportController");
    }

    private ImportJobCreateRequestDto createTextOnlyRequest(String clientRequestId) {
        var frontContent = new ImportContentDto();
        frontContent.setContentId("content-front");
        frontContent.setContentType("text");
        frontContent.setPayload(Map.of("text", "front text", "languageId", "lang-1"));

        var backContent = new ImportContentDto();
        backContent.setContentId("content-back");
        backContent.setContentType("text");
        backContent.setPayload(Map.of("text", "back text", "languageId", "lang-1"));

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

    private ImportJobCreateRequestDto createRequestWithRequiredAttachment(String clientRequestId, String checksum) {
        var request = createTextOnlyRequest(clientRequestId);

        var audioContent = new ImportContentDto();
        audioContent.setContentId("content-back");
        audioContent.setContentType("audio-upload");
        audioContent.setPayload(Map.of("fileRef", "media/audio.mp3", "languageId", "lang-1"));

        request.getContentSet().setContents(List.of(
                request.getContentSet().getContents().get(0),
                audioContent
        ));

        var attachment = new ImportAttachmentDto();
        attachment.setFileRef("media/audio.mp3");
        attachment.setMimeType("audio/mpeg");
        attachment.setSizeBytes(13L);
        attachment.setChecksum(checksum);
        attachment.setRequired(true);

        request.getContentSet().setAttachments(List.of(attachment));

        return request;
    }

    @TestConfiguration
    public static class TestConfig extends IntegrationTestConfigurations.Default {
    }
}
