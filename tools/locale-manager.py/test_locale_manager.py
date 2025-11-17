"""
Test script for locale_manager.py
Tests the core functionality without running the Streamlit UI
"""

import sys
from pathlib import Path

# Add tools directory to path
tools_dir = Path(__file__).parent
sys.path.insert(0, str(tools_dir))

from app import LocaleManager, TranslationUnit, LOCALE_DIR

def test_locale_manager():
    """Test LocaleManager basic functionality"""
    print("Testing LocaleManager...")
    
    manager = LocaleManager()
    
    # Test 1: Get translation files
    print("\n1. Testing get_translation_files()...")
    files = manager.get_translation_files()
    print(f"   Found {len(files)} translation file(s):")
    for f in files:
        print(f"   - {f}")
    
    # Test 2: Extract language code
    print("\n2. Testing extract_language_code()...")
    test_filename = "messages.zh-CN.xlf"
    lang_code = manager.extract_language_code(test_filename)
    print(f"   Extracted '{lang_code}' from '{test_filename}'")
    assert lang_code == "zh-CN", "Language code extraction failed"
    
    # Test 3: Parse existing XLIFF file
    if files:
        print(f"\n3. Testing parse_xliff() on {files[0]}...")
        filepath = manager.locale_dir / files[0]
        source_lang, target_lang, units = manager.parse_xliff(filepath)
        
        print(f"   Source Language: {source_lang}")
        print(f"   Target Language: {target_lang}")
        print(f"   Translation Units: {len(units)}")
        
        if units:
            print(f"\n   First unit:")
            print(f"   - ID: {units[0].id}")
            print(f"   - Source: {units[0].source[:50]}...")
            print(f"   - Target: {units[0].target[:50]}...")
            print(f"   - Contexts: {len(units[0].contexts)}")
        
        # Test 4: Check translation statistics
        translated = sum(1 for u in units if u.target.strip())
        untranslated = len(units) - translated
        print(f"\n   Translated: {translated}/{len(units)} ({translated/len(units)*100:.1f}%)")
        print(f"   Untranslated: {untranslated}")
    
    # Test 5: Parse source file
    print(f"\n4. Testing parse_xliff() on source file (messages.xlf)...")
    source_path = manager.locale_dir / "messages.xlf"
    if source_path.exists():
        source_lang, target_lang, source_units = manager.parse_xliff(source_path)
        print(f"   Source Language: {source_lang}")
        print(f"   Translation Units: {len(source_units)}")
        
        if source_units:
            print(f"\n   Sample units:")
            for i, unit in enumerate(source_units[:3]):
                print(f"   {i+1}. {unit.id}: {unit.source[:40]}...")
    
    print("\n✅ All tests passed!")
    return True

if __name__ == "__main__":
    try:
        test_locale_manager()
    except Exception as e:
        print(f"\n❌ Test failed with error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
