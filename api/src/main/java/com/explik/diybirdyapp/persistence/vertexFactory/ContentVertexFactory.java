package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;

public interface ContentVertexFactory<T extends ContentVertex, TOptions> extends VertexFactory<T, TOptions> {
    T copy(T existingVertex);
}
