package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputRecordAudioDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.query.GetAllExercisesQuery;
import com.explik.diybirdyapp.persistence.query.GetExerciseByIdsQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import com.explik.diybirdyapp.service.helper.ExerciseEvaluationHelper;
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
    private QueryHandler<GetExerciseByIdsQuery, ExerciseDto> getExerciseByIdsQueryHandler;

    @Autowired
    private QueryHandler<GetAllExercisesQuery, List<ExerciseDto>> getAllExercisesQueryHandler;

    @Autowired
    private ExerciseEvaluationHelper evaluationHelper;

    public ExerciseDto getExercise(String id, String sessionId) {
        var query = new GetExerciseByIdsQuery();
        query.setId(id);
        query.setSessionId(sessionId);

        return getExerciseByIdsQueryHandler.handle(query);
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

        return evaluationHelper.evaluateAnswer(answer);
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
        var query = new GetAllExercisesQuery();
        return getAllExercisesQueryHandler.handle(query);
    }
}
