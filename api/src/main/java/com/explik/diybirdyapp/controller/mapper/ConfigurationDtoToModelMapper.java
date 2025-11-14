package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.admin.*;
import com.explik.diybirdyapp.model.admin.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationDtoToModelMapper implements GenericMapper<ConfigurationDto, ConfigurationModel>{
    private final ModelMapper modelMapper;

    public ConfigurationDtoToModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(configurationConverter);
    }

    @Override
    public ConfigurationModel map(ConfigurationDto configurationDto) {
        return modelMapper.map(configurationDto, ConfigurationModel.class);
    }

    Converter<ConfigurationDto, ConfigurationModel> configurationConverter = new AbstractConverter<ConfigurationDto, ConfigurationModel>() {
        @Override
        protected ConfigurationModel convert(ConfigurationDto source) {
            if (source == null)
                return null;
            if (source instanceof ConfigurationMicrosoftTextToSpeechDto)
                return modelMapper.map(source, ConfigurationMicrosoftTextToSpeechModel.class);
            if (source instanceof ConfigurationGoogleTextToSpeechDto)
                return modelMapper.map(source, ConfigurationGoogleTextToSpeechModel.class);
            if (source instanceof ConfigurationGoogleSpeechToTextDto)
                return modelMapper.map(source, ConfigurationGoogleSpeechToTextModel.class);
            if (source instanceof ConfigurationGoogleTranslateDto)
                return modelMapper.map(source, ConfigurationGoogleTranslateModel.class);

            throw new RuntimeException("Unsupported configuration type: " + source.getClass().getName());
        }
    };
}
