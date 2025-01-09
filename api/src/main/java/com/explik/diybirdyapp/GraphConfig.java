package com.explik.diybirdyapp;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.ser.Serializers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.tinkerpop.gremlin.driver.Cluster;


@Configuration
public class GraphConfig {
    @Value("${gremlin.server.host}")
    private String host;

    @Value("${gremlin.server.protocol}")
    private String protocol;

    @Value("${gremlin.server.port}")
    private int port;

    @Bean
    public GraphTraversalSource traversalSource() {
        Cluster cluster = Cluster
                .build(host)
                .protocol(protocol)
                .port(port)
                .serializer(Serializers.GRAPHSON_V3)
                .create();
        return AnonymousTraversalSource.traversal().withRemote(DriverRemoteConnection.using(cluster, "g"));
    }
}