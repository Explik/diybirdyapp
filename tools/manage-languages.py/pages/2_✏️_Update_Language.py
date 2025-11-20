"""
Page: Update Language
Update existing language properties in the backend.
"""

import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))

from login_utils import render_login_sidebar, require_login
from shared.language_client import LanguageClient

# Add current directory's parent to path to import config_viewer
sys.path.append(str(Path(__file__).parent.parent))
from config_viewer import render_config_viewer

st.set_page_config(page_title="Update Language", page_icon="✏️", layout="wide")

# Require login for this page
require_login()

st.title("✏️ Update Language")

st.markdown("""
Update properties of existing languages in the backend. You can modify the name and abbreviation.
""")

st.markdown("---")

def get_client():
    """Get an authenticated LanguageClient"""
    return LanguageClient(
        st.session_state.backend_url,
        st.session_state.session_cookie
    )


# Fetch languages
with st.spinner("Loading languages..."):
    client = get_client()
    try:
        languages = client.get_languages()
    except Exception as e:
        st.error(f"Failed to fetch languages: {str(e)}")
        languages = []

if languages:
    st.subheader("Select Language to Update")
    
    # Create a mapping for the selectbox
    language_options = {f"{lang['name']} ({lang['abbreviation']}) - {lang['id']}": lang for lang in languages}
    
    # Get languageId from query parameters
    query_params = st.query_params
    language_id_from_query = query_params.get("languageId")
    
    # Find the index of the language to preselect
    default_index = 0
    if language_id_from_query:
        for idx, (key, lang) in enumerate(language_options.items()):
            if lang['id'] == language_id_from_query:
                default_index = idx
                break
    
    selected_language_key = st.selectbox(
        "Language",
        options=list(language_options.keys()),
        index=default_index,
        help="Choose the language you want to update",
        key="language_selector"
    )
    
    selected_language = language_options[selected_language_key]
    
    # Update query parameter to match the selected language
    if selected_language['id'] != language_id_from_query:
        st.query_params["languageId"] = selected_language['id']
    
    st.markdown("---")
    st.subheader("Update Properties")
    
    with st.form("update_language_form", border=False):
        st.info(f"**Language ID:** `{selected_language['id']}` (cannot be changed)")
        
        new_name = st.text_input(
            "Language Name *",
            value=selected_language['name'],
            help="Full name of the language"
        )
    
        new_abbreviation = st.text_input(
            "Abbreviation *",
            value=selected_language['abbreviation'],
            help="Short abbreviation for the language"
        )
        
        submit_button = st.form_submit_button("Update Language", type="primary")
        
        if submit_button:
            # Validate inputs
            if not new_name or not new_abbreviation:
                st.error("❌ All fields are required!")
            else:
                # Check if anything changed
                if new_name == selected_language['name'] and new_abbreviation == selected_language['abbreviation']:
                    st.warning("⚠️ No changes detected. Please modify at least one field.")
                else:
                    # Create update data
                    update_data = {
                        "id": selected_language['id'],
                        "name": new_name.strip(),
                        "abbreviation": new_abbreviation.strip()
                    }
                    
                    # Call API
                    with st.spinner("Updating language..."):
                        client = get_client()
                        success, message = client.update_language(selected_language['id'], update_data)
                        
                        if success:
                            st.success(f"✅ {message}")
                            st.balloons()
                            # Force a rerun to refresh the language list
                            st.rerun()
                        else:
                            st.error(f"❌ {message}")
    
    st.markdown("---")
    
    # Show current configurations for the selected language
    st.subheader(f"Configurations for {selected_language['name']}")
    render_config_viewer(client, selected_language['id'], "all", show_type_filter=True)

else:
    st.warning("⚠️ No languages found in the backend.")
    st.info("💡 Create a new language using the 'Create Language' page.")