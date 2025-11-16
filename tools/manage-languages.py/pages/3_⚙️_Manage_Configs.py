"""
Page: Create/Update Configuration
Allows users to create new configurations or modify existing ones for languages.
"""

import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))

from login_utils import render_login_sidebar, require_login
from shared.language_client import LanguageClient, CONFIG_TYPES

st.set_page_config(page_title="Create/Update Config", page_icon="⚙️", layout="wide")

# Require login for this page
require_login()

st.title("⚙️ Create/Update Configuration")

st.markdown("""
Create new configurations or update existing ones for languages. 
Select a language, choose the configuration type, and provide the necessary details.
""")

st.markdown("---")

def get_client():
    """Get an authenticated LanguageClient"""
    return LanguageClient(
        st.session_state.backend_url,
        st.session_state.session_cookie
    )


# Step 1: Select Language
st.subheader("1. Select Language")

client = get_client()

try:
    languages = client.get_languages()
except Exception as e:
    st.error(f"Failed to fetch languages: {str(e)}")
    languages = []

if not languages:
    st.warning("No languages available. Please check your connection.")
    st.stop()

language_options = {f"{lang['name']} ({lang['abbreviation']})": lang for lang in languages}
selected_language_name = st.selectbox(
    "Choose a language",
    options=list(language_options.keys()),
    help="Select the language to create or update configuration for"
)

selected_language = language_options[selected_language_name]

st.info(f"Selected Language ID: {selected_language['id']}")

st.markdown("---")

# Step 2: Select Configuration Type
st.subheader("2. Select Configuration Type")

selected_config_type_key = st.selectbox(
    "Choose configuration type",
    options=list(CONFIG_TYPES.keys()),
    format_func=lambda x: CONFIG_TYPES[x],
    help="Select the type of configuration to create or update"
)

st.markdown("---")

# Step 3: Select Existing or Create New
st.subheader("3. Select Configuration")

# Fetch existing configurations of the selected type
try:
    existing_configs = client.get_language_configs(selected_language['id'], selected_config_type_key)
except Exception as e:
    st.error(f"Failed to fetch configurations: {str(e)}")
    existing_configs = []

config_mode_options = ["Create new configuration"]
if existing_configs:
    config_mode_options.extend([f"Update: {cfg.get('id', 'Unknown')} - {cfg.get('type', 'N/A')}" for cfg in existing_configs])

selected_mode = st.selectbox(
    "Select action",
    options=config_mode_options,
    help="Choose to create a new configuration or update an existing one"
)

is_new = selected_mode == "Create new configuration"
selected_config = None if is_new else existing_configs[config_mode_options.index(selected_mode) - 1]

st.markdown("---")

# Step 4: Configuration Details
st.subheader("4. Configuration Details")

config_data = {
    "type": selected_config_type_key
}

if not is_new and selected_config:
    st.info(f"Updating configuration ID: {selected_config.get('id', 'N/A')}")
    config_data["id"] = selected_config.get("id")

# Type-specific fields
if selected_config_type_key == "google-text-to-speech":
    st.markdown("**Google Text-to-Speech Configuration**")
    
    col1, col2 = st.columns(2)
    
    with col1:
        language_code = st.text_input(
            "Language Code",
            value=selected_config.get('languageCode', '') if selected_config else '',
            help="e.g., en-US, es-ES, fr-FR",
            placeholder="en-US"
        )
    
    with col2:
        voice_name = st.text_input(
            "Voice Name",
            value=selected_config.get('voiceName', '') if selected_config else '',
            help="e.g., en-US-Wavenet-A",
            placeholder="en-US-Wavenet-A"
        )
    
    config_data["languageCode"] = language_code
    config_data["voiceName"] = voice_name

elif selected_config_type_key == "microsoft-text-to-speech":
    st.markdown("**Microsoft Text-to-Speech Configuration**")
    st.info("This configuration type has no additional fields.")

elif selected_config_type_key == "google-speech-to-text":
    st.markdown("**Google Speech-to-Text Configuration**")
    st.info("This configuration type has no additional fields.")

elif selected_config_type_key == "google-translate":
    st.markdown("**Google Translate Configuration**")
    st.info("This configuration type has no additional fields.")

# Preview configuration data
with st.expander("Preview Configuration JSON"):
    st.json(config_data)

st.markdown("---")

# Step 5: Save Configuration
st.subheader("5. Save Configuration")

col1, col2, col3 = st.columns([1, 1, 2])

with col1:
    save_button = st.button("💾 Save Configuration", type="primary", use_container_width=True)

with col2:
    if not is_new and selected_config:
        attach_button = st.button("🔗 Attach to Language", use_container_width=True)
    else:
        attach_button = False

if save_button:
    # Validate required fields for Google TTS
    if selected_config_type_key == "google-text-to-speech":
        if not config_data.get("languageCode"):
            st.error("❌ Language Code is required for Google Text-to-Speech")
        elif not config_data.get("voiceName"):
            st.error("❌ Voice Name is required for Google Text-to-Speech")
        else:
            with st.spinner("Saving configuration..."):
                success, message = client.create_config(selected_language['id'], config_data)
                
                if success:
                    st.success(f"✅ {message}")
                    st.balloons()
                    # Clear cache and suggest refresh
                    st.info("🔄 Configuration saved! You may want to refresh the 'View Languages' page to see the changes.")
                else:
                    st.error(f"❌ {message}")
    else:
        # For other types with no additional fields
        with st.spinner("Saving configuration..."):
            success, message = client.create_config(selected_language['id'], config_data)
            
            if success:
                st.success(f"✅ {message}")
                st.balloons()
                st.info("🔄 Configuration saved! You may want to refresh the 'View Languages' page to see the changes.")
            else:
                st.error(f"❌ {message}")

if attach_button and selected_config:
    with st.spinner("Attaching configuration..."):
        success, message = client.attach_config(
            selected_language['id'],
            selected_config.get('id'),
            selected_config_type_key
        )
        
        if success:
            st.success(f"✅ {message}")
        else:
            st.error(f"❌ {message}")

st.markdown("---")
st.info("💡 Tip: After saving, check the 'View Languages' page to verify the configuration was added.")
