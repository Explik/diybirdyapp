## Backend Architecture Overview

## Introduction
The backend consists of the following layers: 
- Controllers
- Services
- Repositories
- Repository operations
- Commands / Command Handlers
- VertexFactories
- Vertices
- ModelFactories

The controllers are responsible for translating DTOs to models and vice versa. 
The services are responsible for handling the business logic.
The repositories are responsible for handling persistance in the graph.
The repository operations are responsible for handling the graph operations per data subtype.
The commands/command handlers are responsible for handling one-off operations on the graph.
The vertex factories are responsible for creating vertices per data subtype.
The vertices are responsible for handling the graph representation of the data per data subtype.
The model factories are responsible for creating models from the graph representation of the data.