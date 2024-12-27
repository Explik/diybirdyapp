## Backend Architecture Overview

## Introduction
The backend consists of the following layers: 
- Controllers
- Services
- Repositories
- Operations 
- VertexFactories
- Vertices

The controllers are responsible for translating DTOs to models and vice versa. 
The services are responsible for handling the business logic.
The repositories are responsible for handling persistance in the graph.
The operations are responsible for handling the graph operations per data subtype.
The vertex factories are responsible for creating vertices per data subtype.
The vertices are responsible for handling the graph representation of the data per data subtype.