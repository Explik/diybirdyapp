import streamlit as st
import requests
import zipfile
import tempfile
import os
import shared.import_client as import_client
from shared.anki import AnkiDeck
from shared.utils import strip_prefix_suffix
from shared.converter_utils import create_audio_content_upload, create_flashcard, create_flashcard_deck, create_language_query, create_text_content

def establish_connection():
    if server_url:
        try:
            response = requests.get(f"{server_url}/language")
            if response.status_code == 200:
                languages = response.json()

                if languages is None or len(languages) == 0:
                    st.session_state.connection_status = "warning: No languages available on the server."
                    return

                st.session_state.languages = response.json()  # Assuming the response is a JSON list of languages
                st.session_state.connection_status = "success"
            else:
                st.session_state.connection_status = f"error: {response.status_code}"
        except requests.exceptions.RequestException as e:
            st.session_state.connection_status = f"error: {e}"
    else:
        st.session_state.connection_status = "warning: Please enter a valid server URL."

def format_field_values(anki_file, field_name, max_length=100):
    iterator = (card.get_raw_value(field_name).replace(".", "").replace("?", " ").replace("\n", " ").replace("<br>", " ") for card in anki_file.get_flashcards())

    buffer = set()
    buffer_length = 0

    for item in iterator: 
        if len(item) == 0: 
            continue
        if buffer_length > max_length:
            break

        buffer_length += len(item) + 2  # +2 for ", " or "& "
        buffer.add(item)
    
    if len(buffer) == 0:
        return "No examples available"

    buffer = list(buffer)
    result = ", ".join(buffer[:-1])
    result += " & " + buffer[-1] if len(buffer) > 1 else buffer[0]
    if len(result) > max_length:
        result = result[:max_length - 3] + "..."

    return result

# Set the title of the app
st.title("Import ANKI Flashcards")

# Setup section configure server in the sidebar
st.subheader("Configure Server")

# Add an input field for the URL in the sidebar
server_url = st.text_input("Server URL", placeholder="Enter the server URL")

if "connection_status" not in st.session_state:
    st.session_state.connection_status = ""

if st.button("Establish Connection"):
    establish_connection()

# Display the connection status below the button
if st.session_state.connection_status:
    if st.session_state.connection_status == "success":
        st.success("Connection successful!")
    elif st.session_state.connection_status.startswith("error"):
        st.error(f"Connection failed: {st.session_state.connection_status.split(': ', 1)[1]}")
    elif st.session_state.connection_status.startswith("warning"):
        st.sidebar.warning(st.session_state.connection_status.split(': ', 1)[1])

anki_file = None
uploaded_file = None

if st.session_state.connection_status == "success":
    st.subheader("Configure conversion")
    uploaded_file = st.file_uploader("Upload ANKI file", type=["apkg"])

if uploaded_file is not None:
    # Save file to a temporary directory
    with tempfile.TemporaryDirectory() as temp_dir:
        file_path = os.path.join(temp_dir, uploaded_file.name)
        with open(file_path, "wb") as f:
            f.write(uploaded_file.getbuffer())

        # Create ANKI file 
        try:
            anki_file = AnkiDeck.create_from_file(file_path)
            st.success(f"Successfully loaded {anki_file.get_number_of_flashcards()} flashcard(s)!")
        except Exception as e:
            st.error(f"Failed to load file: {e}")

# Fetch languages from session state if available
languages = st.session_state.get("languages", [])

if anki_file is not None and len(languages) > 0: 
    all_fields = [None] + anki_file.get_field_names()
    sound_fields =  [None] + anki_file.get_sound_field_names()

    # Section: Front Side
    cols = st.columns(2)
    with cols[0]:
        front_language = st.selectbox(
            "Select front language",
            options=[{"id": lang["id"], "name": lang["name"]} for lang in languages],
            format_func=lambda lang: lang["name"] if lang else "None",
            key="front_language"
        )

        front_field = st.selectbox("Select text field", key="front_text_field", options=all_fields)
        if front_field:
            front_field_string = format_field_values(anki_file, front_field, 50)
            st.write(f"Examples: **{front_field_string}**")

        front_pronounciation_field = st.selectbox("Select pronunciation field", key="front_pronounciation_field", options=sound_fields)
        if front_pronounciation_field:
            front_pronounciation_field_string = format_field_values(anki_file, front_pronounciation_field, 50)
            st.write(f"Examples: **{front_pronounciation_field_string}**")

    with cols[1]:
        back_language = st.selectbox(
            "Select back language",
            options=[{"id": lang["id"], "name": lang["name"]} for lang in languages],
            format_func=lambda lang: lang["name"] if lang else "None",
            key="back_language"
        )

        back_field = st.selectbox("Select text field", key="back_text_field", options=all_fields)
        if back_field:
            back_field_string = format_field_values(anki_file, back_field, 50)
            st.write(f"Examples: **{back_field_string}**")

        back_pronounciation_field = st.selectbox("Select pronunciation field", key="back_pronounciation_field", options=sound_fields)
        if back_pronounciation_field:
            back_pronounciation_field_string = format_field_values(anki_file, back_pronounciation_field, 50)
            st.write(f"Examples: **{back_pronounciation_field_string}**")

    # Section: Preview 
    st.subheader("Preview conversion")
    # Add a slider to select the preview index
    preview_index = st.number_input("Preview Index", min_value=0, max_value=anki_file.get_number_of_flashcards() - 1, step=1, value=0)
    card = anki_file.get_flashcards()[preview_index]

    # Fetch the corresponding front and back values
    front_value = card.get_raw_value(front_field) if front_field else "N/A"
    back_value = card.get_raw_value(back_field) if back_field else "N/A"

    cols = st.columns(2, border=True)
    with cols[0]:
        st.write("*Front Side*")
        st.write(f"**{front_value}**")
        if front_pronounciation_field:
            try: 
                st.audio(card.get_media_path(front_pronounciation_field))
            except Exception as e:
                st.error(f"Failed to load audio: {e}")
    
    with cols[1]:
        st.write("*Back side*")
        st.write(f"**{back_value}**")
        if back_pronounciation_field:
            try: 
                st.audio(card.get_media_path(back_pronounciation_field))
            except Exception as e:
                st.error(f"Failed to load audio: {e}")

    convert_button = st.button("Convert Flashcards")
    if convert_button:
        # Convert flashcards to the desired format
        flashcards = []
        
        for flashcard in anki_file.get_flashcards(): 
            try: 
                # Create text-text flashcard
                front_text = flashcard.get_text_value(front_field)
                back_text = flashcard.get_text_value(back_field)

                text_flashcard = create_flashcard(
                    create_text_content(front_text, front_language), 
                    create_text_content(back_text, back_language))
                flashcards.append(text_flashcard)

            except Exception as e:
                    st.warning("Failed to create flashcard: ", flashcard, e)

        deck_name = uploaded_file.name.rstrip(".apkg")
        deck_description = f"Converted from {uploaded_file.name}"
        flashcard_deck = create_flashcard_deck(
            deck_name, 
            deck_description, 
            flashcards)
        
        deck = import_client.create_flashcard_deck(deck_name, deck_description)
        for flashcard in flashcards: 
            try: 
                import_client.create_flashcard(deck, flashcard)
            except Exception as e:
                st.warning("Failed to create flashcard: ", flashcard, e)

        print(flashcard_deck)

        # TODO send to server
        

