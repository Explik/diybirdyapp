package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.FlashcardDeckVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlashcardDeckVertexBuilder extends VertexBuilderBase<FlashcardDeckVertex> {
    private String id;
    private String name;
    private LanguageVertex defaultFrontLanguage;
    private LanguageVertex defaultBackLanguage;
    private List<FlashcardVertex> flashcardVertices = new ArrayList<>();
    private List<VertexBuilder<? extends FlashcardVertex>> flashcardVertexBuilders = new ArrayList<>();

    public FlashcardDeckVertexBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public FlashcardDeckVertexBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FlashcardDeckVertexBuilder withDefaultFrontLanguage(LanguageVertex defaultFrontLanguage) {
        this.defaultFrontLanguage = defaultFrontLanguage;
        return this;
    }

    public FlashcardDeckVertexBuilder withDefaultBackLanguage(LanguageVertex defaultBackLanguage) {
        this.defaultBackLanguage = defaultBackLanguage;
        return this;
    }

    public FlashcardDeckVertexBuilder withDefaultLanguages(LanguageVertex defaultFrontLanguage, LanguageVertex defaultBackLanguage) {
        this.defaultFrontLanguage = defaultFrontLanguage;
        this.defaultBackLanguage = defaultBackLanguage;
        return this;
    }

    public FlashcardDeckVertexBuilder addFlashcard(FlashcardVertexBuilder flashcardVertexBuilder) {
        this.flashcardVertexBuilders.add(flashcardVertexBuilder);
        return this;
    }

    public FlashcardDeckVertexBuilder addFlashcard(FlashcardVertex flashcardVertex) {
        this.flashcardVertices.add(flashcardVertex);
        return this;
    }

    public FlashcardDeckVertex build(GraphTraversalSource traversalSource) {
        if (this.factories == null)
            throw new RuntimeException("Factories were not");

        var id = (this.id != null) ? this.id : UUID.randomUUID().toString();
        var name = (this.name != null) ? this.name : "Unnamed deck";
        var vertices = new ArrayList<FlashcardVertex>(flashcardVertices);

        for(var builder : flashcardVertexBuilders) {
            if (builder instanceof VertexBuilderFactoriesInjectable injectable1) {
                injectable1.injectFactories(factories);
            }
            if (builder instanceof DefaultDeckInjectable injectable2) {
                injectable2.injectDefaultFrontLanguage(defaultFrontLanguage);
                injectable2.injectDefaultBackLanguage(defaultBackLanguage);
            }

            var vertex = builder.build(traversalSource);
            vertices.add(vertex);
        }

        return this.factories.flashcardDeckVertexFactory.create(
                traversalSource,
                new FlashcardDeckVertexFactory.Options(id, name, vertices));
    }
}
