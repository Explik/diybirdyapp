"""
Login utilities for Streamlit app
Provides sidebar login UI and session management
"""

import streamlit as st
import sys
import os
from pathlib import Path

# Add shared directory to path
sys.path.append(str(Path(__file__).parent.parent))

from shared.auth import (
    try_login,
    test_connection,
    get_session_cookie,
    set_session_cookie,
    clear_session_cookie,
    save_login_info,
    load_login_info,
    clear_login_info
)

# Path to login info file (relative to the import-flashcards.py directory)
LOGIN_INFO_FILE = os.path.join(os.path.dirname(__file__), "login_info.json")


def initialize_session_state():
    """Initialize session state variables for login"""
    if 'logged_in' not in st.session_state:
        st.session_state.logged_in = False
    if 'backend_url' not in st.session_state:
        st.session_state.backend_url = "http://localhost:8080"
    if 'username' not in st.session_state:
        st.session_state.username = ""
    if 'password' not in st.session_state:
        st.session_state.password = ""
    if 'session_cookie' not in st.session_state:
        st.session_state.session_cookie = None
    if 'connection_tested' not in st.session_state:
        st.session_state.connection_tested = False
    if 'connection_status' not in st.session_state:
        st.session_state.connection_status = None


def load_saved_credentials():
    """Load saved login credentials from file"""
    saved_info = load_login_info(LOGIN_INFO_FILE)
    if saved_info:
        st.session_state.backend_url = saved_info.get('url', 'http://localhost:8080').strip()
        st.session_state.username = saved_info.get('username', '')
        st.session_state.password = saved_info.get('password', '')
        return True
    return False


def render_login_sidebar():
    """
    Render login UI in the sidebar.
    This should be called from every page to maintain consistent login state.
    
    Returns:
        bool: True if logged in, False otherwise
    """
    initialize_session_state()
    
    # Try to load saved credentials on first run
    if not st.session_state.logged_in and 'credentials_loaded' not in st.session_state:
        if load_saved_credentials():
            st.session_state.credentials_loaded = True
    
    st.sidebar.markdown("## 🔐 Login")
    
    if st.session_state.logged_in:
        # Show logged-in state
        st.sidebar.success(f"✅ Logged in as **{st.session_state.username}**")
        st.sidebar.text(f"Backend: {st.session_state.backend_url}")
        
        # Test connection button
        if st.sidebar.button("🔍 Test Connection", use_container_width=True):
            with st.sidebar.status("Testing connection..."):
                success, error = test_connection(
                    st.session_state.backend_url,
                    st.session_state.session_cookie
                )
                st.session_state.connection_tested = True
                st.session_state.connection_status = (success, error)
        
        # Show connection test results
        if st.session_state.connection_tested and st.session_state.connection_status:
            success, error = st.session_state.connection_status
            if success:
                st.sidebar.success("✅ Connection successful!")
            else:
                st.sidebar.error(f"❌ Connection failed: {error}")
        
        # Logout button
        if st.sidebar.button("🚪 Logout", use_container_width=True):
            st.session_state.logged_in = False
            st.session_state.session_cookie = None
            st.session_state.connection_tested = False
            st.session_state.connection_status = None
            clear_session_cookie()
            st.rerun()
        
        return True
    
    else:
        # Show login form
        st.sidebar.text_input(
            "Backend URL",
            value=st.session_state.backend_url,
            key="backend_url_input",
            help="Base URL of the backend API (e.g., http://localhost:8080)"
        )
        
        st.sidebar.text_input(
            "Username",
            value=st.session_state.username,
            key="username_input",
            help="Your username"
        )
        
        st.sidebar.text_input(
            "Password",
            value=st.session_state.password,
            type="password",
            key="password_input",
            help="Your password"
        )
        
        # Update session state from inputs
        st.session_state.backend_url = st.session_state.backend_url_input.strip()
        st.session_state.username = st.session_state.username_input
        st.session_state.password = st.session_state.password_input
        
        col1, col2 = st.sidebar.columns(2)
        
        with col1:
            login_clicked = st.button("🔓 Login", use_container_width=True)
        
        with col2:
            save_clicked = st.button("💾 Save", use_container_width=True, 
                                   help="Save credentials for next time")
        
        # Handle login
        if login_clicked:
            if not st.session_state.backend_url:
                st.sidebar.error("❌ Please enter backend URL")
            elif not st.session_state.username:
                st.sidebar.error("❌ Please enter username")
            elif not st.session_state.password:
                st.sidebar.error("❌ Please enter password")
            else:
                with st.sidebar.status("Logging in..."):
                    success, session_cookie, error = try_login(
                        st.session_state.backend_url,
                        st.session_state.username,
                        st.session_state.password
                    )
                    
                    if success:
                        st.session_state.logged_in = True
                        st.session_state.session_cookie = session_cookie
                        set_session_cookie(session_cookie)
                        st.sidebar.success("✅ Login successful!")
                        st.rerun()
                    else:
                        st.sidebar.error(f"❌ Login failed: {error}")
        
        # Handle save
        if save_clicked:
            if not st.session_state.backend_url:
                st.sidebar.error("❌ Please enter backend URL")
            elif not st.session_state.username:
                st.sidebar.error("❌ Please enter username")
            elif not st.session_state.password:
                st.sidebar.error("❌ Please enter password")
            else:
                if save_login_info(
                    st.session_state.backend_url,
                    st.session_state.username,
                    st.session_state.password,
                    LOGIN_INFO_FILE
                ):
                    st.sidebar.success("✅ Credentials saved!")
                else:
                    st.sidebar.error("❌ Failed to save credentials")
        
        st.sidebar.info("ℹ️ Please log in to access all features")
        
        return False


def require_login():
    """
    Check if user is logged in. If not, show warning and stop execution.
    Use this in pages that require authentication.
    """
    if not render_login_sidebar():
        st.warning("⚠️ Please log in using the sidebar to access this feature.")
        st.stop()


def get_authenticated_session():
    """
    Get the authenticated session cookie.
    
    Returns:
        str: Session cookie or None if not logged in
    """
    return st.session_state.get('session_cookie')
