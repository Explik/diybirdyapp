"""
Shared configuration editor UI component.
Provides a reusable form for editing language configurations.
"""

import streamlit as st
from shared.language_client import CONFIG_TYPES


def render_config_editor(config_type_key, existing_config=None):
    """
    Render configuration editor fields based on config type.
    
    Args:
        config_type_key: The type of configuration to edit
        existing_config: Optional existing configuration data to pre-populate fields
    
    Returns:
        dict: Configuration data from the form
    """
    config_data = {
        "type": config_type_key
    }
    
    if existing_config:
        config_data["id"] = existing_config.get("id")
    
    # Type-specific fields
    if config_type_key == "google-text-to-speech":
        st.markdown("**Google Text-to-Speech Configuration**")
        
        language_code = st.text_input(
            "Language Code",
            value=existing_config.get('languageCode', '') if existing_config else '',
            help="e.g., en-US, es-ES, fr-FR",
            placeholder="en-US"
        )
    
        voice_name = st.text_input(
            "Voice Name",
            value=existing_config.get('voiceName', '') if existing_config else '',
            help="e.g., en-US-Wavenet-A",
            placeholder="en-US-Wavenet-A"
        )
        
        config_data["languageCode"] = language_code
        config_data["voiceName"] = voice_name
    
    elif config_type_key == "microsoft-text-to-speech":
        st.markdown("**Microsoft Text-to-Speech Configuration**")
        st.info("This configuration type has no additional fields.")
    
    elif config_type_key == "google-speech-to-text":
        st.markdown("**Google Speech-to-Text Configuration**")
        st.info("This configuration type has no additional fields.")
    
    elif config_type_key == "google-translate":
        st.markdown("**Google Translate Configuration**")
        st.info("This configuration type has no additional fields.")
    
    # Preview configuration data
    with st.expander("Preview Configuration JSON"):
        st.json(config_data)
    
    return config_data


def validate_config_data(config_type_key, config_data):
    """
    Validate configuration data based on type.
    
    Args:
        config_type_key: The type of configuration
        config_data: The configuration data to validate
    
    Returns:
        tuple: (is_valid: bool, error_message: str or None)
    """
    if config_type_key == "google-text-to-speech":
        if not config_data.get("languageCode"):
            return False, "Language Code is required for Google Text-to-Speech"
        if not config_data.get("voiceName"):
            return False, "Voice Name is required for Google Text-to-Speech"
    
    return True, None
