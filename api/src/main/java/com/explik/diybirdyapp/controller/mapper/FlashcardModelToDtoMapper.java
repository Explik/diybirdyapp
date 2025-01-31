package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.content.*;
import com.explik.diybirdyapp.model.content.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FlashcardModelToDtoMapper implements GenericMapper<FlashcardModel, FlashcardDto> {
    protected final ModelMapper modelMapper;

    public FlashcardModelToDtoMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(contentConverter);
    }

    @Override
    public FlashcardDto map(FlashcardModel flashcardModel) {
        return modelMapper.map(flashcardModel, FlashcardDto.class);
    }

    Converter<FlashcardContentModel, FlashcardContentDto> contentConverter = new AbstractConverter<>() {
        @Override
        protected FlashcardContentDto convert(FlashcardContentModel source) {
            if (source instanceof FlashcardContentAudioModel)
                return modelMapper.map(source, FlashcardContentAudioDto.class);
            if (source instanceof FlashcardContentImageModel)
                return modelMapper.map(source, FlashcardContentImageDto.class);
            if (source instanceof FlashcardContentTextModel)
                return modelMapper.map(source, FlashcardContentTextDto.class);
            if (source instanceof FlashcardContentVideoModel)
                return modelMapper.map(source, FlashcardContentVideoDto.class);

            throw new RuntimeException("Unsupported flashcard content model type " + source.getClass().getName());
        }
    };
}
