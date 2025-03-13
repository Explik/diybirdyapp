package com.explik.diybirdyapp.persistence.vertex;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class PairVertex extends AbstractVertex {
    public PairVertex(AbstractVertex vertex) {
        super(vertex.getUnderlyingSource(), vertex.getUnderlyingVertex());
    }

    public PairVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public final static String LABEL = "pair";

    public final static String EDGE_LEFT = "hasLeft";
    public final static String EDGE_RIGHT = "hasRight";

    public final static String PROPERTY_ID = "id";

    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    public void setId(String id) {
        setProperty(PROPERTY_ID, id);
    }

    public ContentVertex getLeftContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_LEFT, VertexHelper::createContent);
    }

    public void setLeftContent(AbstractVertex vertex) {
        addEdgeOneToOne(EDGE_LEFT, vertex);
    }

    public ContentVertex getRightContent() {
        return VertexHelper.getOutgoingModel(this, EDGE_RIGHT, VertexHelper::createContent);
    }

    public void setRightContent(AbstractVertex vertex) {
        addEdgeOneToOne(EDGE_RIGHT, vertex);
    }

    public static PairVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV(LABEL).next();
        return new PairVertex(traversalSource, vertex);
    }
}
