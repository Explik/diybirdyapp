package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.admin.ConfigurationModel;

import java.util.List;

public interface ConfigurationRepository {
    List<ConfigurationModel> getAll(String languageId);
}
