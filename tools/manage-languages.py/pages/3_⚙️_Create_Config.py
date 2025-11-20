"""
Page: Create Configuration
Allows users to create new configurations for languages.
"""

import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))

from login_utils import render_login_sidebar, require_login
from shared.language_client import LanguageClient, CONFIG_TYPES
from config_editor import render_config_editor, validate_config_data

st.set_page_config(page_title="Create Config", page_icon="⚙️", layout="wide")

# Require login for this page
require_login()

st.title("⚙️ Create Configuration")

st.markdown("""
Create new configurations for languages. 
Select a language, choose the configuration type, and provide the necessary details.
""")

st.markdown("---")

def get_client():
    """Get an authenticated LanguageClient"""
    return LanguageClient(
        st.session_state.backend_url,
        st.session_state.session_cookie
    )

client = get_client()

try:
    languages = client.get_languages()
except Exception as e:
    st.error(f"Failed to fetch languages: {str(e)}")
    languages = []

if not languages:
    st.warning("No languages available. Please check your connection.")
    st.stop()

language_options = {f"{lang['name']} ({lang['id']})": lang for lang in languages}
selected_language_name = st.selectbox(
    "Choose a language",
    options=list(language_options.keys()),
    help="Select the language to create configuration for"
)

selected_language = language_options[selected_language_name]

selected_config_type_key = st.selectbox(
    "Choose configuration type",
    options=list(CONFIG_TYPES.keys()),
    format_func=lambda x: CONFIG_TYPES[x],
    help="Select the type of configuration to create"
)

# Check if configurations already exist for this language and type
try:
    existing_configs = client.get_language_configs(selected_language['id'], selected_config_type_key)
    if existing_configs:
        st.warning(f"⚠️ This language already has {len(existing_configs)} configuration(s) of type '{CONFIG_TYPES[selected_config_type_key]}'. View [existing configurations](/Update_Config?languageId={selected_language['id']}&configType={selected_config_type_key}&autoLogin=true).")
except Exception as e:
    st.error(f"Failed to check existing configurations: {str(e)}")

st.markdown("---")

# Configuration Details
config_data = render_config_editor(selected_config_type_key)

create_button = st.button("💾 Create Configuration", type="primary")

if create_button:
    # Validate configuration data
    is_valid, error_message = validate_config_data(selected_config_type_key, config_data)
    
    if not is_valid:
        st.error(f"❌ {error_message}")
    else:
        with st.spinner("Creating configuration..."):
            success, message = client.create_config(selected_language['id'], config_data)
            
            if success:
                st.success(f"✅ {message}")
                st.balloons()
                st.info("🔄 Configuration created! You may want to refresh the 'View Languages' page to see the changes.")
            else:
                st.error(f"❌ {message}")
