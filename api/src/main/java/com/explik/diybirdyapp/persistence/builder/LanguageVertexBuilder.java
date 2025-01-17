package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.LanguageVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.TextToSpeechConfigVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LanguageVertexBuilder extends VertexBuilderBase<LanguageVertex> {
    private String id;
    private String name;
    private String abbreviation;
    private final List<GoogleTextToSpeechConfigs> googleTextToSpeechConfigs = new ArrayList<>();

    public LanguageVertexBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public LanguageVertexBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LanguageVertexBuilder withAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
        return this;
    }

    public LanguageVertexBuilder withGoogleTextToSpeech(String languageCode, String voiceName) {
        this.googleTextToSpeechConfigs.add(new GoogleTextToSpeechConfigs(languageCode, voiceName));
        return this;
    }

    @Override
    public LanguageVertex build(GraphTraversalSource traversalSource) {
        if (this.factories == null)
            throw new IllegalStateException("Factories were not provided");

        var id = (this.id != null) ? this.id : UUID.randomUUID().toString();
        var name = (this.name != null) ? this.name : "Language-" + id;
        var abbreviation = (this.abbreviation != null) ? this.abbreviation : "Lang-" + id;

        var languageVertex = this.factories.languageVertexFactory.create(
                traversalSource,
                new LanguageVertexFactory.Options(id, name, abbreviation));

        for(var config : this.googleTextToSpeechConfigs) {
            this.factories.textToSpeechConfigVertexFactory.create(
                    traversalSource,
                    new TextToSpeechConfigVertexFactory.Options(UUID.randomUUID().toString(), config.languageCode, config.voiceName, languageVertex));
        }
        return languageVertex;
    }

    private record GoogleTextToSpeechConfigs(String languageCode, String voiceName) {}
}
