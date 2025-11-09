"""
Page: Create deck from .anki file
Placeholder page for future Anki import functionality.
"""

import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))
sys.path.append(str(Path(__file__).parent.parent))

from login_utils import render_login_sidebar

st.set_page_config(page_title="Create Deck from Anki", page_icon="📦", layout="wide")

# Render login sidebar
render_login_sidebar()

st.title("📦 Create Deck from .anki File")
st.markdown("---")

st.info("🚧 This feature is coming soon!")

st.markdown("""
### Planned Features:

- **Anki Deck Import**: Import existing Anki (.apkg) deck files
- **Media Preservation**: Maintain images, audio, and video files
- **Card Type Support**: Handle various Anki card types
- **Tag Migration**: Import and preserve card tags
- **Deck Hierarchy**: Support for nested deck structures
- **Scheduling Data**: Optional import of learning statistics

### What is an Anki File?

Anki is a popular spaced repetition flashcard application. Anki decks are typically 
exported as `.apkg` files, which contain:
- Card content (front and back)
- Media files (images, audio, video)
- Formatting and styling
- Tags and metadata
- Learning progress data

### How It Will Work:

1. Export your deck from Anki as an `.apkg` file
2. Upload the file here
3. Select import options (include media, tags, etc.)
4. Preview the converted flashcards
5. Import into the system

Stay tuned for updates!
""")

# Mock interface for demonstration
with st.expander("🔍 Preview: Future Interface"):
    st.subheader("Anki File Upload")
    st.file_uploader("Choose an .apkg file", type=['apkg'], disabled=True)
    
    st.subheader("Import Options")
    st.checkbox("Import media files", value=True, disabled=True)
    st.checkbox("Import tags", value=True, disabled=True)
    st.checkbox("Import learning progress", value=False, disabled=True)
    st.checkbox("Preserve formatting", value=True, disabled=True)
    
    st.button("Import Anki Deck", type="primary", disabled=True)

st.markdown("---")
st.caption("Check back later for this feature!")
