package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.command.CreateFlashcardVertexCommand;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class FlashcardVertexBuilder extends VertexBuilderBase<FlashcardVertex> implements DefaultDeckInjectable {
    private String id;
    private ContentVertex frontContent;
    private ContentVertex backContent;
    private VertexBuilder<? extends ContentVertex> frontContentBuilder;
    private VertexBuilder<? extends ContentVertex> backContentBuilder;
    private LanguageVertex defaultFrontLanguage;
    private LanguageVertex defaultBackLanguage;

    public FlashcardVertexBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public FlashcardVertexBuilder withFrontContent(ContentVertex frontContent) {
        this.frontContent = frontContent;
        return this;
    }

    public FlashcardVertexBuilder withFrontContent(VertexBuilder<? extends ContentVertex> frontContentBuilder) {
        this.frontContentBuilder = frontContentBuilder;
        return this;
    }

    public FlashcardVertexBuilder withBackContent(ContentVertex backContent) {
        this.backContent = backContent;
        return this;
    }

    public FlashcardVertexBuilder withBackContent(VertexBuilder<? extends ContentVertex> backContentBuilder) {
        this.backContentBuilder = backContentBuilder;
        return this;
    }

    public FlashcardVertexBuilder withFrontText(String frontText) {
        var content = new TextContentVertexBuilder().withValue(frontText);
        withFrontContent(content);
        return this;
    }

    public FlashcardVertexBuilder withFrontText(String frontText, LanguageVertex language) {
        var content = new TextContentVertexBuilder().withValue(frontText).withLanguage(language);
        withFrontContent(content);
        return this;
    }

    public FlashcardVertexBuilder withBackText(String backText) {
        var content = new TextContentVertexBuilder().withValue(backText);
        withBackContent(content);
        return this;
    }

    public FlashcardVertexBuilder withBackText(String backText, LanguageVertex language) {
        var content = new TextContentVertexBuilder().withValue(backText).withLanguage(language);
        withBackContent(content);
        return this;
    }

    public FlashcardVertexBuilder withDefaultFrontLanguage(LanguageVertex languageVertex) {
        this.defaultFrontLanguage = languageVertex;
        return this;
    }

    public FlashcardVertexBuilder withDefaultBackLanguage(LanguageVertex languageVertex) {
        this.defaultBackLanguage = languageVertex;
        return this;
    }

    public FlashcardVertexBuilder withDefaultLanguages(LanguageVertex frontLanguage, LanguageVertex backLanguage) {
        withDefaultFrontLanguage(frontLanguage);
        withDefaultBackLanguage(backLanguage);
        return this;
    }

    @Override
    public void injectDefaultFrontLanguage(LanguageVertex languageVertex) {
        this.defaultFrontLanguage = languageVertex;
    }

    @Override
    public void injectDefaultBackLanguage(LanguageVertex languageVertex) {
        this.defaultBackLanguage = languageVertex;
    }

    @Override
    public FlashcardVertex build(GraphTraversalSource traversalSource) {
        if (this.factories == null)
            throw new RuntimeException("Factories were not injected");
        if (this.frontContent == null && this.frontContentBuilder == null)
            throw new RuntimeException("Front content was not provided");
        if (this.backContent == null && this.backContentBuilder == null)
            throw new RuntimeException("Back content was not provided");

        var id = (this.id != null) ? this.id : java.util.UUID.randomUUID().toString();

        ContentVertex frontContent;
        if (this.frontContentBuilder != null) {
            if (this.frontContentBuilder instanceof VertexBuilderFactoriesInjectable injectable1)
                injectable1.injectFactories(this.factories);
            if (this.frontContentBuilder instanceof DefaultLanguageInjectable injectable2)
                injectable2.injectDefaultLanguage(this.defaultFrontLanguage);

            frontContent = this.frontContentBuilder.build(traversalSource);
        }
        else frontContent = this.frontContent;

        ContentVertex backContent;
        if (this.backContentBuilder != null) {
            if (this.backContentBuilder instanceof VertexBuilderFactoriesInjectable injectable1)
                injectable1.injectFactories(this.factories);
            if (this.backContentBuilder instanceof DefaultLanguageInjectable injectable2)
                injectable2.injectDefaultLanguage(this.defaultBackLanguage);
            backContent = this.backContentBuilder.build(traversalSource);
        }
        else backContent = this.backContent;

        var createCommand = new CreateFlashcardVertexCommand();
        createCommand.setId(id);
        createCommand.setLeftContentId(frontContent.getId());
        createCommand.setRightContentId(backContent.getId());
        factories.createFlashcardVertexCommandHandler.handle(createCommand);
        return FlashcardVertex.findById(traversalSource, id);
    }
}
