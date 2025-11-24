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
public class ExerciseModelToDtoMapperUnitTests {
    @Autowired 
    GenericMapper<ExerciseModel, ExerciseDto> mapper;

    @ParameterizedTest
    @MethodSource("provideContentTypes")
    void givenContent_whenMap_mapsToDto(ExerciseContentModel contentModel, Class<? extends ExerciseContentDto> expectedDtoClass) {
        var model = new ExerciseModel();
        model.setContent(contentModel);

        var dto = mapper.map(model);
        var contentDto = dto.getContent();

        assertNotNull(dto);
        assertEquals(expectedDtoClass, contentDto.getClass());
    }

    @ParameterizedTest
    @MethodSource("provideInputTypes")
    void givenInput_whenMap_mapsToDto(ExerciseInputModel inputModel, Class<? extends ExerciseInputDto> expectedDtoClass) {
        var model = new ExerciseModel();
        model.setInput(inputModel);

        var dto = mapper.map(model);
        var inputDto = dto.getInput();

        assertNotNull(dto);
        assertEquals(expectedDtoClass, inputDto.getClass());
    }

    @ParameterizedTest
    @MethodSource("provideSelectOptionTypes")
    void givenSelectOption_whenMap_MapsToDto(ExerciseInputSelectOptionsModel.BaseOption option, String optionType) {
        var inputModel = new ExerciseInputSelectOptionsModel();
        inputModel.getOptions().add(option);

        var model = new ExerciseModel();
        model.setInput(inputModel);

        var dto = mapper.map(model);
        var inputDto = (ExerciseInputSelectOptionsDto)dto.getInput();

        assertNotNull(dto);
        assertEquals(optionType, inputDto.getOptionType());
    }

    // Test argument providers
    private static Stream<Arguments> provideContentTypes() {
        return Stream.of(
                Arguments.of(new ExerciseContentAudioDto(), ExerciseContentAudioDto.class),
                Arguments.of(new ExerciseContentTextDto(), ExerciseContentTextDto.class),
                Arguments.of(new ExerciseContentImageDto(), ExerciseContentImageDto.class),
                Arguments.of(new ExerciseContentVideoDto(), ExerciseContentVideoDto.class),
                Arguments.of(new ExerciseContentFlashcardDto(), ExerciseContentFlashcardDto.class),
                Arguments.of(new ExerciseContentFlashcardSideDto(), ExerciseContentFlashcardSideDto.class)
        );
    }

    private static Stream<Arguments> provideInputTypes() {
        var inputSelectModel = new ExerciseInputSelectOptionsDto();
        inputSelectModel.getOptions().add(new TextOption());

        return Stream.of(
                Arguments.of(new ExerciseInputArrangeTextOptionsDto(), ExerciseInputArrangeTextOptionsDto.class),
                Arguments.of(new ExerciseInputWriteTextDto(), ExerciseInputWriteTextDto.class),
                Arguments.of(new ExerciseInputPairOptionsDto(), ExerciseInputPairOptionsDto.class),
                Arguments.of(inputSelectModel, ExerciseInputSelectOptionsDto.class)
        );
    }

    private static Stream<Arguments> provideSelectOptionTypes() {
        return Stream.of(
                Arguments.of(new AudioOption(), "audio"),
                Arguments.of(new ImageOption(), "image"),
                Arguments.of(new TextOption(), "text"),
                Arguments.of(new VideoOption(), "video")
        );
    }
}
