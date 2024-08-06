package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.graph.model.LanguageModel;
import com.explik.diybirdyapp.graph.vertex.FlashcardVertex;
import com.explik.diybirdyapp.graph.vertex.LanguageVertex;
import com.explik.diybirdyapp.graph.vertex.TextContentVertex;
import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.ReflectionCache;
import com.syncleus.ferma.framefactories.annotation.AnnotationFrameFactory;
import com.syncleus.ferma.typeresolvers.PolymorphicTypeResolver;
import com.syncleus.ferma.typeresolvers.UntypedTypeResolver;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FlashcardRepositoryImpl implements FlashcardRepository {
    private final FramedGraph framedGraph;

    public FlashcardRepositoryImpl(@Autowired TinkerGraph graph) {
        var vertexTypes = List.of(
            FlashcardVertex.class,
            LanguageVertex.class,
            TextContentVertex.class);
        framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);
    }

    @Override
    public FlashcardModel add(FlashcardModel flashcardModel) {
        // Binds flashcard to existing languages
        if (flashcardModel.getLeftLanguage() == null || flashcardModel.getLeftLanguage().getId() == null)
            throw new IllegalArgumentException("leftLanguage is missing");
        if (flashcardModel.getRightLanguage() == null || flashcardModel.getRightLanguage().getId() == null)
            throw new IllegalArgumentException("rightLanguage is missing");

        var languageVertex1 = framedGraph
            .traverse(g -> g.V().has("language", "id", flashcardModel.getLeftLanguage().getId()))
            .nextExplicit(LanguageVertex.class);

        var languageVertex2 = framedGraph
            .traverse(g -> g.V().has("language", "id", flashcardModel.getRightLanguage().getId()))
            .nextExplicit(LanguageVertex.class);

        // Binds flashcard content
        var textContentVertex1 = framedGraph.addFramedVertex(TextContentVertex.class, T.label, "textContent");
        textContentVertex1.setLanguage(languageVertex1);

        var textContentVertex2 = framedGraph.addFramedVertex(TextContentVertex.class, T.label, "textContent");
        textContentVertex2.setLanguage(languageVertex2);

        var flashcardVertex = framedGraph.addFramedVertex(FlashcardVertex.class, T.label, "flashcard");
        flashcardVertex.setLeftContent(textContentVertex1);
        flashcardVertex.setRightContent(textContentVertex2);

        return create(flashcardVertex);
    }

    @Override
    public FlashcardModel get(String id) {
        var vertex = framedGraph
            .traverse(g -> g.V().has("flashcard", "id", id))
            .nextExplicit(FlashcardVertex.class);
        return create(vertex);
    }

    @Override
    public List<FlashcardModel> getAll() {
        var vertices = framedGraph
            .traverse(g -> g.V().hasLabel("flashcard"))
            .toListExplicit(FlashcardVertex.class);

        return vertices
            .stream()
            .map(FlashcardRepositoryImpl::create)
            .toList();
    }

    private static FlashcardModel create(FlashcardVertex v) {
            var leftContent = v.getLeftContent();
            var rightContent = v.getRightContent();
            var leftLanguage = leftContent.getLanguage();
            var rightLanguage = rightContent.getLanguage();

            return new FlashcardModel(
                v.getId(),
                leftContent.getValue(),
                new LanguageModel(
                    leftLanguage.getId(),
                    leftLanguage.getAbbreviation(),
                    leftLanguage.getName()),
                rightContent.getValue(),
                new LanguageModel(
                    rightLanguage.getId(),
                    rightLanguage.getAbbreviation(),
                    rightLanguage.getName()));
    }
}
