package com.explik.diybirdyapp.persistence.vertexFactory.parameter;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;

import java.util.ArrayList;
import java.util.List;

public class ExerciseInputParametersPairOptions {
    private List<Pair> pairs = new ArrayList<>();

    public List<List<ContentVertex>> getPairs() {
        return pairs.stream().map(pair -> List.of(pair.left, pair.right)).toList();
    }

    public void setPairs(List<List<ContentVertex>> pairs) {
        this.pairs = new ArrayList<>();
        for (List<ContentVertex> pair : pairs) {
            this.pairs.add(new Pair(pair.get(0), pair.get(1)));
        }
    }

    public ExerciseInputParametersPairOptions withPairs(List<List<ContentVertex>> pairs) {
        setPairs(pairs);
        return this;
    }

    record Pair(ContentVertex left, ContentVertex right) { }
}
