import uuid
from gremlin_python.structure.graph import Graph
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process.graph_traversal import __
from gremlin_python.process.traversal import T

def open_connection(): 
    return DriverRemoteConnection('ws://localhost:8182/gremlin', 'g')

def close_connection(connection):
    assert connection is not None
    connection.close()

def get_traversal_source(connection):
    assert connection is not None
    return Graph().traversal().withRemote(connection)

def add_language(traversal_source, name, abbreviation): 
    assert traversal_source is not None
    assert name is not None
    assert abbreviation is not None

    return traversal_source.addV('language') \
        .property('id', str(uuid.uuid4())) \
        .property('name', name) \
        .property('abbreviation', abbreviation) \
        .next()

def add_text_content(traversal_source, language_vertex, value): 
    assert traversal_source is not None
    assert language_vertex is not None
    assert value is not None

    vertex = traversal_source.addV('text_content') \
        .property('id', str(uuid.uuid4())) \
        .property('value', value) \
        .next()
    
    traversal_source.V(vertex) \
        .addE('hasLanguage').to(language_vertex) \
        .iterate() 

    return vertex
        
def add_flashcard(traversal_source, content_vertex_1, content_vertex_2): 
    assert traversal_source is not None
    assert content_vertex_1 is not None
    assert content_vertex_2 is not None

    vertex = traversal_source.addV('flashcard') \
        .property('id', str(uuid.uuid4())) \
        .next()
    
    traversal_source.V(vertex).as_('flashcard') \
        .addE('hasLeftContent').from_('flashcard').to(content_vertex_1) \
        .addE('hasRightContent').from_('flashcard').to(content_vertex_2) \
        .iterate()

    return vertex