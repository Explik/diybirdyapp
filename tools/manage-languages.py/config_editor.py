"""
Shared configuration editor UI component.
Provides a reusable form for editing language configurations.
"""

import streamlit as st
from shared.language_client import CONFIG_TYPES


def format_language_code_display(lang_code: str) -> str:
    """Format language code for display (e.g., 'en-US' -> 'English (United States) - en-US')"""
    # Common language mappings
    lang_names = {
        'af': 'Afrikaans', 'ar': 'Arabic', 'bg': 'Bulgarian', 'bn': 'Bengali',
        'ca': 'Catalan', 'cs': 'Czech', 'da': 'Danish', 'de': 'German',
        'el': 'Greek', 'en': 'English', 'es': 'Spanish', 'et': 'Estonian',
        'fi': 'Finnish', 'fr': 'French', 'gu': 'Gujarati', 'he': 'Hebrew',
        'hi': 'Hindi', 'hr': 'Croatian', 'hu': 'Hungarian', 'id': 'Indonesian',
        'is': 'Icelandic', 'it': 'Italian', 'ja': 'Japanese', 'kn': 'Kannada',
        'ko': 'Korean', 'lt': 'Lithuanian', 'lv': 'Latvian', 'ml': 'Malayalam',
        'mr': 'Marathi', 'ms': 'Malay', 'nb': 'Norwegian', 'nl': 'Dutch',
        'no': 'Norwegian', 'pl': 'Polish', 'pt': 'Portuguese', 'ro': 'Romanian',
        'ru': 'Russian', 'sk': 'Slovak', 'sl': 'Slovenian', 'sr': 'Serbian',
        'sv': 'Swedish', 'ta': 'Tamil', 'te': 'Telugu', 'th': 'Thai',
        'tr': 'Turkish', 'uk': 'Ukrainian', 'ur': 'Urdu', 'vi': 'Vietnamese',
        'zh': 'Chinese', 'zh-CN': 'Chinese (Simplified)', 'zh-TW': 'Chinese (Traditional)',
        'cmn': 'Chinese (Mandarin)', 'yue': 'Chinese (Cantonese)',
        'iw': 'Hebrew', 'jw': 'Javanese', 'fa': 'Persian', 'ne': 'Nepali',
        'si': 'Sinhala', 'km': 'Khmer', 'lo': 'Lao', 'my': 'Burmese',
        'ka': 'Georgian', 'am': 'Amharic', 'az': 'Azerbaijani', 'eu': 'Basque',
        'be': 'Belarusian', 'bs': 'Bosnian', 'ceb': 'Cebuano', 'co': 'Corsican',
        'eo': 'Esperanto', 'fy': 'Frisian', 'gd': 'Scots Gaelic', 'gl': 'Galician',
        'ha': 'Hausa', 'haw': 'Hawaiian', 'hmn': 'Hmong', 'ht': 'Haitian Creole',
        'ig': 'Igbo', 'ga': 'Irish', 'jv': 'Javanese', 'kk': 'Kazakh', 'ky': 'Kyrgyz',
        'ku': 'Kurdish', 'la': 'Latin', 'lb': 'Luxembourgish', 'mg': 'Malagasy',
        'mi': 'Maori', 'mk': 'Macedonian', 'mn': 'Mongolian', 'mt': 'Maltese',
        'ny': 'Chichewa', 'or': 'Odia', 'ps': 'Pashto', 'pa': 'Punjabi',
        'sm': 'Samoan', 'sn': 'Shona', 'sd': 'Sindhi', 'so': 'Somali',
        'st': 'Sesotho', 'su': 'Sundanese', 'sw': 'Swahili', 'tg': 'Tajik',
        'tl': 'Tagalog', 'tt': 'Tatar', 'tk': 'Turkmen', 'ug': 'Uyghur',
        'uz': 'Uzbek', 'cy': 'Welsh', 'xh': 'Xhosa', 'yi': 'Yiddish',
        'yo': 'Yoruba', 'zu': 'Zulu'
    }
    
    # Country/region mappings
    region_names = {
        'US': 'United States', 'GB': 'United Kingdom', 'AU': 'Australia',
        'CA': 'Canada', 'IN': 'India', 'IE': 'Ireland', 'ZA': 'South Africa',
        'NZ': 'New Zealand', 'SG': 'Singapore', 'PH': 'Philippines',
        'ES': 'Spain', 'MX': 'Mexico', 'AR': 'Argentina', 'CO': 'Colombia',
        'FR': 'France', 'BE': 'Belgium', 'CH': 'Switzerland',
        'DE': 'Germany', 'AT': 'Austria', 'IT': 'Italy',
        'BR': 'Brazil', 'PT': 'Portugal', 'CN': 'China', 'TW': 'Taiwan',
        'HK': 'Hong Kong', 'JP': 'Japan', 'KR': 'Korea',
        'RU': 'Russia', 'UA': 'Ukraine', 'PL': 'Poland', 'CZ': 'Czech Republic',
        'NL': 'Netherlands', 'SE': 'Sweden', 'NO': 'Norway', 'DK': 'Denmark',
        'FI': 'Finland', 'TR': 'Turkey', 'GR': 'Greece', 'RO': 'Romania',
        'HU': 'Hungary', 'SK': 'Slovakia', 'BG': 'Bulgaria', 'HR': 'Croatia',
        'SI': 'Slovenia', 'LT': 'Lithuania', 'LV': 'Latvia', 'EE': 'Estonia',
        'IS': 'Iceland', 'IE': 'Ireland', 'ID': 'Indonesia', 'MY': 'Malaysia',
        'TH': 'Thailand', 'VN': 'Vietnam', 'PK': 'Pakistan', 'BD': 'Bangladesh'
    }
    
    parts = lang_code.split('-')
    if len(parts) == 2:
        lang = lang_names.get(parts[0], 'Unidentified')
        region = region_names.get(parts[1], parts[1])
        return f"{lang} ({region}) - {lang_code}"
    elif len(parts) == 1:
        lang = lang_names.get(parts[0], 'Unidentified')
        return f"{lang} - {lang_code}"
    
    return f"Unidentified - {lang_code}"


def render_config_editor(config_type_key, existing_config=None, language_client=None):
    """
    Render configuration editor fields based on config type.
    
    Args:
        config_type_key: The type of configuration to edit
        existing_config: Optional existing configuration data to pre-populate fields
        language_client: LanguageClient instance for API calls
    
    Returns:
        dict: Configuration data from the form
    """
    if language_client is None:
        st.error("Language client is required")
        return None
    
    config_data = {
        "type": config_type_key
    }
    
    if existing_config:
        config_data["id"] = existing_config.get("id")
    
    # Type-specific fields
    if config_type_key == "google-text-to-speech":
        st.markdown("**Google Text-to-Speech Configuration**")
        
        # Initialize variables
        language_code = ""
        voice_name = ""
        
        # Step 1: Select Language Code
        if 'tts_language_codes' not in st.session_state:
            try:
                with st.spinner("Loading available language codes from backend..."):
                    response = language_client.get_available_config_options([config_type_key])
                    
                    # Debug: Show response
                    with st.expander("Debug: API Response", expanded=False):
                        st.json(response)
                    
                    options = response.get("availableOptions", [])
                    if not options:
                        st.warning(f"⚠️ API returned no options. Response keys: {list(response.keys())}")
                    
                    st.session_state['tts_language_codes'] = [opt["id"] for opt in options]
            except Exception as e:
                st.error(f"Failed to fetch language codes: {str(e)}")
                import traceback
                st.code(traceback.format_exc())
                st.session_state['tts_language_codes'] = []
        
        language_codes = st.session_state.get('tts_language_codes', [])
        
        if not language_codes:
            st.warning("No language codes available. Please check your backend configuration.")
        else:
            # Find default index if editing existing config
            existing_lang_code = existing_config.get('languageCode', '') if existing_config else ''
            default_lang_index = 0
            if existing_lang_code and existing_lang_code in language_codes:
                default_lang_index = language_codes.index(existing_lang_code)
            
            selected_language_code = st.selectbox(
                "Language Code",
                options=language_codes,
                index=default_lang_index,
                format_func=format_language_code_display,
                help="Select the language for text-to-speech",
                key="tts_language_selector"
            )
            
            language_code = selected_language_code
            
            # Step 2: Show voice dropdown only after language is selected
            if selected_language_code:
                # Clear voices cache if language changed
                cache_key = f'tts_voices_{selected_language_code}'
                if cache_key not in st.session_state:
                    try:
                        with st.spinner(f"Loading available voices for {selected_language_code}..."):
                            response = language_client.get_available_config_options([config_type_key, selected_language_code])
                            options = response.get("availableOptions", [])
                            st.session_state[cache_key] = options
                    except Exception as e:
                        st.error(f"Failed to fetch voices: {str(e)}")
                        import traceback
                        st.code(traceback.format_exc())
                        st.session_state[cache_key] = []
                
                voices = st.session_state.get(cache_key, [])
                
                if not voices:
                    st.warning("No voices available for this language.")
                else:
                    # Create voice options with display text
                    voice_options = {}
                    for voice in voices:
                        voice_data = voice["option"]
                        name = voice_data.get("name", "")
                        ssml_gender = voice_data.get("ssmlGender", "")
                        sample_rate = voice_data.get("naturalSampleRateHertz", "")
                        display_text = f"{name} - {ssml_gender} ({sample_rate} Hz)"
                        voice_options[name] = display_text
                    
                    voice_names = list(voice_options.keys())
                    
                    # Find default index if editing existing config
                    existing_voice_name = existing_config.get('voiceName', '') if existing_config else ''
                    default_voice_index = 0
                    if existing_voice_name and existing_voice_name in voice_names:
                        default_voice_index = voice_names.index(existing_voice_name)
                    
                    selected_voice_name = st.selectbox(
                        "Voice",
                        options=voice_names,
                        index=default_voice_index,
                        format_func=lambda x: voice_options[x],
                        help=f"Select a voice for {selected_language_code}. Showing {len(voices)} available voice(s).",
                        key="tts_voice_selector"
                    )
                    
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
        
        # Initialize variables
        language_code = ""
        
        # Fetch available languages from backend
        if 'translate_language_codes' not in st.session_state:
            try:
                with st.spinner("Loading available languages from backend..."):
                    response = language_client.get_available_config_options([config_type_key])
                    
                    # Debug: Show response
                    with st.expander("Debug: API Response", expanded=False):
                        st.json(response)
                    
                    options = response.get("availableOptions", [])
                    if not options:
                        st.warning(f"⚠️ API returned no options. Response keys: {list(response.keys())}")
                    
                    st.session_state['translate_language_codes'] = [opt["id"] for opt in options]
            except Exception as e:
                st.error(f"Failed to fetch language codes: {str(e)}")
                import traceback
                st.code(traceback.format_exc())
                st.session_state['translate_language_codes'] = []
        
        language_codes = st.session_state.get('translate_language_codes', [])
        
        if not language_codes:
            st.warning("No language codes available. Please check your backend configuration.")
        else:
            # Find default index if editing existing config
            existing_lang_code = existing_config.get('languageCode', '') if existing_config else ''
            default_lang_index = 0
            if existing_lang_code and existing_lang_code in language_codes:
                default_lang_index = language_codes.index(existing_lang_code)
            
            language_code = st.selectbox(
                "Language",
                options=language_codes,
                index=default_lang_index,
                format_func=format_language_code_display,
                help=f"Select the language for translation. Showing {len(language_codes)} available language(s).",
                key="translate_language_selector"
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
