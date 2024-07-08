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
g.addV('person').property('name', 'Charlie').property('age', 30).property('city', 'New York').as_('charlie'). \
  addV('person').property('name', 'Alice').property('age', 28).property('city', 'San Francisco').as_('alice'). \
  addV('person').property('name', 'Bob').property('age', 32).property('city', 'Los Angeles').as_('bob'). \
  addV('person').property('name', 'Eve').property('age', 25).property('city', 'Seattle').as_('eve'). \
  addE('knows').from_('charlie').to('alice'). \
  addE('knows').from_('charlie').to('bob'). \
  addE('knows').from_('alice').to('bob'). \
  addE('knows').from_('eve').to('alice'). \
  iterate()

# Query to find all friends in Charlie's friend group and document who knows whom, including IDs
friendGroup = g.V().has('person', 'name', 'Charlie').as_('charlie').out('knows').as_('friend'). \
    group(). \
        by(T.id). \
        by(__.project('name', 'age', 'city', 'knows_whom', 'friend_of_charlie'). \
            by('name'). \
            by('age'). \
            by('city'). \
            by(__.out('knows').values('name').fold()). \
            by(__.select('charlie').values('name')). \
        fold()). \
    toList()

# Output the results
for id, details in friendGroup[0].items():
    print(f"ID: {id}")
    print(f"Person {details['name']} (Age: {details['age']}, City: {details['city']}) knows: {details['knows_whom']}")
    print(f"Friend of Charlie: {details['friend_of_charlie']}")
    print()

# Close the connection
remoteConn.close()