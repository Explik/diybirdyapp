"""
Page: Create Language
Create new languages in the backend.
"""

import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))

from login_utils import render_login_sidebar, require_login
from shared.language_client import LanguageClient

st.set_page_config(page_title="Create Language", page_icon="➕", layout="wide")

# Require login for this page
require_login()

st.title("➕ Create Language")

st.markdown("""
Create new languages in the backend. Each language requires a unique ID, name, and abbreviation.
""")

st.markdown("---")

def get_client():
    """Get an authenticated LanguageClient"""
    return LanguageClient(
        st.session_state.backend_url,
        st.session_state.session_cookie
    )


st.subheader("Language Details")

st.markdown("""
Enter the details for the new language. All fields are required.
""")

with st.form("create_language_form", border=False):
    language_id = st.text_input(
        "Language ID *",
        placeholder="e.g., en, es, fr, de, zh",
        help="Unique identifier for the language (typically ISO 639-1 code)"
    )
    language_name = st.text_input(
        "Language Name *",
        placeholder="e.g., English, Spanish, French",
        help="Full name of the language"
    )
    language_abbreviation = st.text_input(
        "Abbreviation *",
        placeholder="e.g., EN, ES, FR",
        help="Short abbreviation for the language (typically uppercase)"
    )
    
    st.markdown("---")
    
    submit_button = st.form_submit_button("Create Language", type="primary", use_container_width=True)
    
    if submit_button:
        # Validate inputs
        if not language_id or not language_name or not language_abbreviation:
            st.error("❌ All fields are required!")
        else:
            # Create language data
            language_data = {
                "id": language_id.strip(),
                "name": language_name.strip(),
                "abbreviation": language_abbreviation.strip()
            }
            
            # Call API
            with st.spinner("Creating language..."):
                client = get_client()
                success, message = client.create_language(language_data)
                
                if success:
                    st.success(f"✅ {message}")
                    st.balloons()
                    st.info("💡 You can now attach configurations to this language using the 'Create/Update Config' page.")
                else:
                    st.error(f"❌ {message}")