"""
Test script for login functionality
Run this to verify login authentication works correctly
"""

import sys
from pathlib import Path

# Add shared directory to path
sys.path.append(str(Path(__file__).parent.parent))

from shared.auth import (
    try_login,
    test_connection,
    save_login_info,
    load_login_info,
    clear_login_info,
    get_session_cookie
)

def test_login_functionality():
    """Test the login functionality"""
    print("=" * 60)
    print("Login Functionality Test")
    print("=" * 60)
    
    # Test configuration
    backend_url = "http://localhost:8080"
    username = input("Enter username: ")
    password = input("Enter password: ")
    
    print("\n1. Testing login...")
    success, session_cookie, error = try_login(backend_url, username, password)
    
    if success:
        print(f"   ✅ Login successful!")
        print(f"   Session cookie: {session_cookie[:20]}...")
    else:
        print(f"   ❌ Login failed: {error}")
        return
    
    print("\n2. Testing connection...")
    success, error = test_connection(backend_url, session_cookie)
    
    if success:
        print("   ✅ Connection test successful!")
    else:
        print(f"   ❌ Connection test failed: {error}")
    
    print("\n3. Testing save credentials...")
    test_file = "test_login_info.json"
    if save_login_info(backend_url, username, password, test_file):
        print(f"   ✅ Credentials saved to {test_file}")
    else:
        print("   ❌ Failed to save credentials")
    
    print("\n4. Testing load credentials...")
    loaded_info = load_login_info(test_file)
    if loaded_info:
        print("   ✅ Credentials loaded:")
        print(f"      URL: {loaded_info['url']}")
        print(f"      Username: {loaded_info['username']}")
        print(f"      Password: {'*' * len(loaded_info['password'])}")
    else:
        print("   ❌ Failed to load credentials")
    
    print("\n5. Testing clear credentials...")
    if clear_login_info(test_file):
        print(f"   ✅ Credentials cleared from {test_file}")
    else:
        print("   ❌ Failed to clear credentials")
    
    print("\n6. Testing session cookie getter...")
    cookie = get_session_cookie()
    if cookie:
        print(f"   ✅ Session cookie retrieved: {cookie[:20]}...")
    else:
        print("   ⚠️  No session cookie (expected after manual test)")
    
    print("\n" + "=" * 60)
    print("All tests completed!")
    print("=" * 60)

if __name__ == "__main__":
    print("\nThis script tests the login functionality.")
    print("Make sure the backend server is running at http://localhost:8080\n")
    
    response = input("Do you want to continue? (y/n): ")
    if response.lower() == 'y':
        test_login_functionality()
    else:
        print("Test cancelled.")
