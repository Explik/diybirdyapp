"""
Page: Create deck from .anki file
Import Anki deck files and convert them to the internal storage format.
"""

import tempfile
import os
import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))

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
        "type": "text",
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

def detect_content_type(anki_deck, field_name) -> str:
    """Detect content type (text, audio, image, video) from a field by checking file extensions"""
    import re
    
    # Check the first card to determine the field type
    first_card = anki_deck.get_flashcards()[0]
    raw_value = first_card.get_raw_value(field_name)
    
    # Extract potential filenames from the field
    # Check for sound tag: [sound:filename.ext]
    sound_match = re.search(r'\[sound:([^\]]+)\]', raw_value)
    if sound_match:
        filename = sound_match.group(1)
        ext = filename.split('.')[-1].lower() if '.' in filename else ''
        
        # Audio extensions
        audio_exts = ['mp3', 'wav', 'ogg', 'flac', 'm4a', 'aac', 'wma', 'opus']
        if ext in audio_exts:
            return "Audio"
    
    # Check for img tag: <img src="filename.ext">
    img_match = re.search(r'<img[^>]+src=["\']?([^"\'>\s]+)["\']?', raw_value, re.IGNORECASE)
    if img_match:
        filename = img_match.group(1)
        ext = filename.split('.')[-1].lower() if '.' in filename else ''
        
        # Image extensions
        image_exts = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg', 'webp', 'ico']
        if ext in image_exts:
            return "Image"
    
    # Check for video tag: <video src="filename.ext">
    video_match = re.search(r'<video[^>]+src=["\']?([^"\'>\s]+)["\']?', raw_value, re.IGNORECASE)
    if video_match:
        filename = video_match.group(1)
        ext = filename.split('.')[-1].lower() if '.' in filename else ''
        
        # Video extensions
        video_exts = ['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv', 'webm', 'm4v']
        if ext in video_exts:
            return "Video"
    
    # Default to text if no media detected
    return "Text"

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
    image_fields = [None] + anki_deck.get_image_field_names()
    video_fields = [None] + anki_deck.get_video_field_names()
    media_fields = [None] + anki_deck.get_media_field_names()
    
    # Deck name and description
    deck_name = st.text_input("Deck Name", value=st.session_state.deck_name)
    deck_description = st.text_area("Deck Description (optional)", value=f"Imported from {st.session_state.deck_name}.apkg")
    
    # Field mapping in columns
    cols = st.columns(2)
    
    with cols[0]:
        st.markdown("#### Front Side")
        
        # Field selector - content type will be auto-detected
        front_field = st.selectbox(
            "Select field", 
            key="front_field", 
            options=all_fields,
            help="Select the field for the front of the flashcards. Content type will be detected automatically."
        )
        
        # Auto-detect content type
        front_content_type = None
        front_language = None
        front_pronunciation_field = None
        
        if front_field:
            front_content_type = detect_content_type(anki_deck, front_field)
            
            # Show detected content type
            st.caption(f"🔍 Detected type: **{front_content_type}**")
            
            # Show field examples
            front_field_string = format_field_values(anki_deck, front_field, 50)
            st.caption(f"Examples: **{front_field_string}**")
            
            # Language selector for text, audio, and video content
            if front_content_type in ["Text", "Audio", "Video"]:
                lang_label = "Language" if front_content_type == "Text" else f"{front_content_type} Language"
                front_language_name = st.selectbox(
                    lang_label,
                    options=language_names,
                    key="front_language",
                    help=f"Select the language for the front side"
                )
                front_language = language_options[front_language_name] if front_language_name else None
            
            # Pronunciation field for text content
            if front_content_type == "Text":
                front_pronunciation_field = st.selectbox(
                    "Pronunciation field (optional)", 
                    key="front_pronunciation_field", 
                    options=sound_fields
                )
                if front_pronunciation_field:
                    front_pronunciation_string = format_field_values(anki_deck, front_pronunciation_field, 50)
                    st.caption(f"Examples: **{front_pronunciation_string}**")
                
                # Transcription field for text content
                front_transcription_field = st.selectbox(
                    "Transcription field (optional)",
                    key="front_transcription_field",
                    options=all_fields,
                    help="Select a field containing transcription (e.g., pinyin, romaji)"
                )
                if front_transcription_field:
                    front_transcription_string = format_field_values(anki_deck, front_transcription_field, 50)
                    st.caption(f"Examples: **{front_transcription_string}**")
                    
                    # Transcription system input
                    front_transcription_system = st.text_input(
                        "Transcription system",
                        key="front_transcription_system",
                        placeholder="e.g., pinyin, romaji, IPA",
                        help="Specify the transcription system used"
                    )
    
    with cols[1]:
        st.markdown("#### Back Side")
        
        # Field selector - content type will be auto-detected
        back_field = st.selectbox(
            "Select field", 
            key="back_field", 
            options=all_fields,
            help="Select the field for the back of the flashcards. Content type will be detected automatically."
        )
        
        # Auto-detect content type
        back_content_type = None
        back_language = None
        back_pronunciation_field = None
        
        if back_field:
            back_content_type = detect_content_type(anki_deck, back_field)
            
            # Show detected content type
            st.caption(f"🔍 Detected type: **{back_content_type}**")
            
            # Show field examples
            back_field_string = format_field_values(anki_deck, back_field, 50)
            st.caption(f"Examples: **{back_field_string}**")
            
            # Language selector for text, audio, and video content
            if back_content_type in ["Text", "Audio", "Video"]:
                lang_label = "Language" if back_content_type == "Text" else f"{back_content_type} Language"
                back_language_name = st.selectbox(
                    lang_label,
                    options=language_names,
                    key="back_language",
                    help=f"Select the language for the back side"
                )
                back_language = language_options[back_language_name] if back_language_name else None
            
            # Pronunciation field for text content
            if back_content_type == "Text":
                back_pronunciation_field = st.selectbox(
                    "Pronunciation field (optional)", 
                    key="back_pronunciation_field", 
                    options=sound_fields
                )
                if back_pronunciation_field:
                    back_pronunciation_string = format_field_values(anki_deck, back_pronunciation_field, 50)
                    st.caption(f"Examples: **{back_pronunciation_string}**")
                
                # Transcription field for text content
                back_transcription_field = st.selectbox(
                    "Transcription field (optional)",
                    key="back_transcription_field",
                    options=all_fields,
                    help="Select a field containing transcription (e.g., pinyin, romaji)"
                )
                if back_transcription_field:
                    back_transcription_string = format_field_values(anki_deck, back_transcription_field, 50)
                    st.caption(f"Examples: **{back_transcription_string}**")
                    
                    # Transcription system input
                    back_transcription_system = st.text_input(
                        "Transcription system",
                        key="back_transcription_system",
                        placeholder="e.g., pinyin, romaji, IPA",
                        help="Specify the transcription system used"
                    )
    
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
            
            if front_content_type == "Text":
                front_value = strip_anki_formatting(card.get_raw_value(front_field))
                st.info(front_value)
                if front_transcription_field:
                    try:
                        transcription_value = strip_anki_formatting(card.get_raw_value(front_transcription_field))
                        if transcription_value:
                            st.caption(f"📝 Transcription: {transcription_value}")
                    except Exception:
                        pass
                if front_pronunciation_field:
                    try:
                        if card.has_media(front_pronunciation_field):
                            audio_path = card.get_media_file_path(front_pronunciation_field)
                            st.audio(audio_path)
                        else:
                            st.caption("⚠️ No audio file in this field")
                    except Exception as e:
                        st.caption(f"⚠️ Audio not available: {str(e)}")
            
            elif front_content_type in ["Audio", "Image", "Video"]:
                try:
                    if card.has_media(front_field):
                        media_path = card.get_media_file_path(front_field)
                        media_type = card.get_media_type(front_field)
                        
                        if media_type == "audio" or front_content_type == "Audio":
                            st.audio(media_path)
                        elif media_type == "image" or front_content_type == "Image":
                            st.image(media_path)
                        elif media_type == "video" or front_content_type == "Video":
                            st.video(media_path)
                    else:
                        st.caption("⚠️ No media file in this field")
                except Exception as e:
                    st.caption(f"⚠️ Media not available: {str(e)}")
        
        with cols[1]:
            st.markdown("**Back Side**")
            
            if back_content_type == "Text":
                back_value = strip_anki_formatting(card.get_raw_value(back_field))
                st.info(back_value)
                if back_transcription_field:
                    try:
                        transcription_value = strip_anki_formatting(card.get_raw_value(back_transcription_field))
                        if transcription_value:
                            st.caption(f"📝 Transcription: {transcription_value}")
                    except Exception:
                        pass
                if back_pronunciation_field:
                    try:
                        if card.has_media(back_pronunciation_field):
                            audio_path = card.get_media_file_path(back_pronunciation_field)
                            st.audio(audio_path)
                        else:
                            st.caption("⚠️ No audio file in this field")
                    except Exception as e:
                        st.caption(f"⚠️ Audio not available: {str(e)}")
            
            elif back_content_type in ["Audio", "Image", "Video"]:
                try:
                    if card.has_media(back_field):
                        media_path = card.get_media_file_path(back_field)
                        media_type = card.get_media_type(back_field)
                        
                        if media_type == "audio" or back_content_type == "Audio":
                            st.audio(media_path)
                        elif media_type == "image" or back_content_type == "Image":
                            st.image(media_path)
                        elif media_type == "video" or back_content_type == "Video":
                            st.video(media_path)
                    else:
                        st.caption("⚠️ No media file in this field")
                except Exception as e:
                    st.caption(f"⚠️ Media not available: {str(e)}")
        
        # Step 4: Convert and Save
        st.markdown("---")
        st.subheader("4️⃣ Convert and Save Locally")
        
        if st.button("Convert to Local Deck", type="primary", use_container_width=True):
            if not deck_name:
                show_error("Please provide a deck name")
            # Validate language requirements based on content type
            elif front_content_type in ["Text", "Audio", "Video"] and not front_language:
                show_error(f"Please select a language for the front side ({front_content_type})")
            elif back_content_type in ["Text", "Audio", "Video"] and not back_language:
                show_error(f"Please select a language for the back side ({back_content_type})")
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
                    pronunciation_count = 0
                    media_count = 0
                    
                    # Convert flashcards
                    for idx, flashcard in enumerate(anki_deck.get_flashcards()):
                        try:
                            # Create front content based on content type
                            if front_content_type == "Text":
                                front_text = strip_anki_formatting(flashcard.get_raw_value(front_field))
                                front_content = create_text_content(front_text, front_language.id)
                            else:
                                # For media content, create a placeholder that will be updated
                                front_content = {"type": "text", "text": "", "languageId": front_language.id if front_language else ""}
                            
                            # Create back content based on content type
                            if back_content_type == "Text":
                                back_text = strip_anki_formatting(flashcard.get_raw_value(back_field))
                                back_content = create_text_content(back_text, back_language.id)
                            else:
                                # For media content, create a placeholder that will be updated
                                back_content = {"type": "text", "text": "", "languageId": back_language.id if back_language else ""}
                            
                            # Add flashcard to deck
                            flashcard_obj = deck_storage.add_flashcard(
                                deck_dir=deck_dir,
                                deck_order=idx,
                                front_content=front_content,
                                back_content=back_content,
                                flashcard_id=f"flashcard-{idx+1}"
                            )
                            
                            # Handle front media content
                            if front_content_type in ["Audio", "Image", "Video"]:
                                try:
                                    if flashcard.has_media(front_field):
                                        media_path = flashcard.get_media_file_path(front_field)
                                        media_filename = flashcard.extract_media_filename(front_field)
                                        media_type = front_content_type.lower()
                                        
                                        deck_storage.add_media_content(
                                            deck_dir=deck_dir,
                                            flashcard_id=flashcard_obj["id"],
                                            side="front",
                                            media_file=media_path,
                                            media_type=media_type,
                                            language_id=front_language.id if front_language else None,
                                            media_name=media_filename
                                        )
                                        media_count += 1
                                except Exception as e:
                                    pass  # Skip if media not available
                            elif front_content_type == "Text" and front_pronunciation_field:
                                # Add pronunciation for text content
                                try:
                                    if flashcard.has_media(front_pronunciation_field):
                                        audio_path = flashcard.get_media_file_path(front_pronunciation_field)
                                        media_filename = flashcard.extract_media_filename(front_pronunciation_field)
                                        deck_storage.add_pronunciation(deck_dir, flashcard_obj["id"], "front", audio_path, media_filename)
                                        pronunciation_count += 1
                                except Exception:
                                    pass  # Skip if audio not available
                            
                            # Handle front transcription for text content
                            if front_content_type == "Text" and front_transcription_field and front_transcription_system:
                                try:
                                    transcription_text = strip_anki_formatting(flashcard.get_raw_value(front_transcription_field))
                                    if transcription_text:
                                        deck_storage.add_transcription(
                                            deck_dir=deck_dir,
                                            flashcard_id=flashcard_obj["id"],
                                            side="front",
                                            transcription=transcription_text,
                                            transcription_system=front_transcription_system
                                        )
                                except Exception:
                                    pass  # Skip if transcription not available
                            
                            # Handle back media content
                            if back_content_type in ["Audio", "Image", "Video"]:
                                try:
                                    if flashcard.has_media(back_field):
                                        media_path = flashcard.get_media_file_path(back_field)
                                        media_filename = flashcard.extract_media_filename(back_field)
                                        media_type = back_content_type.lower()
                                        
                                        deck_storage.add_media_content(
                                            deck_dir=deck_dir,
                                            flashcard_id=flashcard_obj["id"],
                                            side="back",
                                            media_file=media_path,
                                            media_type=media_type,
                                            language_id=back_language.id if back_language else None,
                                            media_name=media_filename
                                        )
                                        media_count += 1
                                except Exception as e:
                                    pass  # Skip if media not available
                            elif back_content_type == "Text" and back_pronunciation_field:
                                # Add pronunciation for text content
                                try:
                                    if flashcard.has_media(back_pronunciation_field):
                                        audio_path = flashcard.get_media_file_path(back_pronunciation_field)
                                        media_filename = flashcard.extract_media_filename(back_pronunciation_field)
                                        deck_storage.add_pronunciation(deck_dir, flashcard_obj["id"], "back", audio_path, media_filename)
                                        pronunciation_count += 1
                                except Exception:
                                    pass  # Skip if audio not available
                            
                            # Handle back transcription for text content
                            if back_content_type == "Text" and back_transcription_field and back_transcription_system:
                                try:
                                    transcription_text = strip_anki_formatting(flashcard.get_raw_value(back_transcription_field))
                                    if transcription_text:
                                        deck_storage.add_transcription(
                                            deck_dir=deck_dir,
                                            flashcard_id=flashcard_obj["id"],
                                            side="back",
                                            transcription=transcription_text,
                                            transcription_system=back_transcription_system
                                        )
                                except Exception:
                                    pass  # Skip if transcription not available
                            
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
                    if media_count > 0:
                        show_info(f"Imported {media_count} media file(s) (audio, image, or video)")
                    if pronunciation_count > 0:
                        show_info(f"Imported {pronunciation_count} pronunciation audio file(s)")
                    if failed_cards > 0:
                        show_warning(f"Failed to convert {failed_cards} flashcard(s)")
                    
                    show_info(f"Deck saved to: {deck_dir}")
                    st.session_state.conversion_complete = True
                    
                except Exception as e:
                    show_error("Failed to convert deck", e)
    else:
        show_warning("Please select fields for both front and back sides to preview flashcards")

# Show success message if conversion is complete
if st.session_state.conversion_complete:
    st.markdown("---")
    st.success("✅ Conversion complete! Your deck has been saved locally.")
    st.info("💡 You can now upload this deck using the '📤 Upload Local Deck' page or view it in '📚 Manage Local Decks'.")