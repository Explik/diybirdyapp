## Setup 
Add application-dev.properties file in src/main/resources folder with the following content:
```
gremlin.server.host=localhost
gremlin.server.protocol=websocket
gremlin.server.port=8182
```

## Starting
To start the API, use the following command:
```
mvn spring-boot:run
```

To initially populate graph, call the following endpoint:
```
POST /reset-graph
```