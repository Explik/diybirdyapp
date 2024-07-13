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
    public void add(FlashcardModel flashcardModel) { }

    @Override
    public FlashcardModel get(String id) {
        return null;
    }

    @Override
    public List<FlashcardModel> getAll() {
        var vertices = framedGraph
            .traverse(g -> g.V().hasLabel("flashcard"))
            .toListExplicit(FlashcardVertex.class);

        return vertices
            .stream()
            .map(v -> {
                var leftContent = v.getLeftContent();
                var rightContent = v.getRightContent();
                var leftLanguage = leftContent.getLanguage();
                var rightLanguage = rightContent.getLanguage();

                return new FlashcardModel(
                    leftContent.getValue(),
                    new LanguageModel(
                        leftLanguage.getAbbreviation(),
                        leftLanguage.getName()),
                    rightContent.getValue(),
                    new LanguageModel(
                        rightLanguage.getAbbreviation(),
                        rightLanguage.getName()));
            })
            .toList();
    }
}
