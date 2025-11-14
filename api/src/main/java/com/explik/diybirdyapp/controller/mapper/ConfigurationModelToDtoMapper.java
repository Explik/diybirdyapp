package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.admin.*;
import com.explik.diybirdyapp.model.admin.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationModelToDtoMapper implements GenericMapper<ConfigurationModel, ConfigurationDto> {
    private final ModelMapper modelMapper;

    public ConfigurationModelToDtoMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(configurationConverter);
    }

    @Override
    public ConfigurationDto map(ConfigurationModel configurationModel) {
        return modelMapper.map(configurationModel, ConfigurationDto.class);
    }

    Converter<ConfigurationModel, ConfigurationDto> configurationConverter = new AbstractConverter<ConfigurationModel, ConfigurationDto>() {
        @Override
        protected ConfigurationDto convert(ConfigurationModel source) {
            if (source == null)
                return null;
            if (source instanceof ConfigurationMicrosoftTextToSpeechModel)
                return modelMapper.map(source, ConfigurationMicrosoftTextToSpeechDto.class);
            if (source instanceof ConfigurationGoogleTextToSpeechModel)
                return modelMapper.map(source, ConfigurationGoogleTextToSpeechDto.class);
            if (source instanceof ConfigurationGoogleSpeechToTextModel)
                return modelMapper.map(source, ConfigurationGoogleSpeechToTextDto.class);
            if (source instanceof ConfigurationGoogleTranslateModel)
                return modelMapper.map(source, ConfigurationGoogleTranslateDto.class);

            throw new RuntimeException("Unsupported configuration type: " + source.getClass().getName());
        }
    };

}
