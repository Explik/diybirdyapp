package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.dto.admin.ConfigurationDto;

public interface ConfigurationRepository {
    ConfigurationDto get(String configId);
    ConfigurationDto update(ConfigurationDto configModel);
    void delete(String configId);
}
