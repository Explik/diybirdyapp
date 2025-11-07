import requests

jsessionid = None

# Returns True if login is successful, False otherwise
def try_login(url, email, password):
    payload = {
        "email": "john@doe.com",
        "password": "password31"
    }
    
    try:
        response = requests.post(url, json=payload)
        response.raise_for_status()
        
        # Extract JSESSIONID from cookies
        jsessionid = response.cookies.get('JSESSIONID')
        return True
        
    except requests.exceptions.RequestException as e:
        print(f"Login failed: {e}")
        return False

def get_session_cookie():
    return jsessionid

def clear_session_cookie():
    global jsessionid
    jsessionid = None