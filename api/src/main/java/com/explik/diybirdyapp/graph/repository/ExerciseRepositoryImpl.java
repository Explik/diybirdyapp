package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.vertex.ExerciseVertex;
import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExerciseRepositoryImpl implements ExerciseRepository {
    @Autowired
    private ExerciseFactory exerciseFactory;

    private final FramedGraph framedGraph;

    public ExerciseRepositoryImpl(@Autowired TinkerGraph graph) {
        var vertexTypes = List.of(
            ExerciseVertex.class);
        framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);
    }

    @Override
    public ExerciseModel get(String id) {
        var vertex = framedGraph
            .traverse(g -> g.V().has("exercise", "id", id))
            .next(ExerciseVertex.class);

        return exerciseFactory.create(vertex);
    }

    @Override
    public List<ExerciseModel> getAll() {
        var vertices = framedGraph
            .traverse(g -> g.V().hasLabel("exercise"))
            .toListExplicit(ExerciseVertex.class);

        return vertices
                .stream()
                .map(exerciseFactory::createLimited)
                .toList();
    }
}
