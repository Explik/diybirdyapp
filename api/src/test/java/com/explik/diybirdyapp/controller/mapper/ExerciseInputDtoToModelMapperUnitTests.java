package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.model.exercise.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ExerciseInputDtoToModelMapperUnitTests {
    @Autowired
    GenericMapper<ExerciseInputDto, ExerciseInputModel> mapper;

    @ParameterizedTest
    @MethodSource("provideExerciseInputTypes")
    void givenExerciseInput_whenMap_mapsToModel(ExerciseInputDto inputDto, Class<? extends ExerciseInputModel> expectedModelClass) {
        var model = mapper.map(inputDto);

        assertNotNull(model);
        assertEquals(expectedModelClass, model.getClass());
    }

    // Test argument providers
    private static Stream<Arguments> provideExerciseInputTypes() {
        return Stream.of(
            Arguments.of(new ExerciseInputArrangeTextOptionsDto(), ExerciseInputArrangeTextOptionsModel.class),
            Arguments.of(new ExerciseInputWriteTextDto(), ExerciseInputTextModel.class),
            Arguments.of(new ExerciseInputSelectOptionsDto(), ExerciseInputSelectOptionsModel.class),
            Arguments.of(new ExerciseInputSelectReviewOptionsDto(), ExerciseInputRecognizabilityRatingModel.class),
            Arguments.of(new ExerciseInputPairOptionsDto(), ExerciseInputPairOptionsModel.class),
            Arguments.of(new ExerciseInputRecordAudioDto(), ExerciseInputAudioModel.class)
        );
    }
}
