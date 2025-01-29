package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.content.FileUploadModel;
import com.explik.diybirdyapp.model.content.FlashcardModel;
import com.explik.diybirdyapp.persistence.repository.FlashcardRepository;
import com.explik.diybirdyapp.persistence.service.BinaryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlashcardService {
    @Autowired
    BinaryStorageService binaryStorageService;

    @Autowired
    FlashcardRepository repository;

    public FlashcardModel add(FlashcardModel model, MultipartFile[] files) {
        validateFiles(model, files);
        saveFilesIfAny(files);

        return repository.add(model);
    }

    public FlashcardModel update(FlashcardModel model, MultipartFile[] files) {
        validateFiles(model, files);
        saveFilesIfAny(files);

        return repository.update(model);
    }

    public void delete(String id) {
        repository.delete(id);
    }

    public List<FlashcardModel> getAll(@Nullable String setId) {
        return repository.getAll(setId);
    }

    private void validateFiles(FlashcardModel model, MultipartFile[] files) {
        // Extract all file names from the model
        List<String> expectedFileNames = new ArrayList<>();

        if (model.getFrontContent() instanceof FileUploadModel frontFileUpload)
            expectedFileNames.addAll(frontFileUpload.getFileNames());
        if (model.getBackContent() instanceof FileUploadModel backFileUpload)
            expectedFileNames.addAll(backFileUpload.getFileNames());

        // Verify number of files
        if (files == null && expectedFileNames.isEmpty())
            return; // No files expected

        if (files.length != expectedFileNames.size())
            throw new IllegalArgumentException("Number of files does not match the number of expected files");

        // Verify file names
        for (MultipartFile file : files) {
            if (!expectedFileNames.contains(file.getOriginalFilename()))
                throw new IllegalArgumentException("File name does not match any expected file names");
        }
    }

    private void saveFilesIfAny(MultipartFile[] files) {
        if (files == null)
            return;

        try {
            for (MultipartFile file : files) {
                binaryStorageService.set(file.getOriginalFilename(), file.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }
}
