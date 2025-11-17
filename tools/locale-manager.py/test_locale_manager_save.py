"""
Unit tests for locale_manager.py save functionality
Tests that only target content is changed and nothing else
"""

import unittest
from pathlib import Path
import tempfile
import shutil
from app import LocaleManager, TranslationUnit


class TestLocaleSave(unittest.TestCase):
    """Test the save functionality to ensure only targets are modified"""
    
    def setUp(self):
        """Create a temporary directory for test files"""
        self.test_dir = Path(tempfile.mkdtemp())
        
    def tearDown(self):
        """Clean up temporary directory"""
        shutil.rmtree(self.test_dir)
    
    def create_test_xliff(self, content: str) -> Path:
        """Helper to create a test XLIFF file"""
        test_file = self.test_dir / "test.xlf"
        with open(test_file, 'w', encoding='UTF-8') as f:
            f.write(content)
        return test_file
    
    def test_simple_target_update(self):
        """Test updating a simple target preserves everything else"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="123" datatype="html">
        <source>Hello</source>
        <target>你好</target>
        <context-group purpose="location">
          <context context-type="sourcefile">test.html</context>
        </context-group>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Update the target
        units = [TranslationUnit("123", "Hello", "您好")]
        manager.save_xliff(test_file, "en-US", "zh-CN", units)
        
        # Read back
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Check that only target changed
        self.assertIn('<target>您好</target>', result)
        self.assertIn('<source>Hello</source>', result)
        self.assertIn('id="123"', result)
        self.assertIn('source-language="en-US"', result)
        self.assertIn('<context-group purpose="location">', result)
        
        # Check exact line preservation
        self.assertIn('<?xml version="1.0" encoding="UTF-8" ?>', result)
        
    def test_target_with_placeholders(self):
        """Test that XML placeholders in targets are preserved correctly"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="456" datatype="html">
        <source>Hello <x id="INTERPOLATION" equiv-text="{{name}}" /></source>
        <target>你好 <x id="INTERPOLATION" equiv-text="{{name}}" /></target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Update with new placeholder content
        new_target = '您好 <x id="INTERPOLATION" equiv-text="{{name}}" />'
        units = [TranslationUnit("456", 'Hello <x id="INTERPOLATION" equiv-text="{{name}}" />', new_target)]
        manager.save_xliff(test_file, "en-US", "zh-CN", units)
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Verify placeholder is preserved
        self.assertIn('<target>您好 <x id="INTERPOLATION" equiv-text="{{name}}" /></target>', result)
        # Source should be unchanged
        self.assertIn('<source>Hello <x id="INTERPOLATION" equiv-text="{{name}}" /></source>', result)
    
    def test_no_duplication(self):
        """Test that content is not duplicated"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="789" datatype="html">
        <source>Select a language...</source>
        <target>选择一种语言...</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Parse and save without changes
        source_lang, target_lang, units = manager.parse_xliff(test_file)
        manager.save_xliff(test_file, source_lang, target_lang, units)
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Count occurrences - should be exactly 1
        target_count = result.count('选择一种语言...')
        self.assertEqual(target_count, 1, f"Target text appears {target_count} times, expected 1")
        
    def test_escaped_quotes_preserved(self):
        """Test that escaped quotes in attributes are preserved"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="999" datatype="html">
        <source>Deck &quot;<x id="INTERPOLATION" equiv-text="{{name}}" />&quot;</source>
        <target>卡组 &quot;<x id="INTERPOLATION" equiv-text="{{name}}" />&quot;</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Parse and save
        source_lang, target_lang, units = manager.parse_xliff(test_file)
        manager.save_xliff(test_file, source_lang, target_lang, units)
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Verify escaped quotes remain
        self.assertIn('&quot;', result)
        self.assertIn('<source>Deck &quot;', result)
        
    def test_no_change_when_content_identical(self):
        """Test that file is not modified when content is identical"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="111" datatype="html">
        <source>Test</source>
        <target>测试</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        
        # Get original modification time
        original_mtime = test_file.stat().st_mtime
        
        manager = LocaleManager(self.test_dir)
        source_lang, target_lang, units = manager.parse_xliff(test_file)
        
        import time
        time.sleep(0.1)  # Ensure time difference if file is modified
        
        manager.save_xliff(test_file, source_lang, target_lang, units)
        
        # Check file wasn't modified
        new_mtime = test_file.stat().st_mtime
        self.assertEqual(original_mtime, new_mtime, "File was modified when it shouldn't be")
        
    def test_add_new_target(self):
        """Test adding a new target element"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="222" datatype="html">
        <source>New item</source>
        <context-group purpose="location">
          <context context-type="sourcefile">test.html</context>
        </context-group>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Add target
        units = [TranslationUnit("222", "New item", "新项目")]
        manager.save_xliff(test_file, "en-US", "zh-CN", units)
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Check target was added
        self.assertIn('<target>新项目</target>', result)
        # Source unchanged
        self.assertIn('<source>New item</source>', result)
        # Context preserved
        self.assertIn('<context-group purpose="location">', result)
        
    def test_remove_empty_target(self):
        """Test removing a target when content becomes empty"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="333" datatype="html">
        <source>Remove me</source>
        <target>删除我</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Set target to empty
        units = [TranslationUnit("333", "Remove me", "")]
        manager.save_xliff(test_file, "en-US", "zh-CN", units)
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Target should be removed
        self.assertNotIn('<target>', result)
        # Source preserved
        self.assertIn('<source>Remove me</source>', result)
        
    def test_multiple_units_update(self):
        """Test updating multiple translation units at once"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="001" datatype="html">
        <source>One</source>
        <target>一</target>
      </trans-unit>
      <trans-unit id="002" datatype="html">
        <source>Two</source>
        <target>二</target>
      </trans-unit>
      <trans-unit id="003" datatype="html">
        <source>Three</source>
        <target>三</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Update multiple
        units = [
            TranslationUnit("001", "One", "壹"),
            TranslationUnit("002", "Two", "贰"),
            TranslationUnit("003", "Three", "叁")
        ]
        manager.save_xliff(test_file, "en-US", "zh-CN", units)
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # All targets updated
        self.assertIn('<target>壹</target>', result)
        self.assertIn('<target>贰</target>', result)
        self.assertIn('<target>叁</target>', result)
        
        # No duplication
        self.assertEqual(result.count('<target>壹</target>'), 1)
        self.assertEqual(result.count('<target>贰</target>'), 1)
        self.assertEqual(result.count('<target>叁</target>'), 1)
    
    def test_parse_and_resave_cycle(self):
        """Test that parsing and re-saving doesn't duplicate content"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="763874197462628538" datatype="html">
        <source><x id="START_TAG_OPTION" ctype="x-option" equiv-text="&lt;option value=&quot;&quot;&gt;" />Select a language...<x id="CLOSE_TAG_OPTION" ctype="x-option" equiv-text="&lt;/option&gt;" /></source>
        <target><x id="START_TAG_OPTION" ctype="x-option" equiv-text="&lt;option value=&quot;&quot;&gt;" />选择一种语言...<x id="CLOSE_TAG_OPTION" ctype="x-option" equiv-text="&lt;/option&gt;" /></target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Parse
        source_lang, target_lang, units = manager.parse_xliff(test_file)
        
        # Check parsed content
        self.assertEqual(len(units), 1)
        self.assertIn('选择一种语言...', units[0].target)
        
        # Count occurrences in parsed data
        target_count_in_parsed = units[0].target.count('选择一种语言...')
        self.assertEqual(target_count_in_parsed, 1, f"Parsed target has {target_count_in_parsed} occurrences")
        
        # Save back
        manager.save_xliff(test_file, source_lang, target_lang, units)
        
        # Read and verify
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Should appear exactly once in target
        target_count = result.count('选择一种语言...')
        self.assertEqual(target_count, 1, f"After save, target appears {target_count} times, expected 1")
        
        # Verify the complete target line
        self.assertIn('/>选择一种语言...<x id="CLOSE_TAG_OPTION"', result)
        self.assertNotIn('/>选择一种语言...选择一种语言...', result)
    
    def test_complex_placeholder_content(self):
        """Test complex content with multiple placeholders and text"""
        original = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="888" datatype="html">
        <source>Hello <x id="START" /> world <x id="END" /> test</source>
        <target>你好 <x id="START" /> 世界 <x id="END" /> 测试</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xliff(original)
        manager = LocaleManager(self.test_dir)
        
        # Parse
        source_lang, target_lang, units = manager.parse_xliff(test_file)
        
        # Verify parsing
        self.assertIn('<x id="START" />', units[0].target)
        self.assertIn('<x id="END" />', units[0].target)
        self.assertEqual(units[0].target.count('测试'), 1)
        
        # Modify and save
        units[0].target = '您好 <x id="START" /> 世界 <x id="END" /> 试验'
        manager.save_xliff(test_file, source_lang, target_lang, units)
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Verify no duplication
        self.assertEqual(result.count('试验'), 1)
        self.assertIn('您好 <x id="START" /> 世界 <x id="END" /> 试验', result)


if __name__ == '__main__':
    unittest.main()
