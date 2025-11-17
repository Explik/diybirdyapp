package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.admin.ConfigurationModel;

public interface ConfigurationRepository {
    ConfigurationModel get(String configId);
    ConfigurationModel update(ConfigurationModel configModel);
    void delete(String configId);
}
