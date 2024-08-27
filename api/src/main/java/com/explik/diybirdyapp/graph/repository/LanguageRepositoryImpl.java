package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.graph.vertex.FlashcardLanguageVertex;
import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageRepositoryImpl implements LanguageRepository {
    private final FramedGraph framedGraph;

    public LanguageRepositoryImpl(@Autowired TinkerGraph graph) {
        var vertexTypes = List.of(FlashcardLanguageVertex.class);
        framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);
    }

    @Override
    public List<FlashcardLanguageModel> getAll() {
        var vertices = framedGraph
            .traverse(g -> g.V().hasLabel("language"))
            .toListExplicit(FlashcardLanguageVertex.class);

        return vertices
            .stream()
            .map(LanguageRepositoryImpl::create)
            .toList();
    }

    private static FlashcardLanguageModel create(FlashcardLanguageVertex v) {
        return new FlashcardLanguageModel(
            v.getId(),
            v.getAbbreviation(),
            v.getName());
    }
}
