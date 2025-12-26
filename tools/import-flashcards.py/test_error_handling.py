"""
Test script to simulate the exact error handling that Streamlit does
"""
import sys
from pathlib import Path

# Add shared directory to path (same as app.py does)
sys.path.append(str(Path(__file__).parent.parent))

print("Simulating Streamlit error handling...")

try:
    from import_client import get_languages
    backend_languages = get_languages()
    print(f"✓ Success: Loaded {len(backend_languages)} languages")
except Exception as e:
    # This is exactly what the Streamlit pages do
    error_msg = f"⚠️ Could not load languages from backend: {e}"
    print(f"✓ Error message formatted successfully:")
    print(f"  {error_msg}")
    print(f"\n✓ Exception type: {type(e).__name__}")
    print(f"✓ No 'openapi_client' import error during string formatting!")
