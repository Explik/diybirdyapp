package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.query.GenerateVoiceConfigQuery;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.TextToSpeechService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenerateVoiceConfigQueryHandler implements QueryHandler<GenerateVoiceConfigQuery, TextToSpeechService.Text> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Override
    public TextToSpeechService.Text handle(GenerateVoiceConfigQuery query) {
        var textContentVertex = TextContentVertex.findById(traversalSource, query.getTextContentVertexId());
        if (textContentVertex == null) {
            return null;
        }

        var languageVertex = textContentVertex.getLanguage();
        var textToSpeechConfigs = ConfigurationVertex.findByLanguageAndType(languageVertex, ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        if (textToSpeechConfigs.isEmpty()) {
            return null;
        }

        var textToSpeechConfig = textToSpeechConfigs.getFirst();
        return new TextToSpeechService.Text(
                textContentVertex.getValue(),
                textToSpeechConfig.getPropertyValue("languageCode"),
                textToSpeechConfig.getPropertyValue("voiceName"),
                "LINEAR16"
        );
    }
}
