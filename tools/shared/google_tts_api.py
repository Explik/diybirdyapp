"""
Google Cloud Text-to-Speech API utilities.
Provides functions for listing available voices and voice information.
"""

from typing import List, Dict, Optional


def list_voices(language_filter: Optional[str] = None) -> List[Dict]:
    """
    Lists the available voices from Google Text-to-Speech API.
    
    Args:
        language_filter: Optional language code to filter voices (e.g., 'en-US', 'es-ES').
                        If None, all voices are returned.
    
    Returns:
        List of dictionaries containing voice information with keys:
        - name: Voice name (e.g., 'en-US-Wavenet-A')
        - language_codes: List of supported language codes
        - ssml_gender: SSML voice gender ('MALE', 'FEMALE', 'NEUTRAL')
        - natural_sample_rate_hertz: Natural sample rate in Hz
    
    Raises:
        ImportError: If google-cloud-texttospeech is not installed
        Exception: If the API request fails
    """
    try:
        from google.cloud import texttospeech
    except ImportError:
        raise ImportError(
            "google-cloud-texttospeech is not installed. "
            "Install it with: pip install google-cloud-texttospeech"
        )
    
    client = texttospeech.TextToSpeechClient()
    
    # Performs the list voices request
    voices = client.list_voices()
    
    result = []
    for voice in voices.voices:
        # Check if we should include this voice based on language filter
        if language_filter:
            # Check if any of the voice's language codes match the filter
            if not any(language_filter.lower() in lang_code.lower() 
                      for lang_code in voice.language_codes):
                continue
        
        ssml_gender = texttospeech.SsmlVoiceGender(voice.ssml_gender)
        
        voice_info = {
            'name': voice.name,
            'language_codes': list(voice.language_codes),
            'ssml_gender': ssml_gender.name,
            'natural_sample_rate_hertz': voice.natural_sample_rate_hertz
        }
        result.append(voice_info)
    
    return result


def get_unique_language_codes() -> List[str]:
    """
    Get a sorted list of all unique language codes supported by Google TTS.
    
    Returns:
        Sorted list of unique language codes (e.g., ['af-ZA', 'ar-XA', 'en-US', ...])
    
    Raises:
        ImportError: If google-cloud-texttospeech is not installed
        Exception: If the API request fails
    """
    voices = list_voices()
    language_codes = set()
    
    for voice in voices:
        language_codes.update(voice['language_codes'])
    
    return sorted(list(language_codes))


def format_voice_display(voice: Dict) -> str:
    """
    Format a voice dictionary for display in UI.
    
    Args:
        voice: Voice dictionary with 'name', 'language_codes', 'ssml_gender', etc.
    
    Returns:
        Formatted string for display
    """
    lang_codes = ', '.join(voice['language_codes'])
    return f"{voice['name']} ({lang_codes}) - {voice['ssml_gender']}"


def get_language_name(language_code: str) -> str:
    """
    Convert a language code to a human-readable name.
    
    Args:
        language_code: BCP-47 language code (e.g., 'en-US', 'es-ES', 'fr-FR')
    
    Returns:
        Human-readable language name (e.g., 'English (United States)', 'Spanish (Spain)')
    """
    # Map of common language codes to names
    # Format: 'language-REGION' -> ('Language', 'Region')
    language_map = {
        'af': 'Afrikaans',
        'ar': 'Arabic',
        'bg': 'Bulgarian',
        'bn': 'Bengali',
        'ca': 'Catalan',
        'cmn': 'Mandarin Chinese',
        'cs': 'Czech',
        'da': 'Danish',
        'de': 'German',
        'el': 'Greek',
        'en': 'English',
        'es': 'Spanish',
        'et': 'Estonian',
        'eu': 'Basque',
        'fi': 'Finnish',
        'fil': 'Filipino',
        'fr': 'French',
        'gl': 'Galician',
        'gu': 'Gujarati',
        'he': 'Hebrew',
        'hi': 'Hindi',
        'hr': 'Croatian',
        'hu': 'Hungarian',
        'id': 'Indonesian',
        'is': 'Icelandic',
        'it': 'Italian',
        'ja': 'Japanese',
        'kn': 'Kannada',
        'ko': 'Korean',
        'lt': 'Lithuanian',
        'lv': 'Latvian',
        'ml': 'Malayalam',
        'mr': 'Marathi',
        'ms': 'Malay',
        'nb': 'Norwegian',
        'nl': 'Dutch',
        'pl': 'Polish',
        'pt': 'Portuguese',
        'ro': 'Romanian',
        'ru': 'Russian',
        'sk': 'Slovak',
        'sl': 'Slovenian',
        'sr': 'Serbian',
        'sv': 'Swedish',
        'ta': 'Tamil',
        'te': 'Telugu',
        'th': 'Thai',
        'tr': 'Turkish',
        'uk': 'Ukrainian',
        'ur': 'Urdu',
        'vi': 'Vietnamese',
        'yue': 'Cantonese',
        'zh': 'Chinese',
    }
    
    region_map = {
        'AU': 'Australia',
        'BR': 'Brazil',
        'CA': 'Canada',
        'CN': 'China',
        'DE': 'Germany',
        'ES': 'Spain',
        'FR': 'France',
        'GB': 'United Kingdom',
        'HK': 'Hong Kong',
        'IE': 'Ireland',
        'IN': 'India',
        'IT': 'Italy',
        'JP': 'Japan',
        'KR': 'South Korea',
        'MX': 'Mexico',
        'NL': 'Netherlands',
        'PL': 'Poland',
        'PT': 'Portugal',
        'RU': 'Russia',
        'SE': 'Sweden',
        'TR': 'Turkey',
        'TW': 'Taiwan',
        'UA': 'Ukraine',
        'US': 'United States',
        'XA': 'General',
        'ZA': 'South Africa',
    }
    
    # Parse language code
    parts = language_code.split('-')
    lang_code = parts[0].lower()
    region_code = parts[1] if len(parts) > 1 else None
    
    # Get language name
    language = language_map.get(lang_code, lang_code.upper())
    
    # Get region name if available
    if region_code:
        region = region_map.get(region_code, region_code)
        return f"{language} ({region})"
    else:
        return language


def format_language_code_display(language_code: str) -> str:
    """
    Format a language code for display with human-readable name.
    
    Args:
        language_code: BCP-47 language code (e.g., 'en-US')
    
    Returns:
        Formatted string like "English (United States) - en-US"
    """
    name = get_language_name(language_code)
    return f"{name} - {language_code}"
