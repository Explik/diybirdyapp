package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.model.content.VocabularyContentTextDto;
import com.explik.diybirdyapp.controller.model.content.VocabularyDto;
import com.explik.diybirdyapp.model.content.VocabularyModel;
import com.explik.diybirdyapp.model.content.VocabularyTextContentModel;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class VocabularyModelToDtoMapper implements GenericMapper<VocabularyModel, VocabularyDto> {
    private final ModelMapper modelMapper;

    public VocabularyModelToDtoMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(vocabularyContentConverter);
    }

    @Override
    public VocabularyDto map(VocabularyModel vocabularyModel) {
        return modelMapper.map(vocabularyModel, VocabularyDto.class);
    }

    Converter<VocabularyTextContentModel, VocabularyContentTextDto> vocabularyContentConverter = new AbstractConverter<VocabularyTextContentModel, VocabularyContentTextDto>() {
        @Override
        public VocabularyContentTextDto convert(VocabularyTextContentModel source) {
            return modelMapper.map(source, VocabularyContentTextDto.class);
        }
    };
}
