package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.model.exercise.ExerciseContentAudioDto;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionProgressDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ExerciseSessionModelToDtoUnitTests {
    @Autowired
    GenericMapper<ExerciseSessionModel, ExerciseSessionDto> mapper;

    @Test
    void givenNothing_whenMap_mapsToDto() {
        var model = new ExerciseSessionModel();

        var dto = mapper.map(model);

        assertNotNull(dto);
        assertEquals(ExerciseSessionDto.class, dto.getClass());
    }

    @Test
    void givenExercise_whenMap_mapsToDto() {
        var exerciseModel = new ExerciseModel();
        exerciseModel.setContent(new ExerciseContentAudioModel());

        var model = new ExerciseSessionModel();
        model.setExercise(exerciseModel);

        var dto = mapper.map(model);
        var exerciseDto = dto.getExercise();
        var exerciseContentDto = exerciseDto.getContent();

        assertNotNull(exerciseDto);
        assertEquals(ExerciseDto.class, exerciseDto.getClass());
        assertEquals(ExerciseContentAudioDto.class, exerciseContentDto.getClass());
    }

    @Test
    void givenExerciseProgress_whenMap_mapsToDto() {
        var model = new ExerciseSessionModel();
        model.setProgress(new ExerciseSessionProgressModel());

        var dto = mapper.map(model);
        var progressDto = dto.getProgress();

        assertNotNull(progressDto);
        assertEquals(ExerciseSessionProgressDto.class, progressDto.getClass());
    }
}
