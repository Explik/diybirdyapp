"""
Debug script to test if openapi_client imports work correctly.
Run this to verify all imports are working before starting the main app.
"""
import sys
from pathlib import Path

# Add shared directory to path (same as app.py does)
sys.path.append(str(Path(__file__).parent.parent))

print("Testing imports...")
print(f"Python path: {sys.path[:3]}")

try:
    print("\n1. Testing shared.api_client.openapi_client import...")
    from shared.api_client import openapi_client
    print("   ✓ Success")
except Exception as e:
    print(f"   ✗ Failed: {e}")
    import traceback
    traceback.print_exc()

try:
    print("\n2. Testing Configuration import...")
    from shared.api_client.openapi_client import Configuration
    print("   ✓ Success")
except Exception as e:
    print(f"   ✗ Failed: {e}")
    import traceback
    traceback.print_exc()

try:
    print("\n3. Testing ApiClient import...")
    from shared.api_client.openapi_client.api_client import ApiClient
    print("   ✓ Success")
except Exception as e:
    print(f"   ✗ Failed: {e}")
    import traceback
    traceback.print_exc()

try:
    print("\n4. Testing LanguageControllerApi import...")
    from shared.api_client.openapi_client.api.language_controller_api import LanguageControllerApi
    print("   ✓ Success")
except Exception as e:
    print(f"   ✗ Failed: {e}")
    import traceback
    traceback.print_exc()

try:
    print("\n5. Testing import_client module...")
    from import_client import get_languages
    print("   ✓ Success")
except Exception as e:
    print(f"   ✗ Failed: {e}")
    import traceback
    traceback.print_exc()

try:
    print("\n6. Testing actual get_languages() call (this requires backend running)...")
    from import_client import get_languages
    languages = get_languages()
    print(f"   ✓ Success - found {len(languages)} languages")
except Exception as e:
    print(f"   ✗ Failed: {e}")
    print(f"   Error type: {type(e).__name__}")
    import traceback
    traceback.print_exc()

print("\n" + "="*50)
print("Import debugging complete!")
print("="*50)
