"""
Integration test to verify XmlModifier works correctly with XLIFF files
from the locale_manager use case.

This test uses actual XLIFF content from the project to ensure the
generalized XmlModifier can handle the real-world scenario.
"""

import unittest
from pathlib import Path
import tempfile
import shutil
from shared.xml_modifier import XmlModifier


class TestXmlModifierWithXliff(unittest.TestCase):
    """Test XmlModifier with actual XLIFF content"""
    
    def setUp(self):
        """Create a temporary directory for test files"""
        self.test_dir = Path(tempfile.mkdtemp())
    
    def tearDown(self):
        """Clean up temporary directory"""
        shutil.rmtree(self.test_dir)
    
    def create_xliff_file(self, content: str) -> Path:
        """Helper to create an XLIFF file"""
        test_file = self.test_dir / "messages.xlf"
        with open(test_file, 'w', encoding='UTF-8') as f:
            f.write(content)
        return test_file
    
    def test_xliff_with_placeholders(self):
        """Test updating XLIFF with complex placeholders (actual use case)"""
        xliff = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="763874197462628538" datatype="html">
        <source><x id="START_TAG_OPTION" ctype="x-option" equiv-text="&lt;option value=&quot;&quot;&gt;" />Select a language...<x id="CLOSE_TAG_OPTION" ctype="x-option" equiv-text="&lt;/option&gt;" /></source>
        <target><x id="START_TAG_OPTION" ctype="x-option" equiv-text="&lt;option value=&quot;&quot;&gt;" />选择一种语言...<x id="CLOSE_TAG_OPTION" ctype="x-option" equiv-text="&lt;/option&gt;" /></target>
        <context-group purpose="location">
          <context context-type="sourcefile">src/app/modules/decks/components/deck-card/deck-card.component.html</context>
          <context context-type="linenumber">13,14</context>
        </context-group>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_xliff_file(xliff)
        
        # Use XmlModifier to update the target
        modifier = XmlModifier(test_file)
        
        XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"
        trans_unit = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="763874197462628538"]')
        
        self.assertIsNotNone(trans_unit, "trans-unit should be found")
        
        # Update with new translation
        new_target = '<x id="START_TAG_OPTION" ctype="x-option" equiv-text="&lt;option value=&quot;&quot;&gt;" />选择语言...<x id="CLOSE_TAG_OPTION" ctype="x-option" equiv-text="&lt;/option&gt;" />'
        modifier.replace_element_content(trans_unit, 'target', new_target)
        
        # Apply changes
        self.assertTrue(modifier.apply())
        
        # Verify result
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Check the new target is there
        self.assertIn('选择语言...', result)
        # Original should be gone
        self.assertNotIn('选择一种语言...', result)
        # Source should be unchanged
        self.assertIn('Select a language...', result)
        # Context should be preserved
        self.assertIn('<context-group purpose="location">', result)
        self.assertIn('deck-card.component.html', result)
        
        # Verify no duplication
        self.assertEqual(result.count('<target>'), 1)
        self.assertEqual(result.count('</target>'), 1)
    
    def test_xliff_parse_and_resave(self):
        """Test that parse and re-save cycle doesn't duplicate content"""
        xliff = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="123" datatype="html">
        <source>Hello</source>
        <target>你好</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_xliff_file(xliff)
        
        # First modification
        modifier1 = XmlModifier(test_file)
        XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"
        trans_unit = modifier1.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="123"]')
        modifier1.replace_element_content(trans_unit, 'target', '您好')
        modifier1.apply()
        
        # Second modification (re-parse and save)
        modifier2 = XmlModifier(test_file)
        trans_unit2 = modifier2.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="123"]')
        # Set to same value
        modifier2.replace_element_content(trans_unit2, 'target', '您好')
        was_modified = modifier2.apply()
        
        # Should not modify file (content is same)
        self.assertFalse(was_modified)
        
        # Verify no duplication
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertEqual(result.count('您好'), 1)
        self.assertEqual(result.count('<target>'), 1)
    
    def test_xliff_multiple_trans_units(self):
        """Test updating multiple trans-units"""
        xliff = '''<?xml version="1.0" encoding="UTF-8" ?>
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
        
        test_file = self.create_xliff_file(xliff)
        modifier = XmlModifier(test_file)
        
        XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"
        
        # Update all targets
        translations = {
            '001': '壹',
            '002': '贰', 
            '003': '叁'
        }
        
        for unit_id, new_target in translations.items():
            trans_unit = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="{unit_id}"]')
            if trans_unit is not None:
                modifier.replace_element_content(trans_unit, 'target', new_target)
        
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # All should be updated
        self.assertIn('<target>壹</target>', result)
        self.assertIn('<target>贰</target>', result)
        self.assertIn('<target>叁</target>', result)
        
        # No duplication
        self.assertEqual(result.count('<target>壹</target>'), 1)
        self.assertEqual(result.count('<target>贰</target>'), 1)
        self.assertEqual(result.count('<target>叁</target>'), 1)
    
    def test_xliff_add_missing_target(self):
        """Test adding a target element that doesn't exist"""
        xliff = '''<?xml version="1.0" encoding="UTF-8" ?>
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
        
        test_file = self.create_xliff_file(xliff)
        modifier = XmlModifier(test_file)
        
        XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"
        trans_unit = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="222"]')
        
        # Add target
        modifier.replace_element_content(trans_unit, 'target', '新项目')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Target should be added
        self.assertIn('<target>新项目</target>', result)
        # Source and context preserved
        self.assertIn('<source>New item</source>', result)
        self.assertIn('<context-group purpose="location">', result)
    
    def test_xliff_remove_target(self):
        """Test removing a target by setting to empty"""
        xliff = '''<?xml version="1.0" encoding="UTF-8" ?>
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
        
        test_file = self.create_xliff_file(xliff)
        modifier = XmlModifier(test_file)
        
        XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"
        trans_unit = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="333"]')
        
        # Remove target
        modifier.replace_element_content(trans_unit, 'target', '')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Target should be gone
        self.assertNotIn('<target>', result)
        # Source preserved
        self.assertIn('<source>Remove me</source>', result)
    
    def test_xliff_formatting_preserved(self):
        """Test that XLIFF formatting is completely preserved"""
        xliff = '''<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="test123" datatype="html">
        <source>Test</source>
        <target>测试</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_xliff_file(xliff)
        modifier = XmlModifier(test_file)
        
        XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"
        trans_unit = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="test123"]')
        
        # Update target
        modifier.replace_element_content(trans_unit, 'target', '试验')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Check formatting preserved
        self.assertIn('<?xml version="1.0" encoding="UTF-8" ?>', result)
        self.assertIn('  <file source-language="en-US"', result)
        self.assertIn('    <body>', result)
        self.assertIn('      <trans-unit id="test123"', result)
        self.assertIn('        <target>试验</target>', result)


if __name__ == '__main__':
    unittest.main()
