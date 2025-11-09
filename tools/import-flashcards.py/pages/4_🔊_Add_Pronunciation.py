"""
Page: Add pronunciation to flashcards
Allows users to add pronunciation audio to flashcard decks.
"""

import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))
sys.path.append(str(Path(__file__).parent.parent))

from import_client import get_languages
from shared.google_api import text_to_speech

st.set_page_config(page_title="Add Pronunciation", page_icon="🔊", layout="wide")

st.title("🔊 Add Pronunciation to Flashcards")
st.markdown("---")

st.markdown("""
This tool allows you to add pronunciation audio to flashcards in a deck.
You can record your own pronunciation or use automatic text-to-speech synthesis.
""")

# Initialize session state
if 'selected_deck' not in st.session_state:
    st.session_state.selected_deck = None
if 'current_flashcard_idx' not in st.session_state:
    st.session_state.current_flashcard_idx = 0
if 'recorded_audio' not in st.session_state:
    st.session_state.recorded_audio = {}

# Deck selection
st.subheader("Select Deck")

# Placeholder for deck list - would be fetched from API
deck_options = ["Sample Deck 1", "Sample Deck 2", "Spanish Basics"]

selected_deck = st.selectbox(
    "Choose a flashcard deck",
    options=deck_options,
    help="Select the deck to add pronunciation to"
)

if selected_deck:
    st.session_state.selected_deck = selected_deck
    
    st.success(f"✅ Selected deck: {selected_deck}")
    
    st.markdown("---")
    st.subheader("Pronunciation Options")
    
    # Create tabs for different modes
    tab1, tab2, tab3 = st.tabs(["📝 Individual", "🔊 Playback", "🚀 Bulk Generation"])
    
    with tab1:
        st.markdown("### Individual Flashcard Pronunciation")
        
        # Mock flashcard data
        flashcards = [
            {"id": 1, "front": "Hello", "back": "Hola"},
            {"id": 2, "front": "Goodbye", "back": "Adiós"},
            {"id": 3, "front": "Thank you", "back": "Gracias"}
        ]
        
        if flashcards:
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
            
            st.markdown("---")
            
            # Front side
            st.markdown("#### Front Side")
            st.info(f"📝 {current_card['front']}")
            
            col1, col2 = st.columns(2)
            
            with col1:
                st.markdown("**Individual Recording**")
                if st.button("🎤 Record Audio (Front)", key="record_front"):
                    st.warning("⚠️ Recording feature requires browser microphone access (coming soon)")
            
            with col2:
                st.markdown("**Automatic Generation**")
                if st.button("🔊 Generate TTS (Front)", key="tts_front"):
                    st.info("🔄 Generating text-to-speech audio...")
                    st.success("✅ Audio generated for front side")
            
            st.markdown("---")
            
            # Back side
            st.markdown("#### Back Side")
            st.info(f"📝 {current_card['back']}")
            
            col1, col2 = st.columns(2)
            
            with col1:
                st.markdown("**Individual Recording**")
                if st.button("🎤 Record Audio (Back)", key="record_back"):
                    st.warning("⚠️ Recording feature requires browser microphone access (coming soon)")
            
            with col2:
                st.markdown("**Automatic Generation**")
                if st.button("🔊 Generate TTS (Back)", key="tts_back"):
                    st.info("🔄 Generating text-to-speech audio...")
                    st.success("✅ Audio generated for back side")
    
    with tab2:
        st.markdown("### Individual Playback")
        
        st.info("Play back pronunciation audio for individual flashcards")
        
        # Mock flashcard selector
        flashcard_select = st.selectbox(
            "Select Flashcard",
            options=[f"Card {i+1}: {fc['front']} - {fc['back']}" for i, fc in enumerate(flashcards)],
            help="Choose a flashcard to play pronunciation"
        )
        
        col1, col2 = st.columns(2)
        
        with col1:
            st.markdown("**Front Side Audio**")
            if st.button("▶️ Play Front", key="play_front"):
                st.info("🔊 Playing front side audio...")
        
        with col2:
            st.markdown("**Back Side Audio**")
            if st.button("▶️ Play Back", key="play_back"):
                st.info("🔊 Playing back side audio...")
    
    with tab3:
        st.markdown("### Bulk Translation")
        
        st.info("Generate text-to-speech audio for all flashcards in the deck at once")
        
        st.markdown("#### Settings")
        
        col1, col2 = st.columns(2)
        
        with col1:
            generate_front = st.checkbox(
                "Generate for front side",
                value=True,
                help="Generate pronunciation for the front side of all flashcards"
            )
            
            if generate_front:
                front_voice = st.selectbox(
                    "Front voice",
                    options=["Female (Standard)", "Male (Standard)", "Female (Wavenet)", "Male (Wavenet)"],
                    help="Select the voice for front side audio"
                )
        
        with col2:
            generate_back = st.checkbox(
                "Generate for back side",
                value=True,
                help="Generate pronunciation for the back side of all flashcards"
            )
            
            if generate_back:
                back_voice = st.selectbox(
                    "Back voice",
                    options=["Female (Standard)", "Male (Standard)", "Female (Wavenet)", "Male (Wavenet)"],
                    help="Select the voice for back side audio"
                )
        
        st.markdown("---")
        
        audio_quality = st.select_slider(
            "Audio Quality",
            options=["Low", "Medium", "High", "Very High"],
            value="High",
            help="Higher quality audio files are larger"
        )
        
        st.markdown("---")
        
        if st.button("� Generate Pronunciation for All Flashcards", type="primary", use_container_width=True):
            if not generate_front and not generate_back:
                st.error("❌ Please select at least one side to generate pronunciation")
            else:
                with st.spinner("Generating pronunciation audio..."):
                    progress_bar = st.progress(0)
                    status_text = st.empty()
                    
                    # Mock generation
                    for idx, card in enumerate(flashcards):
                        progress = (idx + 1) / len(flashcards)
                        progress_bar.progress(progress)
                        status_text.text(f"Generating audio {idx + 1}/{len(flashcards)}: {card['front']}...")
                    
                    progress_bar.progress(1.0)
                    status_text.text("")
                    
                    st.success(f"✅ Successfully generated pronunciation for {len(flashcards)} flashcards!")
                    st.balloons()

else:
    st.info("👆 Please select a deck to add pronunciation")

st.markdown("---")
st.caption("💡 Tip: You can use Google's text-to-speech or record your own pronunciation for more natural audio")


st.markdown("---")
st.caption("💡 Tip: You can use Google's text-to-speech or record your own pronunciation for more natural audio")
