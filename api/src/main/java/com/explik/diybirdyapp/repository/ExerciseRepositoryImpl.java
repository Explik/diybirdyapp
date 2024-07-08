package com.explik.diybirdyapp.repository;

import com.explik.diybirdyapp.model.Exercise;
import com.explik.diybirdyapp.model.TypeHelper;
import com.explik.diybirdyapp.model.vertex.ExerciseLightVertex;
import com.explik.diybirdyapp.model.vertex.ExerciseVertex;
import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class ExerciseRepositoryImpl implements ExerciseRepository {
    private final ModelMapper modelMapper;
    private final List<Class<? extends AbstractVertexFrame>> vertexTypes;

    @Autowired
    public TinkerGraph graph;

    public ExerciseRepositoryImpl() {
        modelMapper = new ModelMapper();
        vertexTypes = TypeHelper.getVertexTypes();
    }

    @Override
    public void add(Exercise exercise) {
        var discriminator = exercise.getExerciseType();
        var vertexType = TypeHelper.findByDiscriminator(vertexTypes, discriminator);

        FramedGraph framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);
        ExerciseVertex vertex = (ExerciseVertex)framedGraph.addFramedVertex(
            vertexType,
            T.label, "exercise",
            "exerciseType", discriminator);

        modelMapper.map(exercise, vertex);
    }

    @Override
    public Exercise getById(String id) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FramedGraph framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);

        return framedGraph
            .traverse(g -> g.V().has("exercise", "id", id))
            .next(ExerciseVertex.class);
    }

    @Override
    public List<Exercise> getAll() {
        FramedGraph framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);
        List<? extends ExerciseVertex> framedVertices = framedGraph
            .traverse(g -> g.V().hasLabel("exercise"))
            .toListExplicit(ExerciseVertex.class);

        return framedVertices
            .stream()
            .map(i -> (Exercise)i)
            .toList();
    }
}
