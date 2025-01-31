package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.TextContentVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.UUID;

public class TextContentVertexBuilder extends VertexBuilderBase<TextContentVertex> implements DefaultLanguageInjectable {
    private String id;
    private String value;
    private LanguageVertex languageVertex;
    private LanguageVertex defaultLanguageVertex;

    public TextContentVertexBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public TextContentVertexBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public TextContentVertexBuilder withLanguage(LanguageVertex languageVertex) {
        this.languageVertex = languageVertex;
        return this;
    }

    @Override
    public void injectDefaultLanguage(LanguageVertex languageVertex) {
        this.defaultLanguageVertex = languageVertex;
    }

    public TextContentVertex build(GraphTraversalSource traversalSource) {
        var id = (this.id != null) ? this.id : UUID.randomUUID().toString();
        var language = getOrCreateLanguage(traversalSource);

        return this.factories.textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options(id, this.value, language));
    }

    private LanguageVertex getOrCreateLanguage(GraphTraversalSource traversalSource) {
        if (this.languageVertex != null) {
            return this.languageVertex;
        }
        else if (this.defaultLanguageVertex != null) {
            return this.defaultLanguageVertex;
        }
        else {
            var languageBuilder = new LanguageVertexBuilder();
            languageBuilder.injectFactories(factories);
            return languageBuilder.build(traversalSource);
        }
    }
}
