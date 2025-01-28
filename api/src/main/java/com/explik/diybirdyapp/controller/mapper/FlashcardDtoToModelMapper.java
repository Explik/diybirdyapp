package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.content.*;
import com.explik.diybirdyapp.model.content.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class FlashcardDtoToModelMapper implements GenericMapper<FlashcardDto, FlashcardModel> {
    protected final ModelMapper modelMapper;

    public FlashcardDtoToModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(contentConverter);
    }

    @Override
    public FlashcardModel map(FlashcardDto flashcardDto) {
        return modelMapper.map(flashcardDto, FlashcardModel.class);
    }

    Converter<FlashcardContentDto, FlashcardContentModel> contentConverter = new AbstractConverter<>() {
        @Override
        protected FlashcardContentModel  convert(FlashcardContentDto source) {
            if (source == null)
                return null;

            // Content
            if (source instanceof FlashcardContentAudioDto)
                return modelMapper.map(source, FlashcardContentAudioModel.class);
            if (source instanceof FlashcardContentImageDto)
                return modelMapper.map(source, FlashcardContentImageModel.class);
            if (source instanceof FlashcardContentTextDto)
                return modelMapper.map(source, FlashcardContentTextModel.class);
            if (source instanceof FlashcardContentVideoDto)
                return modelMapper.map(source, FlashcardContentVideoModel.class);

            // Content upload
            if (source instanceof FlashcardContentUploadAudioDto)
                return modelMapper.map(source, FlashcardContentUploadAudioModel.class);
            if (source instanceof FlashcardContentUploadImageDto)
                return modelMapper.map(source, FlashcardContentUploadImageModel.class);
            if (source instanceof FlashcardContentUploadVideoDto)
                return modelMapper.map(source, FlashcardContentUploadVideoModel.class);

            throw new RuntimeException("Unsupported flashcard content model type " + source.getClass().getName());
        }
    };
}
