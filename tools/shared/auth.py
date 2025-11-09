import requests
import json
import os
from typing import Optional, Dict, Tuple

# Global session cookie
jsessionid = None

def try_login(url: str, username: str, password: str) -> Tuple[bool, Optional[str], Optional[str]]:
    """
    Attempt to log in to the backend API.
    
    Args:
        url: Base URL of the backend (e.g., "http://localhost:8080")
        email: Username for authentication
        password: Password for authentication
    
    Returns:
        Tuple of (success: bool, jsessionid: Optional[str], error_message: Optional[str])
    """
    global jsessionid
    
    login_url = f"{url.rstrip('/')}/auth/login"
    payload = {
        "email": username,
        "password": password
    }
    
    try:
        response = requests.post(login_url, json=payload, timeout=10)
        response.raise_for_status()
        
        # Extract JSESSIONID from cookies
        session_cookie = response.cookies.get('JSESSIONID')
        if session_cookie:
            jsessionid = session_cookie
            return True, session_cookie, None
        else:
            return False, None, "No session cookie received from server"
        
    except requests.exceptions.HTTPError as e:
        if e.response.status_code == 401:
            return False, None, "Invalid username or password"
        elif e.response.status_code == 403:
            return False, None, "Access forbidden"
        else:
            return False, None, f"HTTP error {e.response.status_code}: {e.response.text}"
    except requests.exceptions.ConnectionError:
        return False, None, f"Could not connect to server at {url}"
    except requests.exceptions.Timeout:
        return False, None, "Connection timed out"
    except requests.exceptions.RequestException as e:
        return False, None, f"Login failed: {str(e)}"

def test_connection(url: str, session_cookie: Optional[str] = None) -> Tuple[bool, Optional[str]]:
    """
    Test the connection to the backend API.
    
    Args:
        url: Base URL of the backend
        session_cookie: Optional JSESSIONID for authenticated requests
    
    Returns:
        Tuple of (success: bool, error_message: Optional[str])
    """
    test_url = f"{url.rstrip('/')}/languages"
    
    cookies = {}
    if session_cookie:
        cookies['JSESSIONID'] = session_cookie
    
    try:
        response = requests.get(test_url, cookies=cookies, timeout=5)
        response.raise_for_status()
        return True, None
    except requests.exceptions.ConnectionError:
        return False, f"Could not connect to server at {url}"
    except requests.exceptions.Timeout:
        return False, "Connection timed out"
    except requests.exceptions.HTTPError as e:
        if e.response.status_code == 401:
            return False, "Authentication required or session expired"
        else:
            return False, f"HTTP error {e.response.status_code}"
    except requests.exceptions.RequestException as e:
        return False, f"Connection test failed: {str(e)}"

def get_session_cookie() -> Optional[str]:
    """Get the current session cookie"""
    return jsessionid

def set_session_cookie(cookie: str):
    """Set the session cookie"""
    global jsessionid
    jsessionid = cookie

def clear_session_cookie():
    """Clear the session cookie"""
    global jsessionid
    jsessionid = None

def save_login_info(url: str, username: str, password: str, file_path: str = "login_info.json") -> bool:
    """
    Save login information to a JSON file.
    
    Args:
        url: Backend URL
        username: Username
        password: Password
        file_path: Path to save the login info
    
    Returns:
        True if successful, False otherwise
    """
    try:
        login_data = {
            "url": url.strip(),
            "username": username,
            "password": password
        }
        
        # Ensure directory exists
        os.makedirs(os.path.dirname(os.path.abspath(file_path)) if os.path.dirname(file_path) else ".", exist_ok=True)
        
        with open(file_path, 'w') as f:
            json.dump(login_data, f, indent=2)
        
        return True
    except Exception as e:
        print(f"Error saving login info: {e}")
        return False

def load_login_info(file_path: str = "login_info.json") -> Optional[Dict[str, str]]:
    """
    Load login information from a JSON file.
    
    Args:
        file_path: Path to the login info file
    
    Returns:
        Dictionary with 'url', 'username', 'password' or None if file doesn't exist
    """
    try:
        if os.path.exists(file_path):
            with open(file_path, 'r') as f:
                return json.load(f)
        return None
    except Exception as e:
        print(f"Error loading login info: {e}")
        return None

def clear_login_info(file_path: str = "login_info.json") -> bool:
    """
    Clear saved login information.
    
    Args:
        file_path: Path to the login info file
    
    Returns:
        True if successful, False otherwise
    """
    try:
        if os.path.exists(file_path):
            os.remove(file_path)
        return True
    except Exception as e:
        print(f"Error clearing login info: {e}")
        return False