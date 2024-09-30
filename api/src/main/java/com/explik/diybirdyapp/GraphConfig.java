package com.explik.diybirdyapp;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.ser.Serializers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.tinkerpop.gremlin.driver.Cluster;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Configuration
public class GraphConfig {
    @Bean
    public GraphTraversalSource traversalSource() {
        Cluster cluster = Cluster
                .build("localhost")
                .protocol("websocket")
                .port(8182)
                .serializer(Serializers.GRAPHSON_V3)
                .create();
        return AnonymousTraversalSource.traversal().withRemote(DriverRemoteConnection.using(cluster, "g"));
    }
}