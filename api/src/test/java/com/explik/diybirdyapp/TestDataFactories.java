package com.explik.diybirdyapp;

import com.explik.diybirdyapp.controller.model.content.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class TestDataFactories {
    public static class FlashcardCreationFactory {
        public FlashcardCreation create(String leftType, String rightType) {
            var dto = new FlashcardDto();
            var files = new ArrayList<MultipartFile>();

            var leftContentCreation = createContent(leftType);
            dto.setFrontContent(leftContentCreation.dto());
            if (leftContentCreation.file() != null)
                files.add(leftContentCreation.file());

            var rightContentCreation = createContent(rightType);
            dto.setBackContent(rightContentCreation.dto());
            if (rightContentCreation.file() != null)
                files.add(rightContentCreation.file());

            return new FlashcardCreation(dto, files.toArray(MultipartFile[]::new));
        }

        FlashcardContentCreation createContent(String type) {
            return switch (type) {
                case ContentTypes.TEXT -> createTextContent();
                case ContentTypes.AUDIO_UPLOAD -> createAudioContent();
                case ContentTypes.IMAGE_UPLOAD -> createImageContent();
                case ContentTypes.VIDEO_UPLOAD -> createVideoContent();
                default -> throw new IllegalArgumentException("Unknown content type: " + type);
            };
        }

        FlashcardContentCreation createTextContent() {
            var dto = new FlashcardContentTextDto();
            dto.setType(ContentTypes.TEXT);
            dto.setText("text");
            return new FlashcardContentCreation(dto, null);
        }

        FlashcardContentCreation createAudioContent() {
            var dto = new FlashcardContentUploadAudioDto();
            dto.setType(ContentTypes.AUDIO_UPLOAD);
            dto.setAudioFileName("audio.mp3");

            var file = new MockMultipartFile("file", "audio.mp3", "audio/mpeg", new byte[0]);

            return new FlashcardContentCreation(dto, file);
        }

        FlashcardContentCreation createImageContent() {
            var dto = new FlashcardContentUploadImageDto();
            dto.setType(ContentTypes.IMAGE_UPLOAD);
            dto.setImageFileName("image.jpg");

            var file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[0]);

            return new FlashcardContentCreation(dto, file);
        }

        FlashcardContentCreation createVideoContent() {
            var dto = new FlashcardContentUploadVideoDto();
            dto.setType(ContentTypes.VIDEO_UPLOAD);
            dto.setVideoFileName("video.mp4");

            var file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[0]);

            return new FlashcardContentCreation(dto, file);
        }
    }

    public record FlashcardCreation(FlashcardDto dto, MultipartFile[] files) { }

    public record FlashcardContentCreation(FlashcardContentDto dto, MultipartFile file) { }
}
