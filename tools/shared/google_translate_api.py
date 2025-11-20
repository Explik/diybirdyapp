"""
Google Cloud Translation API utilities.
Provides functions for listing available languages and language information.
"""

from typing import List, Dict, Optional


def list_languages(target_language: Optional[str] = 'en') -> List[Dict]:
    """
    Lists the available languages from Google Translation API.
    
    Args:
        target_language: Language code for displaying the language names.
                        Defaults to 'en' (English).
                        If None, only language codes are returned without names.
    
    Returns:
        List of dictionaries containing language information with keys:
        - language: Language code (e.g., 'en', 'es', 'fr')
        - name: Language name in the target language (if target_language is provided)
    
    Raises:
        ImportError: If google-cloud-translate is not installed
        Exception: If the API request fails
    """
    try:
        from google.cloud import translate_v2 as translate
    except ImportError:
        raise ImportError(
            "google-cloud-translate is not installed. "
            "Install it with: pip install google-cloud-translate"
        )
    
    client = translate.Client()
    
    # Get the list of supported languages
    languages = client.get_languages(target_language=target_language)
    
    result = []
    for language in languages:
        language_info = {
            'language': language['language'],
            'name': language.get('name', language['language'])
        }
        result.append(language_info)
    
    return result


def get_language_codes() -> List[str]:
    """
    Get a sorted list of all language codes supported by Google Translate.
    
    Returns:
        Sorted list of language codes (e.g., ['af', 'ar', 'en', 'es', ...])
    
    Raises:
        ImportError: If google-cloud-translate is not installed
        Exception: If the API request fails
    """
    languages = list_languages(target_language=None)
    language_codes = [lang['language'] for lang in languages]
    return sorted(language_codes)


def format_language_display(language_code: str, language_name: str) -> str:
    """
    Format a language for display in UI.
    
    Args:
        language_code: Language code (e.g., 'en', 'es')
        language_name: Human-readable language name (e.g., 'English', 'Spanish')
    
    Returns:
        Formatted string for display
    """
    return f"{language_name} - {language_code}"


def get_language_info(language_code: str, target_language: str = 'en') -> Dict:
    """
    Get information about a specific language.
    
    Args:
        language_code: The language code to get info for (e.g., 'es', 'fr')
        target_language: Language code for displaying the language name (default: 'en')
    
    Returns:
        Dictionary with language information:
        - language: Language code
        - name: Language name in the target language
    
    Raises:
        ValueError: If the language code is not found
    """
    languages = list_languages(target_language=target_language)
    
    for lang in languages:
        if lang['language'] == language_code:
            return lang
    
    raise ValueError(f"Language code '{language_code}' not found in supported languages")
