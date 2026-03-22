package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentUploadResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateRequestDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobCreateResponseDto;
import com.explik.diybirdyapp.controller.model.imports.ImportJobStatusDto;
import com.explik.diybirdyapp.service.imports.ContentImportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class ContentImportController {
    private final ContentImportService contentImportService;

    public ContentImportController(ContentImportService contentImportService) {
        this.contentImportService = contentImportService;
    }

    @PostMapping(value = "/content-import/jobs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImportJobCreateResponseDto> createJob(
            Authentication authentication,
            @Valid @RequestBody ImportJobCreateRequestDto request) {
        var response = contentImportService.submitJob(getUserId(authentication), request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(value = "/content-import/jobs/{jobId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportAttachmentUploadResponseDto> uploadAttachment(
            Authentication authentication,
            @PathVariable("jobId") String jobId,
            @RequestParam("fileRef") String fileRef,
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam(value = "chunkIndex", required = false) Integer chunkIndex,
            @RequestParam(value = "totalChunks", required = false) Integer totalChunks,
            @RequestParam(value = "chunkChecksum", required = false) String chunkChecksum,
            @RequestParam(value = "finalChecksum", required = false) String finalChecksum) {
        var response = contentImportService.uploadAttachment(
                getUserId(authentication),
                jobId,
                fileRef,
                chunk,
                chunkIndex,
                totalChunks,
                chunkChecksum,
                finalChecksum);

        var status = "READY".equals(response.getAttachmentStatus()) ? HttpStatus.OK : HttpStatus.ACCEPTED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/content-import/jobs/{jobId}")
    public ResponseEntity<ImportJobStatusDto> getStatus(Authentication authentication, @PathVariable("jobId") String jobId) {
        var response = contentImportService.getJobStatus(getUserId(authentication), jobId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/content-import/jobs/{jobId}/cancel")
    public ResponseEntity<ImportJobStatusDto> cancelJob(Authentication authentication, @PathVariable("jobId") String jobId) {
        var response = contentImportService.cancelJob(getUserId(authentication), jobId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/content-import/capabilities")
    public ResponseEntity<Map<String, Object>> capabilities() {
        return ResponseEntity.ok(contentImportService.getCapabilities());
    }

    private String getUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "anonymous";
        }

        return authentication.getName();
    }
}
