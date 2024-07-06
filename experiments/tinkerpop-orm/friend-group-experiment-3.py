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
class Person: 
    def __init__(self, vertex) -> None:
        self.id: int = vertex['id']
        self.name: str = vertex['name']
        self.age: int = vertex['age']
        self.city: str = vertex['city']
        self.friend_ids = vertex['knows_id']


class FriendsRepository: 
    def __init__(self, graph) -> None:
        self.graph = graph
    
    def get_friend_group(self, name: str) -> list[Person]: 
        vertices = self.graph.V().has('person', 'name', name).\
            out('knows').aggregate('friends'). \
            project('id', 'name', 'age', 'city', 'knows_id'). \
                by('id'). \
                by('name'). \
                by('age'). \
                by('city'). \
                by(__.out('knows').values('id').fold()). \
            toList()
        
        people = [Person(v) for v in vertices]

        return people
        
# Output the results 
repository = FriendsRepository(g)
friend_group = repository.get_friend_group('Charlie')

for person in friend_group: 
    print(f"Person {person.name} (Id: {person.id}, Age: {person.age}, City: {person.city}) knows: {person.friend_ids}")

# Close the connection
remoteConn.close()

