package com.explik.diybirdyapp;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.finalization.ReferenceElementStrategy;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.tinkerpop.gremlin.driver.Cluster;


@Configuration
public class GraphConfig {
    private static final Logger logger = LoggerFactory.getLogger(GraphConfig.class);

    @Value("${graph.database.type:default}")
    private String databaseType;

    // CosmosDB configuration
    @Value("${cosmosdb.endpoint:}")
    private String cosmosEndpoint;

    @Value("${cosmosdb.key:}")
    private String cosmosKey;

    @Value("${cosmosdb.database:}")
    private String cosmosDatabase;

    @Value("${cosmosdb.container:}")
    private String cosmosContainer;

    // Tinkerpop configuration
    @Value("${tinkerpop.server.host:}")
    private String tinkerPopHost;

    @Value("${tinkerpop.server.protocol:}")
    private String tinkerPopProtocol;

    @Value("${tinkerpop.server.port:}")
    private Integer tinkerPopPort;

    @Bean
    public GraphTraversalSource traversalSource() {
        boolean isDefault = databaseType.equalsIgnoreCase("default");
        boolean isMemory = databaseType.equalsIgnoreCase("memory") || isDefault;
        boolean isTinkerPop = databaseType.equalsIgnoreCase("tinkerpop");

        if (isMemory) {
            if (isDefault)
                logger.warn("Database type is not specified. Defaulting to in-memory database.");

            logger.debug("Creating in-memory source");
            return createInMemorySource();
        }
        else if (isTinkerPop) {
            logger.debug("Creating TinkerPop source");
            return createTinkerPopSource();
        } else throw new IllegalArgumentException("Unsupported database type: " + databaseType);
    }

    private GraphTraversalSource createInMemorySource() {
        return TinkerGraph.open().traversal();
    }

    private GraphTraversalSource createTinkerPopSource() {
        if (tinkerPopHost.isEmpty())
            throw new IllegalArgumentException("TinkerPop host is not specified");
        if (tinkerPopProtocol.isEmpty())
            throw new IllegalArgumentException("TinkerPop protocol is not specified");
        if (tinkerPopPort == null)
            throw new IllegalArgumentException("TinkerPop port is not specified");

        Cluster cluster = Cluster
                .build(tinkerPopHost)
                .protocol(tinkerPopProtocol)
                .port(tinkerPopPort)
                .serializer(Serializers.GRAPHSON_V3)
                .create();

        return AnonymousTraversalSource
                .traversal()
                .withRemote(DriverRemoteConnection.using(cluster, "g"));
    }
}