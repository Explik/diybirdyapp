"""
Page: Add pronunciation to flashcards
Allows users to add pronunciation audio to flashcard decks.
"""

import streamlit as st
import sys
from pathlib import Path
import tempfile
import os

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))
sys.path.append(str(Path(__file__).parent.parent))

from import_client import get_languages, list_local_decks, add_local_pronunciation
from login_utils import render_login_sidebar
from deck_storage import DeckStorage

st.set_page_config(page_title="Add Pronunciation", page_icon="🔊", layout="wide")

# Render login sidebar
render_login_sidebar()

st.title("🔊 Add Pronunciation to Flashcards")
st.markdown("---")

st.markdown("""
This tool allows you to add pronunciation audio to flashcards in a deck.
You can record your own pronunciation for individual flashcards.
""")

def has_pronunciation(flashcard, side):
    """Check if a flashcard has pronunciation for a given side."""
    content_key = f"{side}Content"
    if content_key in flashcard:
        return "pronunciation" in flashcard[content_key] and "content" in flashcard[content_key]["pronunciation"]
    return False

def save_audio_to_file(audio_data, flashcard_id, side):
    """Save audio data to a temporary file and return the path."""
    # Create a temporary file
    suffix = ".wav"  # Streamlit audio_input returns WAV format
    temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=suffix, prefix=f"{flashcard_id}_{side}_")
    temp_file.write(audio_data.getvalue())
    temp_file.close()
    return temp_file.name

# Initialize session state
if 'selected_deck' not in st.session_state:
    st.session_state.selected_deck = None
if 'selected_deck_data' not in st.session_state:
    st.session_state.selected_deck_data = None
if 'current_flashcard_idx' not in st.session_state:
    st.session_state.current_flashcard_idx = 0

# Deck selection
st.subheader("Select Deck")

# Get local decks
local_decks = list_local_decks()

if not local_decks:
    st.warning("⚠️ No decks found. Please create a deck first using one of the other tools.")
else:
    # Create options with deck names and flashcard counts
    deck_options = {
        deck['name']: deck 
        for deck in local_decks
    }
    
    selected_deck_name = st.selectbox(
        "Choose a flashcard deck",
        options=list(deck_options.keys()),
        help="Select the deck to add pronunciation to",
        format_func=lambda name: f"{name} ({deck_options[name]['flashcard_count']} cards)"
    )

if local_decks and selected_deck_name:
    selected_deck = deck_options[selected_deck_name]
    st.session_state.selected_deck = selected_deck
    st.session_state.selected_deck_data = selected_deck['data']
    
    st.success(f"✅ Selected deck: {selected_deck['name']} ({selected_deck['flashcard_count']} flashcards)")
    
    # Show recording status - count from deck data
    flashcards = selected_deck['data'].get('flashcards', [])
    total_recordings = sum(
        1 for fc in flashcards if has_pronunciation(fc, 'front')
    ) + sum(
        1 for fc in flashcards if has_pronunciation(fc, 'back')
    )
    
    if total_recordings > 0:
        st.info(f"📊 Recordings saved: {total_recordings} audio files")
    
    st.markdown("---")
    st.subheader("Pronunciation Options")
    
    # Create tabs for different modes
    tab1, tab2 = st.tabs(["📝 Individual", "🔊 Playback"])
    
    with tab1:
        st.markdown("### Individual Flashcard Pronunciation")
        
        # Get flashcards from the selected deck
        flashcards = selected_deck['data'].get('flashcards', [])
        
        if not flashcards:
            st.warning("This deck has no flashcards yet.")
        else:
            # Navigation
            nav_col1, nav_col2, nav_col3 = st.columns([1, 2, 1])
            
            with nav_col1:
                if st.button("⬅️ Previous", disabled=st.session_state.current_flashcard_idx == 0):
                    st.session_state.current_flashcard_idx -= 1
                    st.rerun()
            
            with nav_col2:
                st.markdown(f"**Flashcard {st.session_state.current_flashcard_idx + 1} of {len(flashcards)}**")
            
            with nav_col3:
                if st.button("Next ➡️", disabled=st.session_state.current_flashcard_idx >= len(flashcards) - 1):
                    st.session_state.current_flashcard_idx += 1
                    st.rerun()
            
            # Current flashcard
            current_card = flashcards[st.session_state.current_flashcard_idx]
            
            # Extract text from content structure
            front_text = current_card['frontContent']['text'] if current_card['frontContent']['type'] == 'text' else None
            back_text = current_card['backContent']['text'] if current_card['backContent']['type'] == 'text' else None
            
            # Show status of current card recordings (check deck data)
            front_recorded = has_pronunciation(current_card, 'front')
            back_recorded = has_pronunciation(current_card, 'back')
            
            status_col1, status_col2 = st.columns(2)
            with status_col1:
                if front_recorded:
                    st.success("✅ Front recorded")
                else:
                    st.info("⭕ Front not recorded")
            with status_col2:
                if back_recorded:
                    st.success("✅ Back recorded")
                else:
                    st.info("⭕ Back not recorded")
            
            st.markdown("---")
            
            # Display both sides side by side
            front_col, back_col = st.columns(2)
            
            # Front side
            with front_col:
                st.markdown("#### Front Side")
                st.info(f"📝 {front_text}")
                
                st.markdown("**Record Pronunciation**")
                
                audio_front = st.audio_input("Record front side pronunciation", key=f"record_front_{current_card['id']}")
                
                if audio_front:
                    # Save audio to temporary file
                    temp_audio_path = save_audio_to_file(audio_front, current_card['id'], 'front')
                    
                    try:
                        # Save to deck storage
                        media_path = add_local_pronunciation(
                            selected_deck,
                            current_card['id'],
                            'front',
                            temp_audio_path
                        )
                        
                        st.success("✅ Front side audio recorded and saved!")
                        st.audio(audio_front)
                        
                        # Update the deck data in session state to reflect changes
                        st.session_state.selected_deck_data = list_local_decks()[
                            next(i for i, d in enumerate(list_local_decks()) if d['name'] == selected_deck['name'])
                        ]['data']
                        
                    finally:
                        # Clean up temporary file
                        if os.path.exists(temp_audio_path):
                            os.unlink(temp_audio_path)
                
                # Show existing pronunciation if available
                if has_pronunciation(current_card, 'front'):
                    if not audio_front:  # Only show if not currently recording
                        st.info("Existing recording:")
                        # Load the audio from the media directory
                        deck_dir = Path(selected_deck['deck_dir'])
                        media_path = current_card['frontContent']['pronunciation']['content']
                        audio_file_path = deck_dir / media_path
                        if audio_file_path.exists():
                            with open(audio_file_path, 'rb') as f:
                                st.audio(f.read())
                    
                    # Delete button for existing recording
                    if st.button("🗑️ Delete Front Recording", key=f"delete_front_{current_card['id']}"):
                        # Remove pronunciation from deck data
                        deck_storage = DeckStorage()
                        deck_data = deck_storage.get_deck_data(selected_deck['deck_dir'])
                        
                        # Find and update the flashcard
                        for fc in deck_data['flashcards']:
                            if fc['id'] == current_card['id']:
                                if 'pronunciation' in fc['frontContent']:
                                    # Delete the media file
                                    deck_dir = Path(selected_deck['deck_dir'])
                                    media_path = fc['frontContent']['pronunciation']['content']
                                    audio_file_path = deck_dir / media_path
                                    if audio_file_path.exists():
                                        os.unlink(audio_file_path)
                                    
                                    # Remove from data
                                    del fc['frontContent']['pronunciation']
                                break
                        
                        # Save updated deck data
                        import json
                        data_file = Path(selected_deck['deck_dir']) / "data.json"
                        with open(data_file, 'w', encoding='utf-8') as f:
                            json.dump(deck_data, f, indent=2, ensure_ascii=False)
                        
                        st.success("✅ Front recording deleted!")
                        st.rerun()
            
            # Back side
            with back_col:
                st.markdown("#### Back Side")
                st.info(f"📝 {back_text}")
                
                st.markdown("**Record Pronunciation**")
                
                audio_back = st.audio_input("Record back side pronunciation", key=f"record_back_{current_card['id']}")
                
                if audio_back:
                    # Save audio to temporary file
                    temp_audio_path = save_audio_to_file(audio_back, current_card['id'], 'back')
                    
                    try:
                        # Save to deck storage
                        media_path = add_local_pronunciation(
                            selected_deck,
                            current_card['id'],
                            'back',
                            temp_audio_path
                        )
                        
                        st.success("✅ Back side audio recorded and saved!")
                        st.audio(audio_back)
                        
                        # Update the deck data in session state to reflect changes
                        st.session_state.selected_deck_data = list_local_decks()[
                            next(i for i, d in enumerate(list_local_decks()) if d['name'] == selected_deck['name'])
                        ]['data']
                        
                    finally:
                        # Clean up temporary file
                        if os.path.exists(temp_audio_path):
                            os.unlink(temp_audio_path)
                
                # Show existing pronunciation if available
                if has_pronunciation(current_card, 'back'):
                    if not audio_back:  # Only show if not currently recording
                        st.info("Existing recording:")
                        # Load the audio from the media directory
                        deck_dir = Path(selected_deck['deck_dir'])
                        media_path = current_card['backContent']['pronunciation']['content']
                        audio_file_path = deck_dir / media_path
                        if audio_file_path.exists():
                            with open(audio_file_path, 'rb') as f:
                                st.audio(f.read())
                    
                    # Delete button for existing recording
                    if st.button("🗑️ Delete Back Recording", key=f"delete_back_{current_card['id']}"):
                        # Remove pronunciation from deck data
                        deck_storage = DeckStorage()
                        deck_data = deck_storage.get_deck_data(selected_deck['deck_dir'])
                        
                        # Find and update the flashcard
                        for fc in deck_data['flashcards']:
                            if fc['id'] == current_card['id']:
                                if 'pronunciation' in fc['backContent']:
                                    # Delete the media file
                                    deck_dir = Path(selected_deck['deck_dir'])
                                    media_path = fc['backContent']['pronunciation']['content']
                                    audio_file_path = deck_dir / media_path
                                    if audio_file_path.exists():
                                        os.unlink(audio_file_path)
                                    
                                    # Remove from data
                                    del fc['backContent']['pronunciation']
                                break
                        
                        # Save updated deck data
                        import json
                        data_file = Path(selected_deck['deck_dir']) / "data.json"
                        with open(data_file, 'w', encoding='utf-8') as f:
                            json.dump(deck_data, f, indent=2, ensure_ascii=False)
                        
                        st.success("✅ Back recording deleted!")
                        st.rerun()
    
    with tab2:
        st.markdown("### Individual Playback")
        
        st.info("Play back pronunciation audio for individual flashcards")
        
        if not flashcards:
            st.warning("This deck has no flashcards yet.")
        else:
            # Flashcard selector
            selected_idx = st.selectbox(
                "Select Flashcard",
                options=list(range(len(flashcards))),
                format_func=lambda i: f"Card {i+1}: {flashcards[i].get('frontContent', {}).get('text', 'No text')} - {flashcards[i].get('backContent', {}).get('text', 'No text')}",
                help="Choose a flashcard to play pronunciation"
            )
            
            selected_card = flashcards[selected_idx]
            
            col1, col2 = st.columns(2)
            
            with col1:
                st.markdown("**Front Side Audio**")
                if has_pronunciation(selected_card, 'front'):
                    # Load the audio from the media directory
                    deck_dir = Path(selected_deck['deck_dir'])
                    media_path = selected_card['frontContent']['pronunciation']['content']
                    audio_file_path = deck_dir / media_path
                    if audio_file_path.exists():
                        with open(audio_file_path, 'rb') as f:
                            st.audio(f.read())
                    else:
                        st.warning("Audio file not found")
                    
                    if st.button("🗑️ Delete", key=f"playback_delete_front_{selected_card['id']}"):
                        # Remove pronunciation from deck data
                        deck_storage = DeckStorage()
                        deck_data = deck_storage.get_deck_data(selected_deck['deck_dir'])
                        
                        # Find and update the flashcard
                        for fc in deck_data['flashcards']:
                            if fc['id'] == selected_card['id']:
                                if 'pronunciation' in fc['frontContent']:
                                    # Delete the media file
                                    media_path = fc['frontContent']['pronunciation']['content']
                                    audio_file_path = deck_dir / media_path
                                    if audio_file_path.exists():
                                        os.unlink(audio_file_path)
                                    
                                    # Remove from data
                                    del fc['frontContent']['pronunciation']
                                break
                        
                        # Save updated deck data
                        import json
                        data_file = Path(selected_deck['deck_dir']) / "data.json"
                        with open(data_file, 'w', encoding='utf-8') as f:
                            json.dump(deck_data, f, indent=2, ensure_ascii=False)
                        
                        st.success("✅ Front recording deleted!")
                        st.rerun()
                else:
                    st.warning("No recording available for front side")
            
            with col2:
                st.markdown("**Back Side Audio**")
                if has_pronunciation(selected_card, 'back'):
                    # Load the audio from the media directory
                    deck_dir = Path(selected_deck['deck_dir'])
                    media_path = selected_card['backContent']['pronunciation']['content']
                    audio_file_path = deck_dir / media_path
                    if audio_file_path.exists():
                        with open(audio_file_path, 'rb') as f:
                            st.audio(f.read())
                    else:
                        st.warning("Audio file not found")
                    
                    if st.button("🗑️ Delete", key=f"playback_delete_back_{selected_card['id']}"):
                        # Remove pronunciation from deck data
                        deck_storage = DeckStorage()
                        deck_data = deck_storage.get_deck_data(selected_deck['deck_dir'])
                        
                        # Find and update the flashcard
                        for fc in deck_data['flashcards']:
                            if fc['id'] == selected_card['id']:
                                if 'pronunciation' in fc['backContent']:
                                    # Delete the media file
                                    media_path = fc['backContent']['pronunciation']['content']
                                    audio_file_path = deck_dir / media_path
                                    if audio_file_path.exists():
                                        os.unlink(audio_file_path)
                                    
                                    # Remove from data
                                    del fc['backContent']['pronunciation']
                                break
                        
                        # Save updated deck data
                        import json
                        data_file = Path(selected_deck['deck_dir']) / "data.json"
                        with open(data_file, 'w', encoding='utf-8') as f:
                            json.dump(deck_data, f, indent=2, ensure_ascii=False)
                        
                        st.success("✅ Back recording deleted!")
                        st.rerun()
                else:
                    st.warning("No recording available for back side")

else:
    st.info("👆 Please select a deck to add pronunciation")

st.markdown("---")
st.caption("💡 Tip: Recordings are automatically saved when you finish recording. Use the delete button to remove unwanted recordings.")
