# Change guide
This document describes how to make common changes to the application. 

## Vertex changes 
### Add new vertex
1. Add new vertex model in persistence.vertex package
    - If the vertex is content, then derive from ContentVertex
    - Otherwise, derive from AbstractVertex
2. Add static final field LABEL to vertex model
3. Add static method Create(GraphTraversalSource g) to vertex model
4. Add test for vertex model

### Add new property to vertex
1. Add static final field PROPERTY_[...] to vertex model
2. Add getter and setter for property to vertex model
    - Use getProperty() and setProperty() methods from AbstractVertex in implementation
3. Add test for set/get property to existing test for vertex model

# Add new edge to vertex
1. Add static final field EDGE_[...] to vertex model
    - If the edge has a property, add static final field EDGE_[...]_PROPERTY_[...] to vertex model as well 
2. Add addEdge() method to vertex model
    - If one-to-one edge, use naming set____() and use the addEdgeOneToOne() method from AbstractVertex in implementation
    - If unordered one-to-many edge, use naming add____() use addEdgeOneToMany() method from AbstractVertex in implementation
    - if ordered one-to-many edge, use naming add_____() and addEdgeOrderedOneToMany() method from AbstractVertex in implementation 
3. Add removeEdge() method to vertex model 
    - If one-to-one edge, use set____(null) instead of implementing a removeEdge() method
    - If one-to-many edge, use naming remove____() and use removeEdgeOneToMany() method from AbstractVertex in implementation
4. Add getEdges() method to vertex model (if needed)
    - Use VertexHelper methods in implementation

## Exercise changes
### Add new exercise type 
1. Add new exercise content component in web component
2. Register component in ExerciseComponentService in web component 
3. Add new exercise type to ExerciseTypes in backend component
4. Add new vertex factory on backend component
5. Add new vertex to model on backend component 
6. Add a sample of the exercise in DataInitializer in backend component

## I18n changes
### Generate translation source file
1. Update angular.json to include i18n configuration if not already present
2. Run `ng extract-i18n --output-path src/locale` to generate the source translation file (messages.xlf)
