# Troubleshooting Guide

## Common Issues and Solutions

### 1. App Won't Start

#### Error: "streamlit: command not found" or "streamlit is not recognized"

**Solution:**
```powershell
# Install streamlit
pip install streamlit

# Or install all dependencies
pip install -r requirements.txt
```

#### Error: "No module named 'streamlit'"

**Solution:**
Make sure you're using the correct Python environment:
```powershell
# Check Python version
python --version

# Check pip is using the right Python
pip --version

# Reinstall dependencies
pip install -r requirements.txt
```

### 2. API Connection Issues

#### Error: "Could not load languages from API"

**Symptoms:**
- Empty language dropdowns
- "Could not connect to API server" message

**Solutions:**

1. **Check if API is running:**
   ```powershell
   # Try accessing the API in browser
   # Open: http://localhost:8080
   ```

2. **Start the API server:**
   ```powershell
   # Navigate to api directory
   cd ..\..\api
   
   # Start the Spring Boot server
   .\mvnw spring-boot:run
   ```

3. **Verify port 8080 is available:**
   ```powershell
   # Check what's using port 8080
   netstat -ano | findstr :8080
   ```

4. **Check firewall settings:**
   - Ensure Windows Firewall allows connections to localhost:8080
   - Check antivirus software isn't blocking the connection

#### Error: "Connection refused" or "Unable to connect"

**Solution:**
- Restart both the API server and Streamlit app
- Check if another application is using port 8080
- Try changing the API port in `config.py`

### 3. Translation Issues

#### Error: "google.auth.exceptions.DefaultCredentialsError"

**Symptoms:**
- Translation fails
- "Could not automatically determine credentials"

**Solutions:**

1. **Set Google Cloud credentials:**
   ```powershell
   # Set environment variable (PowerShell)
   $env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\credentials.json"
   
   # Verify it's set
   echo $env:GOOGLE_APPLICATION_CREDENTIALS
   ```

2. **Download credentials from Google Cloud Console:**
   - Go to https://console.cloud.google.com
   - Navigate to "IAM & Admin" > "Service Accounts"
   - Create a service account with Translation API access
   - Download the JSON key file
   - Set GOOGLE_APPLICATION_CREDENTIALS to the file path

3. **Enable the Translation API:**
   - Go to Google Cloud Console
   - Navigate to "APIs & Services" > "Library"
   - Search for "Cloud Translation API"
   - Click "Enable"

#### Error: "Quota exceeded" or "Resource exhausted"

**Solution:**
- Check your Google Cloud quota limits
- Enable billing on your Google Cloud project
- Request quota increase if needed
- Consider using smaller test files first

### 4. Import/Module Issues

#### Error: "No module named 'shared'"

**Solution:**
```powershell
# Add parent directory to Python path
$env:PYTHONPATH="C:\Users\dxied\source\repos\diy-birdy-app\tools"

# Or install the API client
cd ..\shared\api_client
pip install -e .
cd ..\..\import-flashcards.py
```

#### Error: "No module named 'openapi_client'"

**Solution:**
```powershell
# Install the OpenAPI client package
cd ..\shared\api_client
pip install -e .
cd ..\..\import-flashcards.py

# Verify installation
pip list | Select-String "openapi-client"
```

### 5. File Upload Issues

#### Error: File upload fails or times out

**Solutions:**

1. **Check file size:**
   - Default limit is 200MB in Streamlit
   - Large files may take time to upload
   - Try with smaller test files first

2. **Check file encoding:**
   - Ensure .txt files are UTF-8 encoded
   - Use a text editor to convert if needed

3. **File format issues:**
   ```powershell
   # Check file has correct line endings
   # Windows uses CRLF, but LF should also work
   
   # Convert if needed using PowerShell:
   (Get-Content file.txt) | Set-Content file_unix.txt
   ```

### 6. Flashcard Creation Issues

#### Error: "Failed to create flashcard"

**Solutions:**

1. **Check API authentication:**
   - Verify you're authenticated with the API
   - Check if API requires authentication headers
   - Review API logs for detailed error messages

2. **Check language configuration:**
   ```powershell
   # Verify languages exist in the system
   # Check API response at: http://localhost:8080/language
   ```

3. **Check required fields:**
   - Ensure deck name is provided
   - Verify source and target languages are valid
   - Check text content is not empty

#### Error: "Language with abbreviation XX not found"

**Solution:**
- The language might not be configured in your API
- Add the language via the API admin interface
- Or select a different language that exists in the system

### 7. Performance Issues

#### App is slow or unresponsive

**Solutions:**

1. **Process smaller files:**
   - Start with 10-20 lines for testing
   - Process large files in batches

2. **Clear Streamlit cache:**
   ```powershell
   # Clear cache from UI
   # Press 'C' in the app menu, select "Clear cache"
   
   # Or delete cache manually
   Remove-Item -Recurse -Force .streamlit\cache
   ```

3. **Restart the app:**
   ```powershell
   # Press Ctrl+C to stop
   # Then restart with: streamlit run app.py
   ```

### 8. Display Issues

#### Emojis not showing correctly

**Solution:**
- Use a terminal that supports Unicode (Windows Terminal recommended)
- Update PowerShell to latest version
- Use a browser that supports emoji (Chrome, Firefox, Edge)

#### Page layout broken

**Solution:**
- Refresh the browser (F5)
- Clear browser cache
- Try a different browser
- Check browser console for JavaScript errors

### 9. Session State Issues

#### Session state not persisting or losing data

**Solutions:**

1. **Don't mix reruns and page navigation:**
   - Use `st.rerun()` carefully
   - Avoid navigating away during processing

2. **Check session state keys:**
   ```python
   # Debug session state
   st.write(st.session_state)
   ```

3. **Reset session state:**
   - Click "Create Another Deck" button
   - Or manually clear in developer tools

### 10. Windows-Specific Issues

#### PowerShell execution policy errors

**Solution:**
```powershell
# Check current policy
Get-ExecutionPolicy

# Set to allow scripts (run as Administrator)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Or run with bypass
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

#### Path issues with backslashes

**Solution:**
- Python handles both `/` and `\\` on Windows
- Use raw strings for paths: `r"C:\path\to\file"`
- Or use `Path` from `pathlib`: `Path("C:/path/to/file")`

## Getting Help

If you continue to have issues:

1. **Check the logs:**
   - Streamlit shows errors in the terminal
   - API logs show detailed error messages
   - Browser console (F12) shows JavaScript errors

2. **Enable debug mode:**
   ```powershell
   # Run with verbose logging
   streamlit run app.py --logger.level=debug
   ```

3. **Test components individually:**
   - Test API connection with curl or Postman
   - Test Google Translate independently
   - Verify file can be read by Python

4. **Create a minimal test case:**
   - Use the provided `sample_sentences.txt`
   - Use a single line text file
   - Test with default settings

## Quick Diagnostics Checklist

- [ ] Python 3.8+ installed
- [ ] Streamlit installed (`pip list | Select-String streamlit`)
- [ ] API server running (http://localhost:8080 accessible)
- [ ] Google Cloud credentials set ($env:GOOGLE_APPLICATION_CREDENTIALS)
- [ ] OpenAPI client installed (`pip list | Select-String openapi-client`)
- [ ] No firewall blocking localhost connections
- [ ] In correct directory (import-flashcards.py)
- [ ] File encoding is UTF-8
- [ ] Languages configured in API

Still stuck? Check the API logs for more detailed error information!
