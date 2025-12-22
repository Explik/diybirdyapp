package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.command.CreateLanguageVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateSpeechToTextConfigVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateTextToSpeechConfigVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateTranslateConfigVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LanguageVertexBuilder extends VertexBuilderBase<LanguageVertex> {
    private String id;
    private String name;
    private String isoCode;
    private final List<GoogleTextToSpeechConfigs> googleTextToSpeechConfigs = new ArrayList<>();
    private final List<GoogleSpeechToTextConfig> googleSpeechToTextConfigs = new ArrayList<>();
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

    public LanguageVertexBuilder withGoogleSpeechToText(String languageCode) {
        googleSpeechToTextConfigs.add(new GoogleSpeechToTextConfig(languageCode));
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

        var createCommand = new CreateLanguageVertexCommand();
        createCommand.setId(id);
        createCommand.setName(name);
        createCommand.setIsoCode(isoCode);
        this.factories.createLanguageVertexCommandHandler.handle(createCommand);
        var languageVertex = LanguageVertex.findById(traversalSource, id);

        for(var config : this.googleTextToSpeechConfigs) {
            var createConfigCommand = new CreateTextToSpeechConfigVertexCommand();
            createConfigCommand.setId(UUID.randomUUID().toString());
            createConfigCommand.setLanguageCode(config.languageCode);
            createConfigCommand.setVoiceName(config.voiceName);
            createConfigCommand.setLanguageVertex(languageVertex);
            this.factories.createTextToSpeechConfigVertexCommandHandler.handle(createConfigCommand);
        }

        for (var config : this.googleSpeechToTextConfigs) {
            var createConfigCommand = new CreateSpeechToTextConfigVertexCommand();
            createConfigCommand.setId(UUID.randomUUID().toString());
            createConfigCommand.setLanguageCode(config.languageCode);
            createConfigCommand.setLanguageVertex(languageVertex);
            this.factories.createSpeechToTextConfigVertexCommandHandler.handle(createConfigCommand);
        }

        for (var config : this.googleTranslateConfigs) {
            var createConfigCommand = new CreateTranslateConfigVertexCommand();
            createConfigCommand.setId(UUID.randomUUID().toString());
            createConfigCommand.setLanguageCode(config.languageCode);
            createConfigCommand.setLanguageVertex(languageVertex);
            this.factories.createTranslateConfigVertexCommandHandler.handle(createConfigCommand);
        }

        return languageVertex;
    }

    private record GoogleTextToSpeechConfigs(String languageCode, String voiceName) {}

    private record GoogleSpeechToTextConfig (String languageCode) { }

    private record GoogleTranslateConfig (String languageCode) { }
}
