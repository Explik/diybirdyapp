package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.LanguageModel;
import java.util.List;

public interface LanguageRepository {
    List<LanguageModel> getAll();
}
