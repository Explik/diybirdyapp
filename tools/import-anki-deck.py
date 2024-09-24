
from shared.anki import extract_database, get_field_names, get_flashcards
from shared.gremlin import add_flashcard, add_language, add_text_content, close_connection, get_traversal_source, open_connection

# Access ANKI2 DB 
path = "C:\\Users\\dxied\\Downloads\\English_-_Slovak_A1_-_Beginners.apkg"
field_name1 = "Word1"
field_name2 = "Word2"

anki_db = extract_database(path)
field_names = get_field_names(anki_db)

# Validate field names
if field_name1 not in field_names: 
    raise ValueError(f"Field {field_name1} not found in ANKI database")

if field_name2 not in field_names:
    raise ValueError(f"Field {field_name2} not found in ANKI database")

# Extract field name values
flashcards = get_flashcards(anki_db)

for flashchard in flashcards:
    print(f"Left = {flashchard[field_name1]}")
    print(f"Right = {flashchard[field_name2]}")
    print("")

# Add flashcards to graph DB
graph_db_connection = open_connection()
traversal_source = get_traversal_source(graph_db_connection)

for flashcard in flashcards:
    left_content = flashcard[field_name1]
    right_content = flashcard[field_name2]

    left_language_query = traversal_source.V().has('language', 'name', 'English')
    left_language = left_language_query.next() if left_language_query.has_next() else add_language(traversal_source, 'English', 'EN')

    right_language_query = traversal_source.V().has('language', 'name', 'Slovak')
    right_language = right_language_query.next() if right_language_query.has_next() else add_language(traversal_source, 'Slovak', 'SK')

    left_content_vertex = add_text_content(traversal_source, left_language, left_content)
    right_content_vertex = add_text_content(traversal_source, right_language, right_content)

    add_flashcard(traversal_source, left_content_vertex, right_content_vertex)

close_connection(graph_db_connection)