package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.LanguageVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.TextToSpeechConfigVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.TranslateConfigVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LanguageVertexBuilder extends VertexBuilderBase<LanguageVertex> {
    private String id;
    private String name;
    private String isoCode;
    private final List<GoogleTextToSpeechConfigs> googleTextToSpeechConfigs = new ArrayList<>();
    private final List<GoogleTranslateConfig> googleTranslateConfigs = new ArrayList<>();


    public LanguageVertexBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public LanguageVertexBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LanguageVertexBuilder withIsoCode(String isoCode) {
        this.isoCode = isoCode;
        return this;
    }

    public LanguageVertexBuilder withGoogleTextToSpeech(String languageCode, String voiceName) {
        this.googleTextToSpeechConfigs.add(new GoogleTextToSpeechConfigs(languageCode, voiceName));
        return this;
    }

    public LanguageVertexBuilder withGoogleTranslate(String languageCode) {
        this.googleTranslateConfigs.add(new GoogleTranslateConfig(languageCode));
        return this;
    }

    @Override
    public LanguageVertex build(GraphTraversalSource traversalSource) {
        if (this.factories == null)
            throw new IllegalStateException("Factories were not provided");

        var id = (this.id != null) ? this.id : UUID.randomUUID().toString();
        var name = (this.name != null) ? this.name : "Language-" + id;
        var isoCode = (this.isoCode != null) ? this.isoCode : "Lang-" + id;

        var languageVertex = this.factories.languageVertexFactory.create(
                traversalSource,
                new LanguageVertexFactory.Options(id, name, isoCode));

        for(var config : this.googleTextToSpeechConfigs) {
            this.factories.textToSpeechConfigVertexFactory.create(
                    traversalSource,
                    new TextToSpeechConfigVertexFactory.Options(UUID.randomUUID().toString(), config.languageCode, config.voiceName, languageVertex));
        }

        for (var config : this.googleTranslateConfigs) {
            this.factories.translationConfigVertexFactory.create(
                    traversalSource,
                    new TranslateConfigVertexFactory.Options(UUID.randomUUID().toString(), config.languageCode, languageVertex));
        }

        return languageVertex;
    }

    private record GoogleTextToSpeechConfigs(String languageCode, String voiceName) {}

    private record GoogleTranslateConfig (String languageCode) { }
}
