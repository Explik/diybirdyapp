"""
Flashcard Import Tool - Streamlit Multi-page Application
Main entry point for the application.
"""

import streamlit as st

# Configure page settings
st.set_page_config(
    page_title="Flashcard Import Tool",
    page_icon="🎴",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Main page content
st.title("🎴 Flashcard Import Tool")
st.markdown("---")

st.markdown("""
## Welcome to the Flashcard Import Tool

This application helps you create flashcard decks from various sources with automatic translation capabilities.

### Available Features:

**📝 Create deck from .txt file**
- Import plain text files with one sentence per line
- Automatic translation to target language
- Support for auto-detection of source language

**📊 Create deck from .csv file**
- Import structured CSV data
- Skip header row option
- Specify languages for left and right columns

**📦 Create deck from .anki file** *(Coming Soon)*
- Import existing Anki decks
- Preserve card formatting and media

**🔊 Add pronunciation to flashcards**
- Individual recording for each flashcard
- Individual automatic generation using text-to-speech
- Individual playback of pronunciation audio
- Bulk translation for all flashcards in a deck

### Getting Started

1. Select a feature from the sidebar
2. Configure your import settings
3. Upload your source file
4. Generate your flashcard deck!

### System Requirements

- Running API server on `http://localhost:8080`
- Google Cloud credentials (for translation features)
- Supported languages configured in the system
""")

st.markdown("---")
st.info("👈 Select a tool from the sidebar to get started!")
