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
sys.path.append(str(Path(__file__).parent.parent))

from import_client import (
    create_local_flashcard_deck,
    add_local_text_flashcard,
    create_deck_zip,
    get_local_deck_data,
    get_language_by_abbrevation,
    get_language_by_name,
    get_languages
)
from login_utils import render_login_sidebar

st.set_page_config(page_title="Create Deck from CSV", page_icon="📊", layout="wide")

# Render login sidebar
render_login_sidebar()

st.title("📊 Create Deck from .csv File")

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

# Deck name
deck_name = st.text_input(
    "Deck Name",
    placeholder="My Flashcard Deck",
    help="Enter a name for your flashcard deck"
)

st.markdown("---")

# File upload
uploaded_file = st.file_uploader(
    "Choose a .csv file",
    type=['csv'],
    help="Upload a CSV file with two columns (source text and target text)"
)

# Skip header row option
skip_header = st.checkbox(
    "Skip header row",
    value=False,
    help="Check this if the first row contains column headers"
)

if uploaded_file is not None:
    # Display file preview
    content = uploaded_file.read().decode('utf-8')
    uploaded_file.seek(0)  # Reset file pointer
    
    # Parse CSV
    csv_reader = csv.reader(io.StringIO(content))
    rows = list(csv_reader)
    
    # Determine which rows to preview
    preview_start = 1 if skip_header else 0
    preview_rows = rows[preview_start:preview_start + 10]
    total_data_rows = len(rows) - (1 if skip_header else 0)
    
    with st.expander(f"File {uploaded_file.name} ({total_data_rows} data rows)"):
        for i, row in enumerate(preview_rows, 1):
            if len(row) >= 2:
                st.text(f"{i}. {row[0]} | {row[1]}")
        if len(rows) - preview_start > 10:
            st.text(f"... and {len(rows) - preview_start - 10} more rows")

st.markdown("---")

# Try to load languages from backend
try:
    backend_languages = get_languages()
    language_options = {lang.name: lang for lang in backend_languages}
    language_names = list(language_options.keys())
except Exception as e:
    st.error(f"⚠️ Could not load languages from backend: {e}")
    backend_languages = []
    language_options = {}
    language_names = []

# Language selection
if backend_languages:
    lang_col1, lang_col2 = st.columns(2)

    with lang_col1:
        left_language_option = st.selectbox(
            "Left Language (Column 1)",
            options=language_names,
            help="The language of the left column text"
        )
        
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

    st.markdown("---")
else: 
    st.error("❌ Failed to load available languages from the backend.")
    left_language_option = None
    right_language_option = None

# Generate button
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
                deck_metadata = create_local_flashcard_deck(deck_name, "")
                st.session_state.deck_created = True
                st.session_state.deck_metadata = deck_metadata
                st.success(f"✅ Created local deck: {deck_name}")
                
                # Get language objects
                left_lang = language_options[left_language_option]
                right_lang = language_options[right_language_option]
                
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
                        front_language=left_lang.id,
                        back_language=right_lang.id,
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
    
    # Action buttons
    button_col1, button_col2 = st.columns(2)
    
    with button_col1:
        upload_url = f"Upload_deck?deck={deck_name}"
        st.markdown(f"[📤 Upload to Server]({upload_url})")
        
        if st.button("📤 Go to Upload Page", type="primary", use_container_width=True):
            st.switch_page("pages/Upload_deck.py")
            st.query_params["deck"] = deck_name
    
    with button_col2:
        if st.button("Create Another Deck", use_container_width=True):
            st.session_state.deck_created = False
            st.session_state.flashcards_created = 0
            st.session_state.deck_metadata = None
            st.session_state.deck_uploaded = False
            st.rerun()

