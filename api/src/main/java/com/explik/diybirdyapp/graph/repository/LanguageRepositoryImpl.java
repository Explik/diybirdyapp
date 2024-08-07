package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.graph.model.LanguageModel;
import com.explik.diybirdyapp.graph.vertex.FlashcardVertex;
import com.explik.diybirdyapp.graph.vertex.LanguageVertex;
import com.explik.diybirdyapp.graph.vertex.TextContentVertex;
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
        var vertexTypes = List.of(LanguageVertex.class);
        framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);
    }

    @Override
    public List<LanguageModel> getAll() {
        var vertices = framedGraph
            .traverse(g -> g.V().hasLabel("language"))
            .toListExplicit(LanguageVertex.class);

        return vertices
            .stream()
            .map(LanguageRepositoryImpl::create)
            .toList();
    }

    private static LanguageModel create(LanguageVertex v) {
        return new LanguageModel(
            v.getId(),
            v.getAbbreviation(),
            v.getName());
    }
}
