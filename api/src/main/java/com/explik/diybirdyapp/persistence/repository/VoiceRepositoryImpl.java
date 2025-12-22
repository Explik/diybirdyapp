package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.content.VoiceModel;
import com.explik.diybirdyapp.persistence.query.GetVoiceByLanguageIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VoiceRepositoryImpl implements VoiceRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    private QueryHandler<GetVoiceByLanguageIdQuery, VoiceModel> getVoiceByLanguageIdQueryHandler;

    public VoiceRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public VoiceModel get(String languageId) {
        var query = new GetVoiceByLanguageIdQuery();
        query.setLanguageId(languageId);
        return getVoiceByLanguageIdQueryHandler.handle(query);
    }
}
