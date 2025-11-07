"""
Test that locale manager doesn't make unnecessary updates to unchanged content
"""

import sys
from pathlib import Path
import shutil

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent))

from locale_manager import LocaleManager

def test_no_false_updates():
    """Test that saving without changes doesn't modify the file"""
    
    # Use the actual zh-CN locale file
    locale_dir = Path(__file__).parent.parent / "web" / "src" / "locale"
    locale_file = locale_dir / "messages.zh-CN.xlf"
    
    if not locale_file.exists():
        print(f"❌ Test file not found: {locale_file}")
        return False
    
    # Create a backup
    backup_file = locale_file.with_suffix('.xlf.backup')
    shutil.copy2(locale_file, backup_file)
    
    try:
        # Read original content
        with open(locale_file, 'r', encoding='UTF-8') as f:
            original_content = f.read()
        
        print("Testing no false updates...")
        print("=" * 60)
        
        # Parse the file
        manager = LocaleManager()
        source_lang, target_lang, units = manager.parse_xliff(locale_file)
        
        print(f"Loaded {len(units)} translation units")
        
        # Save without making ANY changes
        manager.save_xliff(locale_file, source_lang, target_lang, units)
        
        # Read the content after save
        with open(locale_file, 'r', encoding='UTF-8') as f:
            new_content = f.read()
        
        # Compare
        if original_content == new_content:
            print("✓ PASS: File unchanged after save with no modifications")
            return True
        else:
            print("❌ FAIL: File was modified even though no changes were made!")
            
            # Show what changed
            orig_lines = original_content.split('\n')
            new_lines = new_content.split('\n')
            
            print("\nDifferences found:")
            for i, (orig, new) in enumerate(zip(orig_lines, new_lines), 1):
                if orig != new:
                    print(f"Line {i}:")
                    print(f"  OLD: {orig[:100]}")
                    print(f"  NEW: {new[:100]}")
                    if i > 10:  # Limit output
                        print("  ... (more differences)")
                        break
            
            return False
    
    finally:
        # Restore from backup
        shutil.copy2(backup_file, locale_file)
        backup_file.unlink()
        print("\nRestored original file from backup")

if __name__ == "__main__":
    success = test_no_false_updates()
    sys.exit(0 if success else 1)
