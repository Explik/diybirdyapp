"""
Page: Create deck from .anki file
Import Anki deck files and convert them to the internal storage format.
"""

import streamlit as st
import sys
from pathlib import Path
import tempfile
import os

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))
sys.path.append(str(Path(__file__).parent.parent))

from login_utils import render_login_sidebar
from deck_storage import DeckStorage
from app_utils import show_error, show_success, show_warning, show_info, display_flashcard_preview
from shared.anki import AnkiDeck
from import_client import get_languages

st.set_page_config(page_title="Create Deck from Anki", page_icon="📦", layout="wide")

# Render login sidebar
render_login_sidebar()

st.title("📦 Create Deck from Anki File")
st.markdown("---")

# Initialize session state
if 'anki_deck' not in st.session_state:
    st.session_state.anki_deck = None
if 'deck_name' not in st.session_state:
    st.session_state.deck_name = ""
if 'conversion_complete' not in st.session_state:
    st.session_state.conversion_complete = False
if 'backend_languages' not in st.session_state:
    st.session_state.backend_languages = None

# Helper functions
def format_field_values(anki_deck, field_name, max_length=100):
    """Format field values from Anki deck for preview"""
    iterator = (card.get_raw_value(field_name).replace(".", "").replace("?", " ").replace("\n", " ").replace("<br>", " ") for card in anki_deck.get_flashcards())

    buffer = set()
    buffer_length = 0

    for item in iterator: 
        if len(item) == 0: 
            continue
        if buffer_length > max_length:
            break

        buffer_length += len(item) + 2
        buffer.add(item)
    
    if len(buffer) == 0:
        return "No examples available"

    buffer = list(buffer)
    result = ", ".join(buffer[:-1])
    result += " & " + buffer[-1] if len(buffer) > 1 else buffer[0]
    if len(result) > max_length:
        result = result[:max_length - 3] + "..."

    return result

def create_text_content(text: str, language_id: str = None):
    """Create text content structure"""
    return {
        "type": "Text",
        "text": text,
        "languageId": language_id
    }

def strip_anki_formatting(text: str) -> str:
    """Strip Anki-specific formatting from text"""
    import re
    # Remove HTML tags
    text = re.sub(r'<[^>]+>', '', text)
    # Remove sound tags
    text = re.sub(r'\[sound:[^\]]+\]', '', text)
    # Clean up extra whitespace
    text = ' '.join(text.split())
    return text.strip()

# Step 1: Upload Anki File
st.subheader("1️⃣ Upload Anki Deck File")
uploaded_file = st.file_uploader("Choose an .apkg file", type=["apkg"])

if uploaded_file is not None:
    # Save and load the file
    with tempfile.TemporaryDirectory() as temp_dir:
        file_path = os.path.join(temp_dir, uploaded_file.name)
        with open(file_path, "wb") as f:
            f.write(uploaded_file.getbuffer())

        try:
            anki_deck = AnkiDeck.create_from_file(file_path)
            st.session_state.anki_deck = anki_deck
            st.session_state.deck_name = uploaded_file.name.rstrip(".apkg")
            show_success(f"Successfully loaded {anki_deck.get_number_of_flashcards()} flashcard(s)!")
        except Exception as e:
            show_error("Failed to load Anki file", e)
            st.session_state.anki_deck = None

# Load languages from backend
if st.session_state.backend_languages is None or len(st.session_state.backend_languages) == 0:
    try:
        backend_languages = get_languages()
        st.session_state.backend_languages = backend_languages
    except Exception as e:
        show_error("Could not load languages from backend", e)
        st.session_state.backend_languages = []

backend_languages = st.session_state.backend_languages or []
language_options = {lang.name: lang for lang in backend_languages}
language_names = list(language_options.keys())

# Step 2: Configure Field Mapping
if st.session_state.anki_deck is not None:
    anki_deck = st.session_state.anki_deck
    
    st.markdown("---")
    st.subheader("2️⃣ Configure Field Mapping")
    
    if not backend_languages:
        show_error("No languages available. Please check your backend connection.")
        st.stop()
    
    all_fields = [None] + anki_deck.get_field_names()
    sound_fields = [None] + anki_deck.get_sound_field_names()
    
    # Deck name and description
    deck_name = st.text_input("Deck Name", value=st.session_state.deck_name)
    deck_description = st.text_area("Deck Description (optional)", value=f"Imported from {st.session_state.deck_name}.apkg")
    
    # Field mapping in columns
    cols = st.columns(2)
    
    with cols[0]:
        st.markdown("#### Front Side")
        front_language_name = st.selectbox(
            "Front Language",
            options=language_names,
            key="front_language",
            help="Select the language for the front of the flashcards"
        )
        front_language = language_options[front_language_name] if front_language_name else None
        
        front_field = st.selectbox("Select text field", key="front_text_field", options=all_fields)
        if front_field:
            front_field_string = format_field_values(anki_deck, front_field, 50)
            st.caption(f"Examples: **{front_field_string}**")
        
        front_pronunciation_field = st.selectbox("Select pronunciation field (optional)", key="front_pronunciation_field", options=sound_fields)
        if front_pronunciation_field:
            front_pronunciation_string = format_field_values(anki_deck, front_pronunciation_field, 50)
            st.caption(f"Examples: **{front_pronunciation_string}**")
    
    with cols[1]:
        st.markdown("#### Back Side")
        back_language_name = st.selectbox(
            "Back Language",
            options=language_names,
            key="back_language",
            help="Select the language for the back of the flashcards"
        )
        back_language = language_options[back_language_name] if back_language_name else None
        
        back_field = st.selectbox("Select text field", key="back_text_field", options=all_fields)
        if back_field:
            back_field_string = format_field_values(anki_deck, back_field, 50)
            st.caption(f"Examples: **{back_field_string}**")
        
        back_pronunciation_field = st.selectbox("Select pronunciation field (optional)", key="back_pronunciation_field", options=sound_fields)
        if back_pronunciation_field:
            back_pronunciation_string = format_field_values(anki_deck, back_pronunciation_field, 50)
            st.caption(f"Examples: **{back_pronunciation_string}**")
    
    # Step 3: Preview
    st.markdown("---")
    st.subheader("3️⃣ Preview Flashcards")
    
    if front_field and back_field:
        preview_index = st.number_input(
            "Preview Index", 
            min_value=0, 
            max_value=anki_deck.get_number_of_flashcards() - 1, 
            step=1, 
            value=0
        )
        
        card = anki_deck.get_flashcards()[preview_index]
        
        # Display preview
        cols = st.columns(2)
        
        with cols[0]:
            st.markdown("**Front Side**")
            front_value = strip_anki_formatting(card.get_raw_value(front_field))
            st.info(front_value)
            if front_pronunciation_field:
                try:
                    st.audio(card.get_media_path(front_pronunciation_field))
                except Exception as e:
                    st.caption(f"⚠️ Audio not available: {str(e)}")
        
        with cols[1]:
            st.markdown("**Back Side**")
            back_value = strip_anki_formatting(card.get_raw_value(back_field))
            st.info(back_value)
            if back_pronunciation_field:
                try:
                    st.audio(card.get_media_path(back_pronunciation_field))
                except Exception as e:
                    st.caption(f"⚠️ Audio not available: {str(e)}")
        
        # Step 4: Convert and Save
        st.markdown("---")
        st.subheader("4️⃣ Convert and Save Locally")
        
        if st.button("Convert to Local Deck", type="primary", use_container_width=True):
            if not deck_name:
                show_error("Please provide a deck name")
            elif not front_language or not back_language:
                show_error("Please select languages for both front and back sides")
            else:
                try:
                    # Initialize deck storage
                    deck_storage = DeckStorage()
                    
                    # Create deck
                    deck_info = deck_storage.create_deck(deck_name, deck_description)
                    deck_dir = deck_info["deck_dir"]
                    media_dir = deck_info["media_dir"]
                    
                    # Progress bar
                    progress_bar = st.progress(0)
                    status_text = st.empty()
                    
                    total_cards = anki_deck.get_number_of_flashcards()
                    successful_cards = 0
                    failed_cards = 0
                    
                    # Convert flashcards
                    for idx, flashcard in enumerate(anki_deck.get_flashcards()):
                        try:
                            # Create text content for front and back
                            front_text = strip_anki_formatting(flashcard.get_raw_value(front_field))
                            back_text = strip_anki_formatting(flashcard.get_raw_value(back_field))
                            
                            front_content = create_text_content(front_text, front_language.id)
                            back_content = create_text_content(back_text, back_language.id)
                            
                            # Add flashcard to deck
                            flashcard_obj = deck_storage.add_flashcard(
                                deck_dir=deck_dir,
                                deck_order=idx,
                                front_content=front_content,
                                back_content=back_content,
                                flashcard_id=f"flashcard-{idx+1}"
                            )
                            
                            # Add pronunciation if available
                            if front_pronunciation_field and flashcard.has_media_path(front_pronunciation_field):
                                try:
                                    audio_path = flashcard.get_media_path(front_pronunciation_field)
                                    deck_storage.add_pronunciation(deck_dir, flashcard_obj["id"], "front", audio_path)
                                except Exception:
                                    pass  # Skip if audio not available
                            
                            if back_pronunciation_field and flashcard.has_media_path(back_pronunciation_field):
                                try:
                                    audio_path = flashcard.get_media_path(back_pronunciation_field)
                                    deck_storage.add_pronunciation(deck_dir, flashcard_obj["id"], "back", audio_path)
                                except Exception:
                                    pass  # Skip if audio not available
                            
                            successful_cards += 1
                            
                        except Exception as e:
                            failed_cards += 1
                            if st.checkbox(f"Show error details for card {idx+1}", key=f"error_{idx}"):
                                st.error(str(e))
                        
                        # Update progress
                        progress = (idx + 1) / total_cards
                        progress_bar.progress(progress)
                        status_text.text(f"Processing card {idx + 1}/{total_cards}")
                    
                    # Complete
                    progress_bar.progress(1.0)
                    status_text.text("Conversion complete!")
                    
                    show_success(f"Successfully converted {successful_cards} flashcard(s)!")
                    if failed_cards > 0:
                        show_warning(f"Failed to convert {failed_cards} flashcard(s)")
                    
                    show_info(f"Deck saved to: {deck_dir}")
                    st.session_state.conversion_complete = True
                    
                except Exception as e:
                    show_error("Failed to convert deck", e)
    else:
        show_warning("Please select text fields for both front and back sides to preview flashcards")

# Show success message if conversion is complete
if st.session_state.conversion_complete:
    st.markdown("---")
    st.success("✅ Conversion complete! Your deck has been saved locally.")
    st.info("💡 You can now upload this deck using the '📤 Upload Local Deck' page or view it in '📚 Manage Local Decks'.")
    
    if st.button("Convert Another Deck"):
        st.session_state.anki_deck = None
        st.session_state.deck_name = ""
        st.session_state.conversion_complete = False
        st.rerun()
