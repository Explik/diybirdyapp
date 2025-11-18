"""
Language Management Tool - Streamlit Multi-page Application
Main entry point for the application - Overview page.
"""

import streamlit as st
import sys
from pathlib import Path

# Add shared directory to path
sys.path.append(str(Path(__file__).parent.parent))

from login_utils import render_login_sidebar
from shared.language_client import LanguageClient, CONFIG_TYPES
from config_viewer import render_config_viewer

# Configure page settings
st.set_page_config(
    page_title="Language Management Tool",
    page_icon="🌐",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Render login sidebar (available on all pages)
logged_in = render_login_sidebar()

# Main page content
st.title("🌐 Language Management Tool")
st.markdown("---")

st.markdown("""
This application helps you manage languages and their configurations for the DIY Birdy flashcard system.
Use the sidebar to navigate between different management tools or edit languages directly from the list below.
""")

# Overview section - show list of available languages
if logged_in:
    st.markdown("---")
    st.subheader("📋 Available Languages")
    
    with st.spinner("Loading languages..."):
        client = LanguageClient(
            st.session_state.backend_url,
            st.session_state.session_cookie
        )
        
        try:
            languages = client.get_languages()
            
            if languages:
                # Create a table with language information and clickable IDs
                import pandas as pd
                
                # Prepare data with markdown links for IDs
                df = pd.DataFrame([{
                    "ID": lang['id'],
                    "Name": lang['name'],
                    "ISO code": lang['isoCode'],
                    "EditLink": f"/Update_Language?languageId={lang['id']}&autoLogin=true"
                } for lang in languages])
                
                st.dataframe(
                    df,
                    use_container_width=True,
                    hide_index=True,
                    column_config={
                        "EditLink": st.column_config.LinkColumn("", display_text="Edit")
                    }
                )
                
            else:
                st.info("No languages found in the backend. Create a new language using the sidebar.")
                
        except Exception as e:
            st.error(f"Failed to fetch languages: {str(e)}")
    
    st.markdown("---")

    # Configuration section
    st.subheader("⚙️ Language Configurations")
    
    # Language selection dropdown
    col1, col2 = st.columns([2, 2])
    
    with col1:
        language_options = {lang['id']: f"{lang['name']} ({lang['isoCode']})" for lang in languages}
        selected_language_id = st.selectbox(
            "Select Language",
            options=[None] + list(language_options.keys()),
            format_func=lambda x: "-- Select a language --" if x is None else language_options[x],
            key="config_language_select"
        )
    
    # Display configurations if a language is selected
    if selected_language_id:
        with col2:
            pass  # Column for layout balance
        render_config_viewer(client, selected_language_id, "all", show_type_filter=True)
    else:
        st.info("👆 Please select a language to view its configurations.")

else:
    st.markdown("---")
    st.info("👈 Please log in using the sidebar to view available languages and access all features!")

