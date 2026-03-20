package com.explik.diybirdyapp.service.imports;

import com.explik.diybirdyapp.controller.model.imports.ImportAttachmentUploadResponseDto;
import com.explik.diybirdyapp.model.imports.ImportAttachmentChunkModel;
import com.explik.diybirdyapp.model.imports.ImportAttachmentModel;
import com.explik.diybirdyapp.model.imports.ImportAttachmentState;
import com.explik.diybirdyapp.model.imports.ImportJobModel;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

@Component
public class ContentImportAttachmentService {
    private final ImportBinaryStore importBinaryStore;
    private final BinaryStorageService binaryStorageService;
    private final long maxAttachmentChunkBytes;

    public ContentImportAttachmentService(
            ImportBinaryStore importBinaryStore,
            BinaryStorageService binaryStorageService,
            @Value("${content-import.max-attachment-chunk-bytes:10485760}") long maxAttachmentChunkBytes) {
        this.importBinaryStore = importBinaryStore;
        this.binaryStorageService = binaryStorageService;
        this.maxAttachmentChunkBytes = maxAttachmentChunkBytes;
    }

    public ImportAttachmentUploadResponseDto receiveChunk(
            ImportJobModel job,
            ImportAttachmentModel attachment,
            MultipartFile chunk,
            Integer chunkIndex,
            Integer totalChunks,
            String chunkChecksum,
            String finalChecksum) {
        if (chunk == null || chunk.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing attachment chunk");
        }

        if (chunk.getSize() > maxAttachmentChunkBytes) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Attachment chunk exceeds limit");
        }

        var index = chunkIndex == null ? 0 : chunkIndex;
        var total = totalChunks == null ? 1 : totalChunks;
        if (index < 0 || total <= 0 || index >= total) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid chunkIndex/totalChunks");
        }

        byte[] bytes;
        try {
            bytes = chunk.getBytes();
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read chunk bytes");
        }

        if (chunkChecksum != null && !chunkChecksum.isBlank()) {
            var computed = ImportSupport.sha256WithPrefix(bytes);
            if (!chunkChecksum.equalsIgnoreCase(computed)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Chunk checksum mismatch");
            }
        }

        synchronized (attachment) {
            if (attachment.getTotalChunks() != null && attachment.getTotalChunks() != total) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Inconsistent totalChunks for fileRef");
            }
            attachment.setTotalChunks(total);

            var existingChunk = importBinaryStore.readChunk(job.getJobId(), attachment.getFileRef(), index);
            if (existingChunk != null) {
                if (!Arrays.equals(existingChunk, bytes)) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Chunk already exists with different content");
                }
            } else {
                importBinaryStore.writeChunk(job.getJobId(), attachment.getFileRef(), index, bytes);
            }

            var chunkModel = new ImportAttachmentChunkModel();
            chunkModel.setChunkIndex(index);
            chunkModel.setTotalChunks(total);
            chunkModel.setChecksum(chunkChecksum);
            chunkModel.setSizeBytes(bytes.length);
            chunkModel.setReceivedAt(Instant.now());
            attachment.getChunks().put(index, chunkModel);

            if (!attachment.isComplete()) {
                attachment.setState(ImportAttachmentState.UPLOADING);
                return createUploadingResponse(job, attachment);
            }

            var storageKey = importBinaryStore.assembleChunks(job.getJobId(), attachment.getFileRef(), total);
            var assembled = importBinaryStore.readAssembled(job.getJobId(), attachment.getFileRef());
            if (assembled == null) {
                attachment.setState(ImportAttachmentState.FAILED);
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Failed to assemble attachment");
            }

            var expectedChecksum = finalChecksum;
            if ((expectedChecksum == null || expectedChecksum.isBlank()) && attachment.getChecksum() != null) {
                expectedChecksum = attachment.getChecksum();
            }
            if (expectedChecksum != null && !expectedChecksum.isBlank()) {
                var actualChecksum = ImportSupport.sha256WithPrefix(assembled);
                if (!expectedChecksum.equalsIgnoreCase(actualChecksum)) {
                    attachment.setState(ImportAttachmentState.FAILED);
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Final checksum mismatch");
                }
            }

            binaryStorageService.set(ImportSupport.extractFileName(attachment.getFileRef()), assembled);

            attachment.setStorageKey(storageKey);
            attachment.setState(ImportAttachmentState.READY);
            attachment.setUpdatedAt(Instant.now());

            return createReadyResponse(job, attachment);
        }
    }

    private ImportAttachmentUploadResponseDto createUploadingResponse(ImportJobModel job, ImportAttachmentModel attachment) {
        var response = new ImportAttachmentUploadResponseDto();
        response.setJobId(job.getJobId());
        response.setFileRef(attachment.getFileRef());
        response.setAttachmentStatus("UPLOADING");
        response.setReceivedChunks(attachment.getReceivedChunks());
        response.setTotalChunks(attachment.getTotalChunks());
        response.setPollAfterMs(1000);
        return response;
    }

    private ImportAttachmentUploadResponseDto createReadyResponse(ImportJobModel job, ImportAttachmentModel attachment) {
        var response = new ImportAttachmentUploadResponseDto();
        response.setJobId(job.getJobId());
        response.setFileRef(attachment.getFileRef());
        response.setAttachmentStatus("READY");
        response.setReceivedChunks(attachment.getReceivedChunks());
        response.setTotalChunks(attachment.getTotalChunks());
        return response;
    }
}
