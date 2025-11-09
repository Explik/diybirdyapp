# Login Functionality Guide

## Overview

The Flashcard Import Tool now includes a comprehensive login system that allows you to authenticate with the backend API. This guide explains how to use the login functionality.

## Features

### 1. Persistent Login Across Pages
- Login status is maintained across all pages in the application
- Session information persists even when navigating between pages
- Login UI is available in the sidebar on every page

### 2. Saved Credentials
- Login credentials can be saved to a local file (`login_info.json`)
- Saved credentials are automatically loaded when you restart the application
- You can clear saved credentials at any time by deleting the file

### 3. Connection Testing
- Test your connection to the backend server
- Verify that your session is still valid
- Diagnose connection issues before attempting operations

### 4. Authenticated API Requests
- All API requests automatically include your session cookie
- No need to manually manage authentication for each request
- Session expires according to backend settings

## How to Use

### Basic Login Flow

1. **Open the Application**
   ```powershell
   .\run.ps1
   ```
   Or:
   ```powershell
   streamlit run app.py
   ```

2. **Enter Your Credentials**
   - In the sidebar, you'll see the login form
   - Enter the backend URL (e.g., `http://localhost:8080`)
   - Enter your username
   - Enter your password

3. **Click Login**
   - Click the "🔓 Login" button
   - If successful, you'll see a success message
   - The sidebar will now show your logged-in status

4. **Optional: Save Credentials**
   - Click the "💾 Save" button to save your credentials
   - Next time you open the app, your credentials will be pre-filled
   - You'll still need to click "Login" to authenticate

### Testing Your Connection

Once logged in, you can test your connection:

1. Click the "🔍 Test Connection" button in the sidebar
2. The system will attempt to connect to the backend
3. Results will show:
   - ✅ Success: Connection is working
   - ❌ Failure: Shows specific error message

### Logging Out

To log out:
1. Click the "🚪 Logout" button in the sidebar
2. Your session will be cleared
3. You'll need to log in again to access authenticated features

## Files and Directories

### `login_info.json`
Location: `tools/import-flashcards.py/login_info.json`

This file stores your saved credentials:
```json
{
  "url": "http://localhost:8080",
  "username": "your_username",
  "password": "your_password"
}
```

**⚠️ Security Note:** This file contains your password in plain text. It's stored locally for convenience during development. For production use, consider implementing more secure credential storage.

### Source Files

- **`login_utils.py`**: Login UI and session management for Streamlit
- **`shared/auth.py`**: Core authentication functions (login, test connection, save/load credentials)

## API Integration

### Backend Login Endpoint

The system authenticates using the backend API:

```
POST /auth/login
Content-Type: application/json

{
  "username": "your_username",
  "password": "your_password"
}

Response:
- Cookie: JSESSIONID=<session_id>
- Status: 200 OK (success) or 401 Unauthorized (failure)
```

### Authenticated Requests

After login, all API requests include the session cookie:

```python
# Example from import_client.py
response = requests.post(
    "http://localhost:8080/flashcard",
    json=flashcard_dto,
    cookies={'JSESSIONID': session_cookie}
)
```

## Usage in Your Code

### Adding Login to a New Page

```python
import streamlit as st
from login_utils import render_login_sidebar

st.set_page_config(page_title="My Page", page_icon="📝")

# Render login sidebar on this page
render_login_sidebar()

st.title("My Page")
# ... rest of your page code
```

### Requiring Login for a Page

```python
import streamlit as st
from login_utils import require_login

st.set_page_config(page_title="Protected Page", page_icon="🔒")

# This will stop execution if not logged in
require_login()

st.title("Protected Page")
# ... rest of your page code (only runs if logged in)
```

### Getting the Session Cookie

```python
from login_utils import get_authenticated_session

session_cookie = get_authenticated_session()
if session_cookie:
    # Make authenticated request
    response = requests.get(
        "http://localhost:8080/api/endpoint",
        cookies={'JSESSIONID': session_cookie}
    )
```

## Troubleshooting

### "Connection failed: Could not connect to server"
- Verify the backend server is running
- Check the backend URL is correct (default: `http://localhost:8080`)
- Ensure there are no firewall issues blocking the connection

### "Login failed: Invalid username or password"
- Verify your credentials are correct
- Check if your account exists in the backend database
- Ensure the backend authentication endpoint is functioning

### "Authentication required or session expired"
- Your session has expired (backend timeout)
- Click "Login" again to get a new session
- Sessions typically expire after a period of inactivity

### Saved credentials not loading
- Check that `login_info.json` exists in the `import-flashcards.py` directory
- Verify the JSON file is properly formatted
- Check file permissions (ensure it's readable)

### Session cookie not being sent with requests
- Verify you're logged in (check sidebar status)
- Restart the application if issues persist
- Check console logs for any errors

## Security Best Practices

1. **Don't commit `login_info.json`**: Add it to `.gitignore`
2. **Use HTTPS in production**: Never send credentials over unencrypted HTTP in production
3. **Rotate passwords regularly**: Change your password periodically
4. **Clear saved credentials**: Delete `login_info.json` when using a shared computer
5. **Session timeout**: Be aware that sessions expire and you'll need to re-login

## Example Session

```
# 1. Start application
.\run.ps1

# 2. In sidebar:
Backend URL: http://localhost:8080
Username: myuser
Password: ********
[Click Login]

# 3. Success!
✅ Logged in as myuser
Backend: http://localhost:8080

# 4. Test connection
[Click Test Connection]
✅ Connection successful!

# 5. Use the app normally
# All API requests now include authentication

# 6. When done
[Click Logout]
ℹ️ Please log in using the sidebar to access all features
```

## Related Documentation

- [README.md](README.md) - Main application documentation
- [QUICKSTART.md](QUICKSTART.md) - Getting started guide
- [README_APP.md](README_APP.md) - Full application documentation
