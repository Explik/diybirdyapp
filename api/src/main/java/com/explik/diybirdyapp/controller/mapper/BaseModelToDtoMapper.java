package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.*;
import com.explik.diybirdyapp.model.exercise.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;

public abstract class BaseModelToDtoMapper {
    protected final ModelMapper modelMapper;

    public BaseModelToDtoMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(exerciseContentConverter);
        modelMapper.addConverter(exerciseInputConverter);
    }

    Converter<ExerciseContentModel, ExerciseContentDto> exerciseContentConverter = new AbstractConverter<ExerciseContentModel, ExerciseContentDto>() {
        protected ExerciseContentDto convert(ExerciseContentModel source) {
            if (source == null)
                return null;
            if (source instanceof ExerciseContentAudioModel)
                return modelMapper.map(source, ExerciseContentAudioDto.class);
            if (source instanceof ExerciseContentTextModel)
                return modelMapper.map(source, ExerciseContentTextDto.class);
            if (source instanceof ExerciseContentImageModel)
                return modelMapper.map(source, ExerciseContentImageDto.class);
            if (source instanceof ExerciseContentVideoModel)
                return modelMapper.map(source, ExerciseContentVideoDto.class);
            if (source instanceof ExerciseContentFlashcardModel)
                return modelMapper.map(source, ExerciseContentFlashcardDto.class);
            if (source instanceof ExerciseContentFlashcardSideModel)
                return modelMapper.map(source, ExerciseContentFlashcardSideDto.class);

            throw new RuntimeException("Unsupported content type: " + source.getClass().getName());
        }
    };

    Converter<ExerciseInputModel, ExerciseInputDto> exerciseInputConverter = new AbstractConverter<ExerciseInputModel, ExerciseInputDto>() {
        @Override
        protected ExerciseInputDto convert(ExerciseInputModel source) {
            if (source == null)
                return null;
            if (source instanceof ExerciseInputArrangeTextOptionsModel)
                return modelMapper.map(source, ExerciseInputArrangeTextOptionsDto.class);
            if (source instanceof ExerciseInputTextModel)
                return modelMapper.map(source, ExerciseInputWriteTextDto.class);
            if (source instanceof ExerciseInputPairOptionsModel pairOptionsModel)
                return convert(pairOptionsModel);
            if (source instanceof ExerciseInputSelectOptionsModel selectOptionsModel)
                return convert(selectOptionsModel);

            throw new RuntimeException("Unsupported input type: " + source.getClass().getName());
        }

        private ExerciseInputSelectOptionsDto convert(ExerciseInputSelectOptionsModel source) {
            if (source.getOptions().isEmpty())
                throw new RuntimeException("Options list is empty");

            var target = modelMapper.map(source, ExerciseInputSelectOptionsDto.class);

            var targetOptionType = getOptionType(source.getOptions().getFirst().getClass());
            target.setOptionType(targetOptionType);

            var targetOptions = source.getOptions().stream().map(this::convert).toList();
            target.setOptions(targetOptions);

            return target;
        }

        private String getOptionType(Class<? extends ExerciseInputSelectOptionsModel.BaseOption> optionClass) {
            if (optionClass.equals(ExerciseInputSelectOptionsModel.AudioOption.class))
                return "audio";
            if (optionClass.equals(ExerciseInputSelectOptionsModel.ImageOption.class))
                return "image";
            if (optionClass.equals(ExerciseInputSelectOptionsModel.TextOption.class))
                return "text";
            if (optionClass.equals(ExerciseInputSelectOptionsModel.VideoOption.class))
                return "video";

            throw new RuntimeException("Unsupported option type: " + optionClass.getName());
        }

        private ExerciseInputSelectOptionsDto.SelectOptionInputBaseOption convert(ExerciseInputSelectOptionsModel.BaseOption source) {
            if (source instanceof ExerciseInputSelectOptionsModel.AudioOption)
                return modelMapper.map(source, ExerciseInputSelectOptionsDto.SelectOptionInputAudioOption.class);
            if (source instanceof ExerciseInputSelectOptionsModel.ImageOption)
                return modelMapper.map(source, ExerciseInputSelectOptionsDto.SelectOptionInputImageOption.class);
            if (source instanceof ExerciseInputSelectOptionsModel.TextOption)
                return modelMapper.map(source, ExerciseInputSelectOptionsDto.SelectOptionInputTextOption.class);
            if (source instanceof ExerciseInputSelectOptionsModel.VideoOption)
                return modelMapper.map(source, ExerciseInputSelectOptionsDto.SelectOptionInputVideoOption.class);

            throw new RuntimeException("Unsupported option type: " + source.getClass().getName());
        }

        private ExerciseInputPairOptionsDto convert(ExerciseInputPairOptionsModel source) {
            var target = new ExerciseInputPairOptionsDto();
            target.setId(source.getId());
            target.setType(source.getType());

            var leftOptionDtos = new ArrayList<ExerciseInputPairOptionsDto.PairOptionsInputOptionDto>();
            for (var leftOption : source.getLeftOptions()) {
                var leftOptionDto = new ExerciseInputPairOptionsDto.PairOptionsInputTextOptionDto();
                leftOptionDto.setId(leftOption.getId());
                leftOptionDto.setText(leftOption.getText());
                leftOptionDtos.add(leftOptionDto);
            }
            target.setLeftOptions(leftOptionDtos);

            var rightOptionDtos = new ArrayList<ExerciseInputPairOptionsDto.PairOptionsInputOptionDto>();
            for (var rightOption : source.getRightOptions()) {
                var rightOptionDto = new ExerciseInputPairOptionsDto.PairOptionsInputTextOptionDto();
                rightOptionDto.setId(rightOption.getId());
                rightOptionDto.setText(rightOption.getText());
                rightOptionDtos.add(rightOptionDto);
            }
            target.setRightOptions(rightOptionDtos);

            return target;
        }
    };
}
