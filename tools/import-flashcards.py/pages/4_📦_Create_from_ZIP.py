"""
Page: Create deck from .zip file
Allows users to import flashcard decks from ZIP files in the internal deck storage format.
"""

import streamlit as st
import sys
import os
from pathlib import Path
import zipfile
import json
import shutil
import tempfile

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))
sys.path.append(str(Path(__file__).parent.parent))

from deck_storage import DeckStorage
from login_utils import render_login_sidebar

st.set_page_config(page_title="Create Deck from ZIP", page_icon="📦", layout="wide")

# Render login sidebar
render_login_sidebar()

st.title("📦 Create Deck from .zip File")

st.markdown("""
This tool allows you to import flashcard decks from ZIP files in the internal deck storage format.

**Expected ZIP format:**
- `data.json` - Contains deck metadata and flashcard data
- `media/` folder (optional) - Contains audio, image, and video files referenced by flashcards
""")

# Initialize session state
if 'deck_imported' not in st.session_state:
    st.session_state.deck_imported = False
if 'flashcard_count' not in st.session_state:
    st.session_state.flashcard_count = 0
if 'deck_info' not in st.session_state:
    st.session_state.deck_info = None
if 'import_error' not in st.session_state:
    st.session_state.import_error = None

st.markdown("---")

# File upload
uploaded_file = st.file_uploader(
    "Choose a .zip file",
    type=['zip'],
    help="Upload a ZIP file containing data.json and optional media folder"
)

def validate_zip_structure(zip_ref):
    """
    Validate that the ZIP file has the correct structure.
    Returns (is_valid, error_message, data_json_content)
    """
    # Check for data.json
    if 'data.json' not in zip_ref.namelist():
        return False, "Missing required file: data.json", None
    
    # Try to parse data.json
    try:
        with zip_ref.open('data.json') as f:
            data = json.load(f)
    except json.JSONDecodeError as e:
        return False, f"Invalid JSON in data.json: {str(e)}", None
    except Exception as e:
        return False, f"Error reading data.json: {str(e)}", None
    
    # Validate data.json structure
    if 'name' not in data:
        return False, "data.json missing required field: 'name'", None
    if 'flashcards' not in data:
        return False, "data.json missing required field: 'flashcards'", None
    if not isinstance(data['flashcards'], list):
        return False, "data.json 'flashcards' must be a list", None
    
    # Check for media files referenced in flashcards
    media_files_in_zip = set()
    for name in zip_ref.namelist():
        if name.startswith('media/') and name != 'media/':
            media_files_in_zip.add(name)
    
    # Collect all media references in flashcards
    referenced_media = set()
    for flashcard in data['flashcards']:
        # Check front and back content
        for side in ['frontContent', 'backContent']:
            if side in flashcard:
                content = flashcard[side]
                
                # Check for media file references
                if content.get('type') == 'audio-upload' and 'audioFileName' in content:
                    referenced_media.add(f"media/{content['audioFileName']}")
                elif content.get('type') == 'image-upload' and 'imageFileName' in content:
                    referenced_media.add(f"media/{content['imageFileName']}")
                elif content.get('type') == 'video-upload' and 'videoFileName' in content:
                    referenced_media.add(f"media/{content['videoFileName']}")
                
                # Check for pronunciation
                if 'pronunciation' in content and 'content' in content['pronunciation']:
                    referenced_media.add(content['pronunciation']['content'])
    
    # Check if all referenced media files exist
    missing_media = referenced_media - media_files_in_zip
    if missing_media:
        return False, f"Missing media files: {', '.join(missing_media)}", None
    
    return True, None, data

if uploaded_file is not None:
    try:
        # Create a temporary file to save the uploaded content
        with tempfile.NamedTemporaryFile(delete=False, suffix='.zip') as tmp_file:
            tmp_file.write(uploaded_file.getvalue())
            tmp_path = tmp_file.name
        
        # Validate ZIP structure
        with zipfile.ZipFile(tmp_path, 'r') as zip_ref:
            is_valid, error_msg, data_content = validate_zip_structure(zip_ref)
            
            if is_valid:
                # Display file preview
                media_files = [name for name in zip_ref.namelist() if name.startswith('media/') and name != 'media/']
                
                with st.expander(f"Preview: {uploaded_file.name}", expanded=True):
                    st.success("✅ Valid deck format detected!")
                    
                    col1, col2 = st.columns(2)
                    
                    with col1:
                        st.markdown("**Deck Information:**")
                        st.write(f"- **Name:** {data_content['name']}")
                        st.write(f"- **Description:** {data_content.get('description', '(none)')}")
                        st.write(f"- **Flashcard count:** {len(data_content['flashcards'])}")
                    
                    with col2:
                        st.markdown("**Media Files:**")
                        if media_files:
                            st.write(f"- **Total media files:** {len(media_files)}")
                            media_types = {}
                            for mf in media_files:
                                ext = Path(mf).suffix.lower()
                                media_types[ext] = media_types.get(ext, 0) + 1
                            for ext, count in media_types.items():
                                st.write(f"  - {ext} files: {count}")
                        else:
                            st.write("- No media files")
                    
                    # Show sample flashcards
                    if data_content['flashcards']:
                        st.markdown("**Sample Flashcards:**")
                        sample_count = min(5, len(data_content['flashcards']))
                        for i, fc in enumerate(data_content['flashcards'][:sample_count], 1):
                            front = fc.get('frontContent', {})
                            back = fc.get('backContent', {})
                            
                            # Format front text
                            if front.get('type') == 'text':
                                front_display = front.get('text', '(empty)')
                            elif front.get('type') == 'audio-upload':
                                front_display = f"🔊 Audio: {front.get('audioFileName', 'unknown')}"
                            elif front.get('type') == 'image-upload':
                                front_display = f"🖼️ Image: {front.get('imageFileName', 'unknown')}"
                            elif front.get('type') == 'video-upload':
                                front_display = f"🎥 Video: {front.get('videoFileName', 'unknown')}"
                            else:
                                front_display = f"({front.get('type', 'unknown type')})"
                            
                            # Format back text
                            if back.get('type') == 'text':
                                back_display = back.get('text', '(empty)')
                            elif back.get('type') == 'audio-upload':
                                back_display = f"🔊 Audio: {back.get('audioFileName', 'unknown')}"
                            elif back.get('type') == 'image-upload':
                                back_display = f"🖼️ Image: {back.get('imageFileName', 'unknown')}"
                            elif back.get('type') == 'video-upload':
                                back_display = f"🎥 Video: {back.get('videoFileName', 'unknown')}"
                            else:
                                back_display = f"({back.get('type', 'unknown type')})"
                            
                            st.text(f"{i}. {front_display} → {back_display}")
                        
                        if len(data_content['flashcards']) > sample_count:
                            st.text(f"... and {len(data_content['flashcards']) - sample_count} more flashcards")
                
                # Store data for import
                st.session_state.zip_data = data_content
                st.session_state.zip_path = tmp_path
                st.session_state.import_error = None
                
            else:
                st.error(f"❌ Invalid ZIP file: {error_msg}")
                st.session_state.import_error = error_msg
                st.session_state.zip_data = None
                # Clean up temp file
                os.unlink(tmp_path)
    
    except zipfile.BadZipFile:
        st.error("❌ Invalid ZIP file: File is not a valid ZIP archive")
        st.session_state.import_error = "Not a valid ZIP file"
    except Exception as e:
        st.error(f"❌ Error reading ZIP file: {str(e)}")
        st.session_state.import_error = str(e)

st.markdown("---")

# Import button
if uploaded_file and 'zip_data' in st.session_state and st.session_state.zip_data:
    # Option to override deck name
    override_name = st.checkbox(
        "Override deck name",
        value=False,
        help="Use a different name than the one in the ZIP file"
    )
    
    if override_name:
        deck_name = st.text_input(
            "New Deck Name",
            placeholder=st.session_state.zip_data['name'],
            help="Enter a new name for the imported deck"
        )
    else:
        deck_name = st.session_state.zip_data['name']
        st.info(f"📝 Deck will be imported as: **{deck_name}**")
    
    if st.button("📥 Import Deck", type="primary", use_container_width=True):
        if not deck_name or not deck_name.strip():
            st.error("❌ Please provide a deck name")
        else:
            try:
                with st.spinner("Importing deck..."):
                    # Initialize deck storage
                    storage = DeckStorage()
                    
                    # Create safe folder name
                    safe_name = "".join(c for c in deck_name if c.isalnum() or c in (' ', '-', '_')).rstrip()
                    safe_name = safe_name.replace(' ', '_')
                    
                    deck_dir = storage.storage_dir / safe_name
                    
                    # Check if deck already exists
                    if deck_dir.exists():
                        st.warning(f"⚠️ Deck '{deck_name}' already exists. It will be overwritten.")
                        shutil.rmtree(deck_dir)
                    
                    # Create deck directory
                    deck_dir.mkdir(parents=True, exist_ok=True)
                    media_dir = deck_dir / "media"
                    media_dir.mkdir(exist_ok=True)
                    
                    # Extract ZIP contents
                    with zipfile.ZipFile(st.session_state.zip_path, 'r') as zip_ref:
                        # Extract data.json
                        zip_ref.extract('data.json', deck_dir)
                        
                        # Extract media files
                        media_files = [name for name in zip_ref.namelist() if name.startswith('media/') and name != 'media/']
                        for media_file in media_files:
                            zip_ref.extract(media_file, deck_dir)
                    
                    # Update deck name if overridden
                    if override_name and deck_name != st.session_state.zip_data['name']:
                        data_file = deck_dir / "data.json"
                        with open(data_file, 'r', encoding='utf-8') as f:
                            deck_data = json.load(f)
                        deck_data['name'] = deck_name
                        with open(data_file, 'w', encoding='utf-8') as f:
                            json.dump(deck_data, f, indent=2, ensure_ascii=False)
                    
                    # Clean up temp file
                    os.unlink(st.session_state.zip_path)
                    
                    # Update session state
                    st.session_state.deck_imported = True
                    st.session_state.flashcard_count = len(st.session_state.zip_data['flashcards'])
                    st.session_state.deck_info = {
                        'name': deck_name,
                        'deck_dir': str(deck_dir),
                        'flashcard_count': len(st.session_state.zip_data['flashcards'])
                    }
                    
                    st.success(f"✅ Successfully imported deck: {deck_name}")
                    st.info(f"💾 Deck saved locally at: {deck_dir}")
                    st.balloons()
                    
            except Exception as e:
                st.error(f"❌ Error importing deck: {str(e)}")
                st.exception(e)

# Display summary if deck was imported
if st.session_state.deck_imported and st.session_state.deck_info:
    st.markdown("---")
    st.subheader("📊 Import Summary")
    
    summary_col1, summary_col2 = st.columns(2)
    
    with summary_col1:
        st.metric("Deck Name", st.session_state.deck_info['name'])
    
    with summary_col2:
        st.metric("Flashcards Imported", st.session_state.deck_info['flashcard_count'])
    
    # Show deck location
    st.info(f"📁 Deck location: `{st.session_state.deck_info['deck_dir']}`")
    
    # Action buttons
    button_col1, button_col2 = st.columns(2)
    
    with button_col1:
        if st.button("📤 Go to Upload Page", type="primary", use_container_width=True):
            st.switch_page("pages/5_🚀_Upload_deck.py")
    
    with button_col2:
        if st.button("Import Another Deck", use_container_width=True):
            st.session_state.deck_imported = False
            st.session_state.flashcard_count = 0
            st.session_state.deck_info = None
            st.session_state.import_error = None
            if 'zip_data' in st.session_state:
                del st.session_state.zip_data
            if 'zip_path' in st.session_state:
                del st.session_state.zip_path
            st.rerun()
