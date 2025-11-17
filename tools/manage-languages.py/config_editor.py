"""
Shared configuration editor UI component.
Provides a reusable form for editing language configurations.
"""

import streamlit as st
from shared.language_client import CONFIG_TYPES
from shared.google_tts_api import list_voices, format_language_code_display
from shared.google_translate_api import list_languages, format_language_display

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
        
        # Fetch all voices on first load
        if 'tts_all_voices' not in st.session_state:
            try:
                with st.spinner("Loading available voices from Google Cloud..."):
                    st.session_state['tts_all_voices'] = list_voices()
                    
                    # Extract unique language codes from voices
                    language_codes_set = set()
                    for voice in st.session_state['tts_all_voices']:
                        language_codes_set.update(voice['language_codes'])
                    st.session_state['tts_language_codes'] = sorted(list(language_codes_set))
                    
            except ImportError as e:
                st.error(f"❌ {str(e)}")
                st.info("Install the required package: `pip install google-cloud-texttospeech`")
                st.session_state['tts_all_voices'] = []
                st.session_state['tts_language_codes'] = []
            except Exception as e:
                st.error(f"Failed to fetch voices: {str(e)}")
                st.info("Make sure your Google Cloud credentials are properly configured.")
                st.session_state['tts_all_voices'] = []
                st.session_state['tts_language_codes'] = []
        
        all_voices = st.session_state.get('tts_all_voices', [])
        language_codes = st.session_state.get('tts_language_codes', [])
        
        if not all_voices:
            st.warning("No voices available. Please check your Google Cloud configuration.")
            language_code = ""
            voice_name = ""
        else:
            # Step 1: Select Language Code
            # Find default index if editing existing config
            existing_lang_code = existing_config.get('languageCode', '') if existing_config else ''
            default_lang_index = 0
            if existing_lang_code and existing_lang_code in language_codes:
                default_lang_index = language_codes.index(existing_lang_code)
            
            selected_language_code = st.selectbox(
                "Language",
                options=language_codes,
                index=default_lang_index,
                format_func=format_language_code_display,
                help="Select the language for text-to-speech"
            )
            
            # Step 2: Filter voices by selected language code
            filtered_voices = [
                voice for voice in all_voices 
                if selected_language_code in voice['language_codes']
            ]
            
            # Create voice options with display text
            voice_options = {}
            for voice in filtered_voices:
                display_text = f"{voice['name']} - {voice['ssml_gender']} ({voice['natural_sample_rate_hertz']} Hz)"
                voice_options[voice['name']] = display_text
            
            # Find default index if editing existing config
            existing_voice_name = existing_config.get('voiceName', '') if existing_config else ''
            voice_names = list(voice_options.keys())
            default_voice_index = 0
            if existing_voice_name and existing_voice_name in voice_names:
                default_voice_index = voice_names.index(existing_voice_name)
            
            selected_voice_name = st.selectbox(
                "Voice",
                options=voice_names,
                index=default_voice_index,
                format_func=lambda x: voice_options[x],
                help=f"Select a voice for {selected_language_code}. Showing {len(filtered_voices)} available voice(s)."
            )
            
            language_code = selected_language_code
            voice_name = selected_voice_name
        
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
        
        # Fetch all languages on first load
        if 'translate_all_languages' not in st.session_state:
            try:
                with st.spinner("Loading available languages from Google Cloud..."):
                    st.session_state['translate_all_languages'] = list_languages(target_language='en')
                    
            except ImportError as e:
                st.error(f"❌ {str(e)}")
                st.info("Install the required package: `pip install google-cloud-translate`")
                st.session_state['translate_all_languages'] = []
            except Exception as e:
                st.error(f"Failed to fetch languages: {str(e)}")
                st.info("Make sure your Google Cloud credentials are properly configured.")
                st.session_state['translate_all_languages'] = []
        
        all_languages = st.session_state.get('translate_all_languages', [])
        
        if not all_languages:
            st.warning("No languages available. Please check your Google Cloud configuration.")
            language_code = ""
        else:
            # Create language options with display text
            language_options = {}
            for lang in all_languages:
                language_options[lang['language']] = format_language_display(lang['language'], lang['name'])
            
            language_codes = list(language_options.keys())
            
            # Language Selection
            existing_lang_code = existing_config.get('languageCode', '') if existing_config else ''
            default_lang_index = 0
            if existing_lang_code and existing_lang_code in language_codes:
                default_lang_index = language_codes.index(existing_lang_code)
            
            language_code = st.selectbox(
                "Language",
                options=language_codes,
                index=default_lang_index,
                format_func=lambda x: language_options[x],
                help=f"Select the language for translation. Showing {len(all_languages)} available language(s)."
            )
        
        config_data["languageCode"] = language_code
    
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
    
    elif config_type_key == "google-translate":
        if not config_data.get("languageCode"):
            return False, "Language Code is required for Google Translate"
    
    return True, None
