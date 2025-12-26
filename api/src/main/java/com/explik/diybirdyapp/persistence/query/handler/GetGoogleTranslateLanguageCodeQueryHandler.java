package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.GoogleTranslationConfigModel;
import com.explik.diybirdyapp.persistence.query.GetGoogleTranslateLanguageCodeQuery;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetGoogleTranslateLanguageCodeQueryHandler implements QueryHandler<GetGoogleTranslateLanguageCodeQuery, GoogleTranslationConfigModel> {
    
    @Autowired
    private GraphTraversalSource traversalSource;
    
    @Override
    public GoogleTranslationConfigModel handle(GetGoogleTranslateLanguageCodeQuery query) {
        // Fetch both language codes in parallel
        String sourceLanguageCode = getLanguageCode(query.getSourceLanguageId());
        String targetLanguageCode = getLanguageCode(query.getTargetLanguageId());
        
        return new GoogleTranslationConfigModel(sourceLanguageCode, targetLanguageCode);
    }
    
    private String getLanguageCode(String languageId) {
        // Find the language vertex by ID
        LanguageVertex languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null) {
            throw new IllegalArgumentException("Language not found with ID: " + languageId);
        }
        
        // Find the Google Translate configuration for this language
        List<ConfigurationVertex> configs = ConfigurationVertex.findByLanguage(languageVertex);
        ConfigurationVertex translateConfig = configs.stream()
                .filter(config -> ConfigurationTypes.GOOGLE_TRANSLATE.equals(config.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No Google Translate configuration found for language ID: " + languageId
                ));
        
        // Return the language code from the configuration
        return translateConfig.getPropertyValue("languageCode");
    }
}
