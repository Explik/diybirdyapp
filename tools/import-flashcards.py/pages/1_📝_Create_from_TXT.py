"""
Page: Create deck from .txt file
Allows users to create flashcard decks from plain text files with automatic translation.
"""

import streamlit as st
import sys
import os
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))

from shared.import_client import (
    create_local_flashcard_deck,
    add_local_text_flashcard,
    upload_local_deck,
    create_deck_zip,
    get_local_deck_data,
    get_language_by_abbrevation,
    get_language_by_name,
    get_languages
)
from shared.google_api import translate_text, list_languages

st.set_page_config(page_title="Create Deck from TXT", page_icon="📝", layout="wide")

st.title("📝 Create deck from .txt File")

st.markdown("""
This tool allows you to create flashcard decks from plain text files. 
Upload a text file with one sentence per line in the source language, 
and the system will automatically translate them to the target language.
""")

# Initialize session state
if 'deck_created' not in st.session_state:
    st.session_state.deck_created = False
if 'flashcards_created' not in st.session_state:
    st.session_state.flashcards_created = 0
if 'deck_metadata' not in st.session_state:
    st.session_state.deck_metadata = None
if 'deck_uploaded' not in st.session_state:
    st.session_state.deck_uploaded = False

# Deck name
deck_name = st.text_input(
    "Deck Name",
    placeholder="My Flashcard Deck",
    help="Enter a name for your flashcard deck"
)

st.markdown("---")

# File upload
uploaded_file = st.file_uploader(
    "Choose a .txt file",
    type=['txt'],
    help="Upload a text file with one sentence per line"
)

if uploaded_file is not None:
    # Display file preview
    content = uploaded_file.read().decode('utf-8')
    lines = content.strip().split('\n')
    
    with st.expander(f"File {uploaded_file.name} ({len(lines)} lines)"):
        preview_lines = lines[:10]
        for i, line in enumerate(preview_lines, 1):
            st.text(f"{i}. {line}")
        if len(lines) > 10:
            st.text(f"... and {len(lines) - 10} more lines")

st.markdown("---")

# Try to load languages from Google Translate API
try:
    available_languages = list_languages()
    language_names = [lang['name'] for lang in available_languages]
    language_codes = {lang['name']: lang['language'] for lang in available_languages}
except Exception as e:
    st.error(f"⚠️ Could not load languages: {e}")
    available_languages = []
    language_names = []
    language_codes = {}

# Language selection
if available_languages:
    lang_col1, lang_col2 = st.columns(2)

    with lang_col1:
        source_language_option = st.selectbox(
            "Source Language",
            options=["Auto-detect"] + language_names,
            help="The language of the input text. Select 'Auto-detect' to automatically detect the language."
        )
        
    with lang_col2:
        if language_names:
            default_target = language_names[0] if language_names else None
            target_language_option = st.selectbox(
                "Target Language",
                options=language_names,
                index=0 if language_names else None,
                help="The language for the flashcard translations"
            )
        else:
            target_language_option = None

    st.markdown("---")

# Generate button
if st.button("🚀 Generate Flashcard Deck", type="primary", use_container_width=True):
    # Validation
    if not uploaded_file:
        st.error("❌ Please upload a .txt file")
    elif not deck_name:
        st.error("❌ Please enter a deck name")
    elif not target_language_option:
        st.error("❌ Please select a target language")
    else:
        # Create the deck
        try:
            with st.spinner("Creating flashcard deck locally..."):
                # Create deck locally
                deck_metadata = create_local_flashcard_deck(deck_name, "")
                st.session_state.deck_created = True
                st.session_state.deck_metadata = deck_metadata
                st.success(f"✅ Created local deck: {deck_name}")
                
                # Get language codes
                source_lang_code = None
                if source_language_option != "Auto-detect":
                    source_lang_code = language_codes[source_language_option]
                
                target_lang_code = language_codes[target_language_option]
                
                # Create flashcards
                progress_bar = st.progress(0)
                status_text = st.empty()
                
                flashcards_created = 0
                
                for idx, line in enumerate(lines):
                    if not line.strip():
                        continue
                    
                    # Update progress
                    progress = (idx + 1) / len(lines)
                    progress_bar.progress(progress)
                    status_text.text(f"Processing flashcard {idx + 1}/{len(lines)}: {line[:50]}...")
                    
                    # Translate text
                    source_text = line.strip()
                    
                    # Translate
                    translation_result = translate_text(
                        text=source_text,
                        target_language=target_lang_code,
                        source_language=source_lang_code
                    )
                    
                    target_text = translation_result[0]['translatedText']
                    
                    # Get detected source language code if auto-detect was used
                    detected_source_code = source_lang_code
                    if not source_lang_code and 'detectedSourceLanguage' in translation_result[0]:
                        detected_source_code = translation_result[0]['detectedSourceLanguage']
                    
                    # Try to get language objects from the system for creating flashcard
                    source_lang = None
                    target_lang = None
                    try:
                        if detected_source_code:
                            source_lang = get_language_by_abbrevation(detected_source_code)
                        target_lang = get_language_by_abbrevation(target_lang_code)
                    except:
                        # If language not found in system, try to use first available
                        try:
                            all_system_languages = get_languages()
                            if all_system_languages:
                                source_lang = all_system_languages[0]
                                target_lang = all_system_languages[0]
                        except:
                            pass
                    
                    # Create flashcard locally
                    if source_lang and target_lang:
                        add_local_text_flashcard(
                            deck_metadata=deck_metadata,
                            deck_order=idx,
                            front_language=source_lang,
                            back_language=target_lang,
                            front_text=source_text,
                            back_text=target_text
                        )
                        flashcards_created += 1
                
                progress_bar.progress(1.0)
                status_text.text("")
                
                st.session_state.flashcards_created = flashcards_created
                
                # Create ZIP file
                zip_path = create_deck_zip(deck_metadata)
                st.success(f"✅ Successfully created {flashcards_created} flashcards!")
                st.info(f"💾 Deck saved locally at: {deck_metadata['deck_dir']}")
                st.info(f"📦 ZIP file created: {zip_path}")
                st.balloons()
                
        except Exception as e:
            st.error(f"❌ Error creating flashcard deck: {str(e)}")
            st.exception(e)

# Display summary if deck was created
if st.session_state.deck_created:
    st.markdown("---")
    st.subheader("📊 Summary")
    
    summary_col1, summary_col2, summary_col3 = st.columns(3)
    
    with summary_col1:
        st.metric("Deck Name", deck_name)
    
    with summary_col2:
        st.metric("Flashcards Created", st.session_state.flashcards_created)
    
    with summary_col3:
        st.metric("Languages", f"{source_language_option} → {target_language_option}")
    
    # Show deck location
    if st.session_state.deck_metadata:
        st.info(f"📁 Deck location: `{st.session_state.deck_metadata['deck_dir']}`")
    
    # Upload and reset buttons
    button_col1, button_col2 = st.columns(2)
    
    with button_col1:
        if not st.session_state.deck_uploaded:
            if st.button("📤 Upload to Server", type="primary", use_container_width=True):
                try:
                    with st.spinner("Uploading deck to server..."):
                        server_deck = upload_local_deck(st.session_state.deck_metadata)
                        st.session_state.deck_uploaded = True
                        st.success(f"✅ Deck uploaded successfully! Server ID: {server_deck.id}")
                except Exception as e:
                    st.error(f"❌ Error uploading deck: {str(e)}")
                    st.exception(e)
        else:
            st.success("✅ Deck already uploaded to server")
    
    with button_col2:
        if st.button("Create Another Deck", use_container_width=True):
            st.session_state.deck_created = False
            st.session_state.flashcards_created = 0
            st.session_state.deck_metadata = None
            st.session_state.deck_uploaded = False
            st.rerun()

