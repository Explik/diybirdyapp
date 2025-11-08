"""
Page: Create deck from .csv file
Allows users to create flashcard decks from CSV files.
"""

import streamlit as st
import csv
import io
import sys
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

st.set_page_config(page_title="Create Deck from CSV", page_icon="📊", layout="wide")

st.title("📊 Create Deck from .csv File")
st.markdown("---")

st.markdown("""
This tool allows you to create flashcard decks from CSV files.
Upload a CSV file with two columns containing the source and target text.
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

# Create two columns for the form
col1, col2 = st.columns([2, 1])

with col1:
    st.subheader("Upload and Configuration")
    
    # File upload
    uploaded_file = st.file_uploader(
        "Choose a .csv file",
        type=['csv'],
        help="Upload a CSV file with two columns (source text and target text)"
    )
    
    if uploaded_file is not None:
        # Display file preview
        content = uploaded_file.read().decode('utf-8')
        uploaded_file.seek(0)  # Reset file pointer
        
        # Parse CSV
        csv_reader = csv.reader(io.StringIO(content))
        rows = list(csv_reader)
        
        st.info(f"📄 File loaded: {uploaded_file.name} ({len(rows)} rows)")
        
        with st.expander("Preview file content (first 10 rows)"):
            preview_rows = rows[:10]
            for i, row in enumerate(preview_rows, 1):
                if len(row) >= 2:
                    st.text(f"{i}. {row[0]} | {row[1]}")
            if len(rows) > 10:
                st.text(f"... and {len(rows) - 10} more rows")

with col2:
    st.subheader("Generation Options")
    
    # Skip header row option
    skip_header = st.checkbox(
        "Skip header row",
        value=False,
        help="Check this if the first row contains column headers"
    )
    
    # Deck name
    deck_name = st.text_input(
        "Deck Name",
        placeholder="My Flashcard Deck",
        help="Enter a name for your flashcard deck"
    )
    
    # Deck description
    deck_description = st.text_area(
        "Description (Optional)",
        placeholder="Brief description of the deck...",
        help="Optional description for the deck",
        height=100
    )
    
    # Try to load languages from API
    try:
        available_languages = get_languages()
        language_names = [lang.name for lang in available_languages]
        language_abbrevs = {lang.name: lang.abbreviation for lang in available_languages}
    except Exception as e:
        st.error(f"⚠️ Could not load languages from API: {e}")
        st.info("Make sure the API server is running on http://localhost:8080")
        available_languages = []
        language_names = []
        language_abbrevs = {}

# Language selection
st.markdown("---")
st.subheader("Language Settings")

lang_col1, lang_col2 = st.columns(2)

with lang_col1:
    if language_names:
        left_language_option = st.selectbox(
            "Left Language (Column 1)",
            options=language_names,
            help="The language of the left column text"
        )
    else:
        left_language_option = None
    
with lang_col2:
    if language_names:
        default_right_idx = 1 if len(language_names) > 1 else 0
        right_language_option = st.selectbox(
            "Right Language (Column 2)",
            options=language_names,
            index=default_right_idx,
            help="The language of the right column text"
        )
    else:
        right_language_option = None

# Generate button
st.markdown("---")

if st.button("🚀 Generate Flashcard Deck", type="primary", use_container_width=True):
    # Validation
    if not uploaded_file:
        st.error("❌ Please upload a .csv file")
    elif not deck_name:
        st.error("❌ Please enter a deck name")
    elif not left_language_option or not right_language_option:
        st.error("❌ Please select both languages")
    else:
        # Create the deck
        try:
            with st.spinner("Creating flashcard deck locally..."):
                # Create deck locally
                deck_metadata = create_local_flashcard_deck(deck_name, deck_description or "")
                st.session_state.deck_created = True
                st.session_state.deck_metadata = deck_metadata
                st.success(f"✅ Created local deck: {deck_name}")
                
                # Get language objects
                left_lang_abbrev = language_abbrevs[left_language_option]
                left_lang = get_language_by_abbrevation(left_lang_abbrev)
                
                right_lang_abbrev = language_abbrevs[right_language_option]
                right_lang = get_language_by_abbrevation(right_lang_abbrev)
                
                # Parse CSV again
                uploaded_file.seek(0)
                content = uploaded_file.read().decode('utf-8')
                csv_reader = csv.reader(io.StringIO(content))
                rows = list(csv_reader)
                
                # Skip header if requested
                start_idx = 1 if skip_header else 0
                data_rows = rows[start_idx:]
                
                # Create flashcards
                progress_bar = st.progress(0)
                status_text = st.empty()
                
                flashcards_created = 0
                
                for idx, row in enumerate(data_rows):
                    if len(row) < 2 or not row[0].strip() or not row[1].strip():
                        continue
                    
                    # Update progress
                    progress = (idx + 1) / len(data_rows)
                    progress_bar.progress(progress)
                    status_text.text(f"Processing flashcard {idx + 1}/{len(data_rows)}: {row[0][:50]}...")
                    
                    left_text = row[0].strip()
                    right_text = row[1].strip()
                    
                    # Create flashcard locally
                    add_local_text_flashcard(
                        deck_metadata=deck_metadata,
                        deck_order=idx,
                        front_language=left_lang,
                        back_language=right_lang,
                        front_text=left_text,
                        back_text=right_text
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
        st.metric("Languages", f"{left_language_option} → {right_language_option}")
    
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

