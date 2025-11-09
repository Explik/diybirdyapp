"""
Page: Upload Deck to Server
Dedicated page for uploading locally created flashcard decks to the server.
"""

import streamlit as st
import sys
from pathlib import Path

# Add parent directory to path to import shared modules
sys.path.append(str(Path(__file__).parent.parent.parent))
sys.path.append(str(Path(__file__).parent.parent))

from import_client import upload_local_deck, get_local_deck_data, list_local_decks
from login_utils import render_login_sidebar, require_login

st.set_page_config(page_title="Upload Deck", page_icon="🚀", layout="wide")

# Render login sidebar and require login for this page
require_login()

st.title("🚀 Upload Deck to Server")

st.markdown("""
This page allows you to upload a locally created flashcard deck to the server.
""")

# Get deck name from URL parameters
query_params = st.query_params
deck_name_param = query_params.get("deck", None)

# Initialize session state
if 'upload_complete' not in st.session_state:
    st.session_state.upload_complete = False
if 'uploaded_deck_id' not in st.session_state:
    st.session_state.uploaded_deck_id = None

# If deck name is provided in URL, try to load it
if deck_name_param:
    st.info(f"📦 Deck to upload: **{deck_name_param}**")
    
    try:
        # Try to get the deck metadata
        deck_metadata = get_local_deck_data(deck_name_param)
        
        if deck_metadata:
            st.success(f"✅ Found local deck: {deck_name_param}")
            
            # Display deck information
            st.markdown("---")
            st.subheader("📊 Deck Information")
            
            col1, col2 = st.columns(2)
            
            with col1:
                st.metric("Deck Name", deck_metadata['name'])
            
            with col2:
                # Count flashcards
                data = deck_metadata.get('data', {})
                flashcard_count = len(data.get('flashcards', []))
                st.metric("Flashcards", flashcard_count)
            
            st.info(f"📁 Deck location: `{deck_metadata['deck_dir']}`")
            
            st.markdown("---")
            
            # Upload button
            if not st.session_state.upload_complete:
                if st.button("📤 Upload to Server", type="primary", use_container_width=True):
                    try:
                        with st.spinner("Uploading deck to server..."):
                            server_deck = upload_local_deck(deck_metadata)
                            st.session_state.upload_complete = True
                            st.session_state.uploaded_deck_id = server_deck.id
                            st.success(f"✅ Deck uploaded successfully! Server ID: {server_deck.id}")
                            st.balloons()
                    except Exception as e:
                        st.error(f"❌ Error uploading deck: {str(e)}")
                        st.exception(e)
            else:
                st.success(f"✅ Deck already uploaded to server (ID: {st.session_state.uploaded_deck_id})")
                
                # Reset button
                if st.button("Upload Another Deck", use_container_width=True):
                    st.session_state.upload_complete = False
                    st.session_state.uploaded_deck_id = None
                    st.query_params.clear()
                    st.rerun()
        else:
            st.error(f"❌ Could not find deck: {deck_name_param}")
            st.info("Please make sure the deck was created locally first.")
            
    except Exception as e:
        st.error(f"❌ Error loading deck: {str(e)}")
        st.exception(e)
else:
    # Show a list of available local decks
    st.markdown("---")
    st.subheader("📚 Available Local Decks")
    
    try:
        local_decks = list_local_decks()
        
        if local_decks:
            # Create a dictionary mapping deck names to metadata
            deck_dict = {deck['name']: deck for deck in local_decks}
            deck_names = list(deck_dict.keys())
            
            selected_deck_name = st.selectbox(
                "Select a deck to upload",
                options=deck_names,
                help="Choose a locally created deck to upload to the server"
            )
            
            if selected_deck_name:
                selected_deck = deck_dict[selected_deck_name]
                
                # Display selected deck information
                st.markdown("---")
                st.subheader("� Selected Deck Information")
                
                col1, col2 = st.columns(2)
                
                with col1:
                    st.metric("Deck Name", selected_deck['name'])
                
                with col2:
                    # Count flashcards
                    data = selected_deck.get('data', {})
                    flashcard_count = len(data.get('flashcards', []))
                    st.metric("Flashcards", flashcard_count)
                
                st.info(f"📁 Deck location: `{selected_deck['deck_dir']}`")
                
                st.markdown("---")
                
                # Upload button
                if st.button("📤 Upload Selected Deck", type="primary", use_container_width=True):
                    try:
                        with st.spinner("Uploading deck to server..."):
                            server_deck = upload_local_deck(selected_deck)
                            st.success(f"✅ Deck uploaded successfully! Server ID: {server_deck.id}")
                            st.balloons()
                    except Exception as e:
                        st.error(f"❌ Error uploading deck: {str(e)}")
                        st.exception(e)
        else:
            st.info("📭 No local decks found. Create a deck first using one of the deck creation pages.")
            
    except Exception as e:
        st.error(f"❌ Error loading local decks: {str(e)}")
        st.exception(e)
