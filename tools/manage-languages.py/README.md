# Language Management Tool 
This Streamlit tool is used to manage the languages supported by the backend. It allows users to view, add, edit, and manage language configurations such as Text-to-Speech, Speech-to-Text, and Translation services.

## Features
The tool provides the following features:
- **Server login** (sidebar) - Secure authentication with session management
- **View languages** (separate page) - Browse all supported languages and their configurations
- **Create/update config** (separate page) - Add or modify language configurations

## Installation

### Prerequisites
- Python 3.8 or higher
- Access to the DIY Birdy backend API (default: `http://localhost:8080`)
- Valid admin credentials

### Setup

1. **Navigate to the tool directory:**
   ```powershell
   cd tools\manage-languages.py
   ```

2. **Install dependencies:**
   ```powershell
   pip install -r requirements.txt
   ```

3. **Run the application:**
   ```powershell
   .\run.ps1
   ```
   
   Or directly with Streamlit:
   ```powershell
   streamlit run app.py
   ```

## Usage

### Login
1. Open the application in your browser (typically `http://localhost:8501`)
2. Use the sidebar login form:
   - **Backend URL**: The API server URL (e.g., `http://localhost:8080`)
   - **Username**: Your admin username
   - **Password**: Your admin password
3. Click **Login** to authenticate
4. Optionally click **Save** to store credentials for future sessions

## Overview page (app.py)
The overview page shows a list of the available languages in the backend including name, abbriviation and ID.

### Create/update Languages
The "Create/Update Languages" page allows you to add new languages or modify existing ones. You can specify the language name, isoCode, and other relevant details.

Usage: 
1. Select the language to edit or choose to create a new language
1A. If an existing language is selected, its current details will be pre-filled and configurations displayed
2. Fill in the required fields
3. Click "Save Language" to persist changes

### Create/Update Config
The "Create/Update Config" page allows you to manage language configurations:

1. **Select language**: Choose the language to configure
2. **Select configuration type**: Choose from:
   - Google Text-to-Speech
   - Microsoft Text-to-Speech
   - Google Speech-to-Text
   - Google Translate
3. **Select action**: Create new or update existing configuration
4. **Fill in details**: Provide required fields (varies by type)
5. **Save**: Click "Save Configuration" to persist changes

#### Configuration Types

**Google Text-to-Speech**
- Language Code (e.g., `en-US`, `es-ES`)
- Voice Name (e.g., `en-US-Wavenet-A`)

**Microsoft Text-to-Speech**
- No additional fields required

**Google Speech-to-Text**
- No additional fields required

**Google Translate**
- No additional fields required

## Technical Details

### API Endpoints Used

- `GET /language` - Fetch all languages
- `GET /language/{id}/config` - Fetch configurations for a language
- `POST /language/{id}/create-config` - Create a new configuration
- `POST /language/{id}/attach-config` - Attach existing configuration
- `POST /language/{id}/detach-config` - Detach configuration

### Project Structure

```
manage-languages.py/
├── app.py                          # Main application entry point
├── login_utils.py                  # Login and authentication utilities
├── config.py                       # Configuration settings
├── requirements.txt                # Python dependencies
├── run.ps1                         # Launch script
├── README.md                       # This file
└── pages/
    ├── 1_🗣️_View_Languages.py     # View languages page
    └── 2_⚙️_Create_Update_Config.py # Create/update config page
```

### Shared Modules

This tool uses shared modules from `../shared/`:
- `auth.py` - Authentication and session management
- `language_client.py` - Language and configuration API client

## Troubleshooting

**Connection Failed**
- Ensure the backend API is running
- Verify the backend URL is correct
- Check network connectivity

**Login Failed**
- Verify username and password are correct
- Ensure your account has admin privileges
- Check if the session has expired

**Configuration Not Saving**
- Verify all required fields are filled
- Check the backend logs for errors
- Ensure proper permissions for the language