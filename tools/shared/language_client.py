"""
Shared language client utilities for managing language configurations.
Provides reusable functions for language and configuration operations.
"""

import requests
from typing import List, Dict, Optional, Tuple


class LanguageClient:
    """Client for interacting with language and configuration APIs"""
    
    def __init__(self, base_url: str, session_cookie: Optional[str] = None):
        """
        Initialize the language client.
        
        Args:
            base_url: Base URL of the backend API (e.g., "http://localhost:8080")
            session_cookie: Optional JSESSIONID for authenticated requests
        """
        self.base_url = base_url.rstrip('/')
        self.session_cookie = session_cookie
        self.timeout = 10
    
    def _get_cookies(self) -> Dict[str, str]:
        """Get cookies dict for requests"""
        return {'JSESSIONID': self.session_cookie} if self.session_cookie else {}
    
    def get_languages(self) -> List[Dict]:
        """
        Fetch all languages from the backend.
        
        Returns:
            List of language dictionaries with 'id', 'name', 'abbreviation'
            
        Raises:
            Exception: If the request fails
        """
        url = f"{self.base_url}/language"
        response = requests.get(url, cookies=self._get_cookies(), timeout=self.timeout)
        response.raise_for_status()
        return response.json()
    
    def get_language_by_id(self, language_id: str) -> Optional[Dict]:
        """
        Get a language by its ID.
        
        Args:
            language_id: The language ID
            
        Returns:
            Language dictionary or None if not found
        """
        languages = self.get_languages()
        return next((lang for lang in languages if lang['id'] == language_id), None)
    
    def get_language_by_abbreviation(self, abbreviation: str) -> Optional[Dict]:
        """
        Get a language by its abbreviation.
        
        Args:
            abbreviation: The language abbreviation (e.g., "en", "es")
            
        Returns:
            Language dictionary or None if not found
        """
        languages = self.get_languages()
        return next((lang for lang in languages if lang['abbreviation'] == abbreviation), None)
    
    def get_language_by_name(self, name: str) -> Optional[Dict]:
        """
        Get a language by its name.
        
        Args:
            name: The language name (e.g., "English", "Spanish")
            
        Returns:
            Language dictionary or None if not found
        """
        languages = self.get_languages()
        return next((lang for lang in languages if lang['name'] == name), None)
    
    def get_language_configs(self, language_id: str, config_type: Optional[str] = None) -> List[Dict]:
        """
        Fetch configurations for a specific language.
        
        Args:
            language_id: The ID of the language
            config_type: Optional filter by configuration type
            
        Returns:
            List of configuration dictionaries
            
        Raises:
            Exception: If the request fails
        """
        url = f"{self.base_url}/language/{language_id}/config"
        if config_type:
            url += f"?type={config_type}"
        
        response = requests.get(url, cookies=self._get_cookies(), timeout=self.timeout)
        response.raise_for_status()
        return response.json()
    
    def create_config(self, language_id: str, config_data: Dict) -> Tuple[bool, str]:
        """
        Create a new configuration for a language.
        
        Args:
            language_id: The ID of the language
            config_data: Configuration data dictionary with 'type' and type-specific fields
            
        Returns:
            Tuple of (success: bool, message: str)
        """
        try:
            url = f"{self.base_url}/language/{language_id}/create-config"
            response = requests.post(url, json=config_data, cookies=self._get_cookies(), timeout=self.timeout)
            response.raise_for_status()
            return True, "Configuration created successfully"
        except Exception as e:
            return False, f"Failed to create configuration: {str(e)}"
    
    def update_config(self, config_id: str, config_data: Dict) -> Tuple[bool, str]:
        """
        Update an existing configuration.
        
        Args:
            config_id: The ID of the configuration to update
            config_data: Configuration data dictionary with type-specific fields
            
        Returns:
            Tuple of (success: bool, message: str)
        """
        try:
            url = f"{self.base_url}/config/{config_id}"
            response = requests.put(url, json=config_data, cookies=self._get_cookies(), timeout=self.timeout)
            response.raise_for_status()
            return True, "Configuration updated successfully"
        except Exception as e:
            return False, f"Failed to update configuration: {str(e)}"
    
    def attach_config(self, language_id: str, config_id: str, config_type: str) -> Tuple[bool, str]:
        """
        Attach an existing configuration to a language.
        
        Args:
            language_id: The ID of the language
            config_id: The ID of the configuration
            config_type: The type of configuration
            
        Returns:
            Tuple of (success: bool, message: str)
        """
        try:
            url = f"{self.base_url}/language/{language_id}/attach-config"
            payload = {
                "id": config_id,
                "type": config_type
            }
            response = requests.post(url, json=payload, cookies=self._get_cookies(), timeout=self.timeout)
            response.raise_for_status()
            return True, "Configuration attached successfully"
        except Exception as e:
            return False, f"Failed to attach configuration: {str(e)}"
    
    def detach_config(self, language_id: str, config_id: str, config_type: str) -> Tuple[bool, str]:
        """
        Detach a configuration from a language.
        
        Args:
            language_id: The ID of the language
            config_id: The ID of the configuration
            config_type: The type of configuration
            
        Returns:
            Tuple of (success: bool, message: str)
        """
        try:
            url = f"{self.base_url}/language/{language_id}/detach-config"
            payload = {
                "id": config_id,
                "type": config_type
            }
            response = requests.post(url, json=payload, cookies=self._get_cookies(), timeout=self.timeout)
            response.raise_for_status()
            return True, "Configuration detached successfully"
        except Exception as e:
            return False, f"Failed to detach configuration: {str(e)}"
    
    def create_language(self, language_data: Dict) -> Tuple[bool, str]:
        """
        Create a new language.
        
        Args:
            language_data: Language data dictionary with 'id', 'name', 'abbreviation'
            
        Returns:
            Tuple of (success: bool, message: str)
        """
        try:
            url = f"{self.base_url}/language"
            response = requests.post(url, json=language_data, cookies=self._get_cookies(), timeout=self.timeout)
            response.raise_for_status()
            return True, "Language created successfully"
        except Exception as e:
            return False, f"Failed to create language: {str(e)}"
    
    def update_language(self, language_id: str, language_data: Dict) -> Tuple[bool, str]:
        """
        Update an existing language.
        
        Args:
            language_id: The ID of the language to update
            language_data: Language data dictionary with fields to update ('name', 'abbreviation')
            
        Returns:
            Tuple of (success: bool, message: str)
        """
        try:
            url = f"{self.base_url}/language/{language_id}"
            # Ensure the ID is in the payload
            payload = {**language_data, "id": language_id}
            response = requests.put(url, json=payload, cookies=self._get_cookies(), timeout=self.timeout)
            response.raise_for_status()
            return True, "Language updated successfully"
        except Exception as e:
            return False, f"Failed to update language: {str(e)}"
    
    def get_available_config_options(self, selected_options: List[str]) -> Dict:
        """
        Get available configuration options based on previous selections.
        
        Args:
            selected_options: List of previously selected options (empty for initial call)
            
        Returns:
            Dictionary with 'selection', 'selectedOptions', 'availableOptions', 'lastSelection'
            
        Raises:
            Exception: If the request fails
        """
        url = f"{self.base_url}/config/available-options"
        payload = {
            "selectedOptions": selected_options
        }
        response = requests.post(url, json=payload, cookies=self._get_cookies(), timeout=self.timeout)
        response.raise_for_status()
        return response.json()


# Configuration type constants
CONFIG_TYPE_GOOGLE_TTS = "google-text-to-speech"
CONFIG_TYPE_MICROSOFT_TTS = "microsoft-text-to-speech"
CONFIG_TYPE_GOOGLE_STT = "google-speech-to-text"
CONFIG_TYPE_GOOGLE_TRANSLATE = "google-translate"

CONFIG_TYPES = {
    CONFIG_TYPE_GOOGLE_TTS: "Google Text-to-Speech",
    CONFIG_TYPE_MICROSOFT_TTS: "Microsoft Text-to-Speech",
    CONFIG_TYPE_GOOGLE_STT: "Google Speech-to-Text",
    CONFIG_TYPE_GOOGLE_TRANSLATE: "Google Translate"
}
