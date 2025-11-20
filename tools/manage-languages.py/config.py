"""
Configuration settings for the Language Management Tool
"""

# API Configuration
API_BASE_URL = "http://localhost:8080"

# Streamlit Configuration
APP_TITLE = "Language Management Tool"
APP_ICON = "🌐"

# Configuration Types
CONFIG_TYPES = {
    "google-text-to-speech": "Google Text-to-Speech",
    "microsoft-text-to-speech": "Microsoft Text-to-Speech",
    "google-speech-to-text": "Google Speech-to-Text",
    "google-translate": "Google Translate"
}

# API Endpoints
ENDPOINTS = {
    "languages": "/language",
    "language_configs": "/language/{id}/config",
    "create_config": "/language/{id}/create-config",
    "attach_config": "/language/{id}/attach-config",
    "detach_config": "/language/{id}/detach-config"
}

# UI Settings
DEFAULT_TIMEOUT = 10  # seconds for API requests
