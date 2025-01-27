package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.content.VocabularyContentTextDto;
import com.explik.diybirdyapp.controller.dto.content.VocabularyDto;
import com.explik.diybirdyapp.model.content.VocabularyModel;
import com.explik.diybirdyapp.model.content.VocabularyTextContentModel;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class VocabularyModelToDtoMapper extends BaseModelToDtoMapper implements GenericMapper<VocabularyModel, VocabularyDto> {
    public VocabularyModelToDtoMapper() {
        super();

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
