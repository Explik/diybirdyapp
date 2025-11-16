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
    
    selected_language_key = st.selectbox(
        "Language",
        options=list(language_options.keys()),
        help="Choose the language you want to update"
    )
    
    selected_language = language_options[selected_language_key]
    
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
        
        col_btn1, col_btn2, col_btn3 = st.columns([1, 1, 1])
        with col_btn2:
            submit_button = st.form_submit_button("Update Language", type="primary", use_container_width=True)
        
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
    with st.expander(f"⚙️ View Configurations for {selected_language['name']}", expanded=False):
        with st.spinner("Loading configurations..."):
            try:
                configs = client.get_language_configs(selected_language['id'])
                
                if configs:
                    st.success(f"Found {len(configs)} configuration(s)")
                    
                    for idx, config in enumerate(configs, 1):
                        st.markdown(f"**Configuration {idx}**")
                        
                        config_col1, config_col2 = st.columns(2)
                        
                        with config_col1:
                            st.write(f"**Type:** {config.get('type', 'N/A')}")
                            st.write(f"**ID:** {config.get('id', 'N/A')}")
                        
                        with config_col2:
                            # Display type-specific fields
                            if config.get('type') == 'google-text-to-speech':
                                st.write(f"**Language Code:** {config.get('languageCode', 'N/A')}")
                                st.write(f"**Voice Name:** {config.get('voiceName', 'N/A')}")
                            elif config.get('type') == 'microsoft-text-to-speech':
                                st.write("**Microsoft TTS Configuration**")
                            elif config.get('type') == 'google-speech-to-text':
                                st.write("**Google STT Configuration**")
                            elif config.get('type') == 'google-translate':
                                st.write("**Google Translate Configuration**")
                        
                        if idx < len(configs):
                            st.divider()
                else:
                    st.info("No configurations found for this language")
                    
            except Exception as e:
                st.error(f"Failed to fetch configurations: {str(e)}")

else:
    st.warning("⚠️ No languages found in the backend.")
    st.info("💡 Create a new language using the 'Create Language' page.")