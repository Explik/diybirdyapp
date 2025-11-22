package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.dto.exercise.ExerciseInputRecordAudioDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.repository.ExerciseRepository;
import com.explik.diybirdyapp.persistence.service.BinaryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ExerciseService {
    @Autowired
    BinaryStorageService binaryStorageService;

    @Autowired
    ExerciseRepository repository;

    public ExerciseDto getExercise(String id, String sessionId) {
        return repository.get(id, sessionId);
    }

    public ExerciseDto submitExerciseAnswer(ExerciseAnswerModel answer, MultipartFile[] files) {
        // Validate model
        if (answer == null)
            throw new IllegalArgumentException("Answer model is required");
        if (answer.getExerciseId() == null)
            throw new IllegalArgumentException("Exercise ID is required");
        if (answer.getSessionId() == null)
            throw new IllegalArgumentException("Session ID is required");

        // Validate files
        validateFiles(answer, files);
        saveFilesIfAny(files);

        return repository.submitAnswer(answer);
    }

    private void validateFiles(ExerciseAnswerModel answer, MultipartFile[] files) {
        // Extract all file names from the model
        List<String> expectedFileNames = List.of();

        if (answer.getInput() instanceof ExerciseInputRecordAudioDto audioAnswer) {
            expectedFileNames = List.of(audioAnswer.getUrl());
        }

        // Verify number of files
        if (files == null && expectedFileNames.isEmpty())
            return; // No files expected

        // Verify number of files
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

    public List<ExerciseDto> getExercises() {
        return repository.getAll();
    }
}
