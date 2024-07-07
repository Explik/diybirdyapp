# Import necessary libraries
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
    def __init__(self, graph, config) -> None:
        self.graph = graph
        self.config = config

        self.traversal = None

    def traverse(self, traversal):
        self.traversal = traversal
        return self

    def next(self, type_name: str): 
        if type_name not in self.config: 
            raise "Unrecognized type " + type_name

        traversed_vertices = self.traversal(self.graph)
        projected_vertex = self.project(type_name, traversed_vertices).next()
        return self.map(type_name, projected_vertex)
        

    def to_list(self, type_name): 
        if type_name not in self.config: 
            raise "Unrecognized type " + type_name
        
        traversed_vertices = self.traversal(self.graph)
        projected_vertices = self.project(type_name, traversed_vertices).toList()
        return [self.map(type_name, v) for v in projected_vertices]
    
    def project(self, type_name, traversal): 
        type_config = self.config[type_name]
        projection_names = [f"p{i}" for i in range(0, len(type_config))]
        property_values = [p[0] for p in type_config]

        projection = traversal.project(*projection_names)
        for property_value in property_values:
            projection = projection.by(property_value)

        return projection

    def map(self, type_name, vertex):
        type_config = self.config[type_name]
        projection_names = [f"p{i}" for i in range(0, len(type_config))]
        set_methods = [p[2] for p in type_config]

        instance = globals().get(type_name)()
        for i in range(0, len(type_config)):
            projection_name = projection_names[i]
            set_methods[i](instance, vertex[projection_name])
        
        return instance

    
class Person: 
    def __init__(self: int) -> None:
        self.id: int = None
        self.name: str = None
        self.age: int = None
        self.city: str = None
        self.friend_ids: list[int] = None

    def get_id(self):
        return self.id 
    
    def set_id(self, id): 
        self.id = id

    def get_name(self):
        return self.name 
    
    def set_name(self, name): 
        self.name = name
    
    def get_age(self):
        return self.age 
    
    def set_age(self, age): 
        self.age = age

    def get_city(self):
        return self.city 
    
    def set_city(self, city): 
        self.city = city
    
    def get_knows_id(self):
        return self.friend_ids 
    
    def set_knows_id(self, knows_id): 
        self.friend_ids = knows_id

class PersonRepository: 
    def __init__(self, graph) -> None:
        self.framed_graph = FramedGraph(
            graph, 
            {
                "Person": [
                    ["id", Person.get_id, Person.set_id],
                    ["name", Person.get_name, Person.set_name],
                    ["age", Person.get_age, Person.set_age],
                    ["city", Person.get_city, Person.set_city],
                    [__.out('knows').values('id').fold(), Person.get_knows_id, Person.set_knows_id]
                ]
            })
    
    def get_person(self, name: str) -> Person: 
        return self.framed_graph.traverse(
            lambda g: g.V().has('person', 'name', name)
        ).next('Person')

    def get_friend_group_of(self, name: str) -> list[Person]:
        return self.framed_graph.traverse(
            lambda g: g.V().has('person', 'name', name).out('knows').aggregate('friends')
        ).to_list('Person')

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

