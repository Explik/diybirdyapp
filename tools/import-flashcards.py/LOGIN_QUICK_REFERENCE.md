# Login Quick Reference Card

## For Users

### Login Steps
```
1. Open sidebar
2. Enter Backend URL: http://localhost:8080
3. Enter Username: your_username
4. Enter Password: your_password
5. Click "🔓 Login"
6. (Optional) Click "💾 Save" to remember credentials
```

### Test Connection
```
Click "🔍 Test Connection" to verify backend connectivity
```

### Logout
```
Click "🚪 Logout" when finished
```

---

## For Developers

### Add Login to a Page

```python
import streamlit as st
from login_utils import render_login_sidebar

st.set_page_config(page_title="My Page", page_icon="📝")

# Add login sidebar
render_login_sidebar()

# Rest of your page...
```

### Require Login for a Page

```python
from login_utils import require_login

# This stops execution if not logged in
require_login()

# Protected code here (only runs when logged in)
```

### Make Authenticated API Calls

```python
from shared.auth import get_session_cookie
import requests

# Get session cookie
session_cookie = get_session_cookie()

# Make authenticated request
response = requests.post(
    "http://localhost:8080/api/endpoint",
    json=data,
    cookies={'JSESSIONID': session_cookie} if session_cookie else {}
)
```

### Session State Variables

```python
# Available in st.session_state after render_login_sidebar():
st.session_state.logged_in          # bool: Login status
st.session_state.backend_url        # str: Backend URL
st.session_state.username           # str: Username
st.session_state.password           # str: Password (avoid using directly)
st.session_state.session_cookie     # str: JSESSIONID
```

### Auth Module Functions

```python
from shared.auth import (
    try_login,              # (url, username, password) -> (success, cookie, error)
    test_connection,        # (url, cookie) -> (success, error)
    get_session_cookie,     # () -> cookie
    set_session_cookie,     # (cookie) -> None
    clear_session_cookie,   # () -> None
    save_login_info,        # (url, username, password, file) -> success
    load_login_info,        # (file) -> {url, username, password}
    clear_login_info        # (file) -> success
)
```

---

## Common Patterns

### Check if Logged In
```python
from login_utils import render_login_sidebar

is_logged_in = render_login_sidebar()

if is_logged_in:
    # Show authenticated content
    st.write("Welcome!")
else:
    # Show login prompt
    st.info("Please log in")
```

### Optional Login (Works Without Auth)
```python
# Render sidebar but don't require login
render_login_sidebar()

# Check if logged in for optional features
if st.session_state.get('logged_in'):
    if st.button("Upload to Server"):
        # Upload feature
        pass
else:
    st.info("Login to upload to server")
```

### Required Login (Must Be Authenticated)
```python
# Page will stop if not logged in
require_login()

# This code only runs if authenticated
if st.button("Delete All Data"):
    # Dangerous operation requiring auth
    pass
```

---

## Files

| File | Purpose |
|------|---------|
| `login_info.json` | Saved credentials (gitignored) |
| `login_utils.py` | Streamlit UI for login |
| `shared/auth.py` | Core authentication logic |

---

## API Endpoints

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/auth/login` | POST | Authenticate and get session cookie |
| `/languages` | GET | Test connection endpoint |

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Can't connect | Check backend is running on port 8080 |
| Invalid credentials | Verify username/password are correct |
| Session expired | Click Login again to refresh session |
| Saved creds not loading | Check `login_info.json` exists and is valid JSON |

---

## Security Checklist

- [ ] `login_info.json` is in `.gitignore`
- [ ] Using HTTPS in production (not localhost)
- [ ] Backend has proper session timeout configured
- [ ] Credentials are not logged or displayed in UI
- [ ] Error messages don't leak security info

---

**Full Documentation:** See [LOGIN_GUIDE.md](LOGIN_GUIDE.md)
