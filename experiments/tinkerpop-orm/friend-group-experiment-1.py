#########################################################
# Setting up gremlin_python for the first time
#########################################################

# REQUIREMENTS
# Setup gremlin_python (Windows)
# > py -m pip install gremlinpython aiohttp async_timeout
# Setup gremlin server (Windows WSL 2)
# > sudo docker pull tinkerpop/gremlin-server
# > sudo docker run -d -p 8182:8182 --name gremlin-server tinkerpop/gremlin-server

# SCRIPT
# Import necessary libraries
from gremlin_python.structure.graph import Graph
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process.graph_traversal import __
from gremlin_python.process.traversal import T

# Connect to the Gremlin Server
graph = Graph()
remoteConn = DriverRemoteConnection('ws://localhost:8182/gremlin', 'g')
g = graph.traversal().withRemote(remoteConn)

# Create vertices and edges (toy graph)
# Let's create a simple graph with two vertices and one edge

# Drop the existing graph (if any)
g.V().drop().iterate()

# Add vertices
v1 = g.addV('person').property('name', 'Alice').next()
v2 = g.addV('person').property('name', 'Bob').next()

# Add an edge
g.V(v1).addE('knows').to(v2).property('since', 2022).iterate()

# Query the graph
# Fetch all vertices
vertices = g.V().valueMap(True).toList()

# Fetch all edges
edges = g.E().valueMap(True).toList()

# Print vertices and edges
print("Vertices:")
for vertex in vertices:
    print(vertex)

print("\nEdges:")
for edge in edges:
    print(edge)

# Close the connection
remoteConn.close()