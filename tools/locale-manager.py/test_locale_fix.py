"""
Test script to verify the locale manager normalization fix
"""

import sys
from pathlib import Path

# Add parent directory to path to import app
sys.path.insert(0, str(Path(__file__).parent))

from app import LocaleManager

def test_normalization():
    """Test that XML content normalization works correctly"""
    manager = LocaleManager()
    
    # Test cases with equivalent XML but different formatting
    test_cases = [
        # Self-closing tag variations
        (' 上传 <x id="TAG_INPUT" ctype="x-input" equiv-text="test"/>', 
         ' 上传 <x id="TAG_INPUT" ctype="x-input" equiv-text="test" />'),
        
        # Same content
        (' 上传 <x id="TAG_INPUT" ctype="x-input" equiv-text="test" />',
         ' 上传 <x id="TAG_INPUT" ctype="x-input" equiv-text="test" />'),
        
        # Plain text (should remain unchanged)
        ('Hello World', 'Hello World'),
        
        # Different content (should not match)
        (' 上传 <x id="TAG_INPUT" ctype="x-input" equiv-text="test" />',
         ' 下载 <x id="TAG_INPUT" ctype="x-input" equiv-text="test" />'),
    ]
    
    print("Testing XML normalization...")
    print("=" * 60)
    
    for i, (text1, text2) in enumerate(test_cases, 1):
        norm1 = manager._normalize_xml_content(text1)
        norm2 = manager._normalize_xml_content(text2)
        
        matches = norm1 == norm2
        expected_match = text1.replace(' />', '/>').replace('  ', ' ') == text2.replace(' />', '/>').replace('  ', ' ')
        
        print(f"\nTest case {i}:")
        print(f"  Text 1: {text1[:50]}...")
        print(f"  Text 2: {text2[:50]}...")
        print(f"  Normalized match: {matches}")
        print(f"  Expected: {expected_match}")
        
        if i <= 2:
            # Cases 1-2 should match after normalization
            assert matches, f"Test case {i} failed: Expected match but got mismatch"
            print("  ✓ PASS")
        elif i == 3:
            # Case 3 should match (same text)
            assert matches, f"Test case {i} failed: Plain text should match"
            print("  ✓ PASS")
        elif i == 4:
            # Case 4 should not match (different content)
            assert not matches, f"Test case {i} failed: Different content should not match"
            print("  ✓ PASS")
    
    print("\n" + "=" * 60)
    print("All tests passed! ✓")

if __name__ == "__main__":
    test_normalization()
