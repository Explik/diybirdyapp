package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.content.VoiceModel;
import com.explik.diybirdyapp.persistence.query.GetVoiceByLanguageIdQuery;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetVoiceByLanguageIdQueryHandler implements QueryHandler<GetVoiceByLanguageIdQuery, VoiceModel> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public VoiceModel handle(GetVoiceByLanguageIdQuery query) {
        var languageVertex = LanguageVertex.findById(traversalSource, query.getLanguageId());
        if (languageVertex == null)
            return null;

        var voiceConfigs = ConfigurationVertex.findByLanguageAndType(languageVertex, ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        var voiceConfig = voiceConfigs.stream().findFirst().orElse(null);
        if (voiceConfig == null)
            return null;

        return createModel(languageVertex, voiceConfig);
    }

    private static VoiceModel createModel(LanguageVertex langVertex, ConfigurationVertex configVertex) {
        var model = new VoiceModel();
        model.setLanguageId(langVertex.getId());
        model.setLanguageName(langVertex.getName());
        model.setVoiceId(configVertex.getId());
        model.setVoiceName(configVertex.getPropertyValue("voiceName"));
        model.setVoiceLanguageCode(configVertex.getPropertyValue("languageCode"));
        return model;
    }
}
