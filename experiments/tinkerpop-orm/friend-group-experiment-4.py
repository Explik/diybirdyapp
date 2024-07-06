# Import necessary libraries
from typing import Callable
from gremlin_python.structure.graph import Graph
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process.graph_traversal import __
from gremlin_python.process.traversal import T

# Connect to the Gremlin Server
graph = Graph()
remoteConn = DriverRemoteConnection('ws://localhost:8182/gremlin', 'g')
g = graph.traversal().withRemote(remoteConn)

# Drop the existing graph (if any)
g.V().drop().iterate()

# Example graph setup with additional attributes for each person
g.addV('person').property("id", 0).property('name', 'Charlie').property('age', 30).property('city', 'New York').as_('charlie'). \
  addV('person').property("id", 1).property('name', 'Alice').property('age', 28).property('city', 'San Francisco').as_('alice'). \
  addV('person').property("id", 2).property('name', 'Bob').property('age', 32).property('city', 'Los Angeles').as_('bob'). \
  addV('person').property("id", 3).property('name', 'Eve').property('age', 25).property('city', 'Seattle').as_('eve'). \
  addE('knows').from_('charlie').to('alice'). \
  addE('knows').from_('charlie').to('bob'). \
  addE('knows').from_('alice').to('bob'). \
  addE('knows').from_('eve').to('alice'). \
  addE('knows').from_('bob').to('alice'). \
  addE('knows').from_('bob').to('eve'). \
  iterate()

# Setup classes 
class FramedGraph: 
    def __init__(self, graph, projections: dict[str, Callable], mappings: dict[str, Callable]) -> None:
        self.graph = graph
        self.projections = projections
        self.mappings = mappings

        self.traversal = None

    def traverse(self, traversal):
        self.traversal = traversal
        return self

    def next(self, type_name: str): 
        if type_name not in self.projections: 
            raise "Unable to project type " + type_name
        if type_name not in self.mappings: 
            raise "Unable to map type " + type_name

        traversed_vertices = self.traversal(self.graph)
        projected_vertex = self.projections[type_name](traversed_vertices).next()
        return self.mappings[type_name](projected_vertex)
        

    def to_list(self, type_name): 
        if type_name not in self.projections: 
            raise "Unable to project type " + type_name
        if type_name not in self.mappings: 
            raise "Unable to map type " + type_name
        
        traversed_vertices = self.traversal(self.graph)
        projected_vertices = self.projections[type_name](traversed_vertices).toList()
        return [self.mappings[type_name](v) for v in projected_vertices]

    
class Person: 
    def __init__(self: int, id: int, name: str, age: int, city: str, knows_id: list[int]) -> None:
        self.id = id
        self.name = name
        self.age = age
        self.city = city
        self.friend_ids = knows_id

class PersonRepository: 
    def __init__(self, graph) -> None:
        self.framed_graph = FramedGraph(
            graph, 
            { "Person": PersonRepository.project_person_vertices },
            { "Person": PersonRepository.map_person_vertex })
    
    def get_person(self, name: str) -> Person: 
        return self.framed_graph.traverse(
            lambda g: g.V().has('person', 'name', name)
        ).next('Person')

    def get_friend_group_of(self, name: str) -> list[Person]:
        return self.framed_graph.traverse(
            lambda g: g.V().has('person', 'name', name).out('knows').aggregate('friends')
        ).to_list('Person')

    def project_person_vertices(traversal): 
        return traversal \
            .project('id', 'name', 'age', 'city', 'knows_id'). \
                by('id'). \
                by('name'). \
                by('age'). \
                by('city'). \
                by(__.out('knows').values('id').fold())

    def map_person_vertex(vertex):
        return Person(
            vertex['id'], 
            vertex['name'], 
            vertex['age'], 
            vertex['city'], 
            vertex['knows_id'])
        
# Output the results 
repository = PersonRepository(g)
charlie = repository.get_person('Charlie')
charlie_friends = repository.get_friend_group_of('Charlie')

def print_friend(person): 
    print(f"Person {person.name} (Id: {person.id}, Age: {person.age}, City: {person.city}) knows: {person.friend_ids}")

print_friend(charlie)
print("has friends")
for member in charlie_friends: 
    print_friend(member)

# Close the connection
remoteConn.close()

