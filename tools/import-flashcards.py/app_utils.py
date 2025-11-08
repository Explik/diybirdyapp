"""
Utility functions for the Flashcard Import Tool Streamlit app
"""

import streamlit as st
from typing import Optional


def show_error(message: str, exception: Optional[Exception] = None):
    """Display an error message with optional exception details"""
    st.error(f"❌ {message}")
    if exception and st.checkbox("Show error details"):
        st.exception(exception)


def show_success(message: str):
    """Display a success message"""
    st.success(f"✅ {message}")


def show_warning(message: str):
    """Display a warning message"""
    st.warning(f"⚠️ {message}")


def show_info(message: str):
    """Display an info message"""
    st.info(f"ℹ️ {message}")


def validate_required_fields(**fields) -> tuple[bool, str]:
    """
    Validate that required fields are filled
    
    Args:
        **fields: keyword arguments where key is field name and value is the field value
    
    Returns:
        Tuple of (is_valid, error_message)
    """
    for field_name, field_value in fields.items():
        if not field_value:
            return False, f"Please provide a value for {field_name.replace('_', ' ')}"
    return True, ""


def format_file_size(size_bytes: int) -> str:
    """Format file size in human-readable format"""
    for unit in ['B', 'KB', 'MB', 'GB']:
        if size_bytes < 1024.0:
            return f"{size_bytes:.1f} {unit}"
        size_bytes /= 1024.0
    return f"{size_bytes:.1f} TB"


def create_download_button(data: bytes, filename: str, label: str, mime: str = "application/octet-stream"):
    """Create a download button for binary data"""
    st.download_button(
        label=label,
        data=data,
        file_name=filename,
        mime=mime
    )


def check_api_connection(api_url: str = "http://localhost:8080") -> bool:
    """
    Check if the API server is accessible
    
    Args:
        api_url: Base URL of the API server
    
    Returns:
        True if API is accessible, False otherwise
    """
    import requests
    try:
        response = requests.get(api_url, timeout=2)
        return True
    except:
        return False


def display_flashcard_preview(front_text: str, back_text: str):
    """Display a preview of a flashcard"""
    col1, col2 = st.columns(2)
    
    with col1:
        st.markdown("**Front:**")
        st.info(front_text)
    
    with col2:
        st.markdown("**Back:**")
        st.info(back_text)
