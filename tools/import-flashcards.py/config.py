"""
Configuration settings for the Flashcard Import Tool
"""

# API Configuration
API_BASE_URL = "http://localhost:8080"

# Streamlit Configuration
APP_TITLE = "Flashcard Import Tool"
APP_ICON = "🎴"

# File Upload Settings
MAX_FILE_SIZE_MB = 10
ALLOWED_TXT_EXTENSIONS = ['txt']
ALLOWED_CSV_EXTENSIONS = ['csv']
ALLOWED_ANKI_EXTENSIONS = ['apkg']

# Translation Settings
DEFAULT_SOURCE_LANGUAGE = "Auto-detect"
TRANSLATION_TIMEOUT = 30  # seconds

# UI Settings
PREVIEW_LINES = 10  # Number of lines to show in file preview
