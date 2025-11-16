"""
Page: Update Configuration
Allows users to update existing configurations for languages.
"""

import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))

from login_utils import render_login_sidebar, require_login
from shared.language_client import LanguageClient, CONFIG_TYPES
from config_editor import render_config_editor, validate_config_data

st.set_page_config(page_title="Update Config", page_icon="🔧", layout="wide")

# Require login for this page
require_login()

st.title("🔧 Update Configuration")

st.markdown("""
Update existing configurations for languages. 
Select a language, choose an existing configuration, and modify the details.
""")

st.markdown("---")

def get_client():
    """Get an authenticated LanguageClient"""
    return LanguageClient(
        st.session_state.backend_url,
        st.session_state.session_cookie
    )


# Step 1: Select Configuration
client = get_client()

try:
    languages = client.get_languages()
except Exception as e:
    st.error(f"Failed to fetch languages: {str(e)}")
    languages = []

if not languages:
    st.warning("No languages available. Please check your connection.")
    st.stop()

# Get query parameters
query_params = st.query_params
language_id_from_query = query_params.get("languageId")
config_type_from_query = query_params.get("configType")
config_id_from_query = query_params.get("configId")

language_options = {f"{lang['name']} ({lang['id']})": lang for lang in languages}

# Find the index of the language to preselect
language_default_index = 0
if language_id_from_query:
    for idx, (key, lang) in enumerate(language_options.items()):
        if lang['id'] == language_id_from_query:
            language_default_index = idx
            break

selected_language_name = st.selectbox(
    "Choose a language",
    options=list(language_options.keys()),
    index=language_default_index,
    help="Select the language to update configuration for",
    key="language_selector"
)

selected_language = language_options[selected_language_name]

# Update query parameter for language
if selected_language['id'] != language_id_from_query:
    st.query_params["languageId"] = selected_language['id']

# Find the index of the config type to preselect
config_type_default_index = 0
if config_type_from_query and config_type_from_query in CONFIG_TYPES:
    config_type_default_index = list(CONFIG_TYPES.keys()).index(config_type_from_query)

selected_config_type_key = st.selectbox(
    "Choose configuration type",
    options=list(CONFIG_TYPES.keys()),
    index=config_type_default_index,
    format_func=lambda x: CONFIG_TYPES[x],
    help="Select the type of configuration to update",
    key="config_type_selector"
)

# Update query parameter for config type
if selected_config_type_key != config_type_from_query:
    st.query_params["configType"] = selected_config_type_key

# Fetch existing configurations of the selected type
try:
    existing_configs = client.get_language_configs(selected_language['id'], selected_config_type_key)
except Exception as e:
    st.error(f"Failed to fetch configurations: {str(e)}")
    existing_configs = []

if not existing_configs:
    st.warning(f"No existing {CONFIG_TYPES[selected_config_type_key]} configurations found for this language.")
    st.info("💡 Tip: Use the 'Create Config' page to create a new configuration first.")
    st.stop()

# If only one configuration exists, select it automatically
if len(existing_configs) == 1:
    selected_config = existing_configs[0]
    st.info(f"✓ Auto-selected the only available configuration (ID: {selected_config.get('id', 'N/A')})")
    # Update query parameter for config id
    if selected_config.get('id') != config_id_from_query:
        st.query_params["configId"] = selected_config.get('id', '')
else:
    config_options = {
        f"{CONFIG_TYPES.get(cfg.get('type', 'N/A'), cfg.get('type', 'N/A'))} ({cfg.get('id', 'Unknown')})": cfg 
        for cfg in existing_configs
    }

    # Find the index of the config to preselect
    config_default_index = 0
    if config_id_from_query:
        for idx, (key, cfg) in enumerate(config_options.items()):
            if cfg.get('id') == config_id_from_query:
                config_default_index = idx
                break

    selected_config_name = st.selectbox(
        "Choose configuration to update",
        options=list(config_options.keys()),
        index=config_default_index,
        help="Select the configuration you want to modify",
        key="config_selector"
    )

    selected_config = config_options[selected_config_name]

    st.info(f"Selected Configuration ID: {selected_config.get('id', 'N/A')}")
    
    # Update query parameter for config id
    if selected_config.get('id') != config_id_from_query:
        st.query_params["configId"] = selected_config.get('id', '')

st.markdown("---")

# Configuration Details
config_data = render_config_editor(selected_config_type_key, selected_config)


update_button = st.button("💾 Save Changes", type="primary")

if update_button:
    # Validate configuration data
    is_valid, error_message = validate_config_data(selected_config_type_key, config_data)
    
    if not is_valid:
        st.error(f"❌ {error_message}")
    else:
        with st.spinner("Updating configuration..."):
            success, message = client.create_config(selected_language['id'], config_data)
            
            if success:
                st.success(f"✅ {message}")
                st.balloons()
                st.info("🔄 Configuration updated! You may want to refresh the 'View Languages' page to see the changes.")
            else:
                st.error(f"❌ {message}")