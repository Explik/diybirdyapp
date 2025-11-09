"""
Unit tests for xml_modifier.py

Tests the generalized XML modification approach that preserves formatting
while allowing surgical edits to specific elements.
"""

import unittest
from pathlib import Path
import tempfile
import shutil
from shared.xml_modifier import XmlModifier, XmlReplacement


class TestXmlModifier(unittest.TestCase):
    """Test the XmlModifier class"""
    
    def setUp(self):
        """Create a temporary directory for test files"""
        self.test_dir = Path(tempfile.mkdtemp())
    
    def tearDown(self):
        """Clean up temporary directory"""
        shutil.rmtree(self.test_dir)
    
    def create_test_xml(self, content: str) -> Path:
        """Helper to create a test XML file"""
        test_file = self.test_dir / "test.xml"
        with open(test_file, 'w', encoding='UTF-8') as f:
            f.write(content)
        return test_file
    
    def test_simple_element_content_update(self):
        """Test updating simple element content"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item id="1">
    <name>Original Name</name>
    <value>100</value>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        # Find the item element
        item = modifier.root.find('.//item[@id="1"]')
        
        # Update the name
        modifier.replace_element_content(item, 'name', 'Updated Name')
        
        # Apply and verify
        self.assertTrue(modifier.apply())
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('<name>Updated Name</name>', result)
        self.assertIn('<value>100</value>', result)  # Unchanged
        self.assertIn('<?xml version="1.0" encoding="UTF-8"?>', result)  # Preserved
    
    def test_no_change_when_content_identical(self):
        """Test that file is not modified when content is identical"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item>
    <name>Same Name</name>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        original_mtime = test_file.stat().st_mtime
        
        modifier = XmlModifier(test_file)
        item = modifier.root.find('.//item')
        
        # Set to same content
        modifier.replace_element_content(item, 'name', 'Same Name')
        
        import time
        time.sleep(0.1)
        
        # Apply - should return False (no changes)
        self.assertFalse(modifier.apply())
        
        # File should not be modified
        new_mtime = test_file.stat().st_mtime
        self.assertEqual(original_mtime, new_mtime)
    
    def test_add_new_element(self):
        """Test adding a new child element that doesn't exist"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item id="1">
    <name>Test Item</name>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item[@id="1"]')
        
        # Add a new element
        modifier.replace_element_content(item, 'description', 'New description')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('<description>New description</description>', result)
        self.assertIn('<name>Test Item</name>', result)  # Original preserved
    
    def test_remove_element_with_empty_content(self):
        """Test removing an element by setting content to empty string"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item>
    <name>Item Name</name>
    <description>To be removed</description>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item')
        
        # Remove description by setting to empty
        modifier.replace_element_content(item, 'description', '')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertNotIn('<description>', result)
        self.assertIn('<name>Item Name</name>', result)
    
    def test_multiple_elements_update(self):
        """Test updating multiple elements in one pass"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item id="1">
    <name>First</name>
    <value>10</value>
  </item>
  <item id="2">
    <name>Second</name>
    <value>20</value>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        # Update both items
        for item in modifier.root.findall('.//item'):
            item_id = item.get('id')
            modifier.replace_element_content(item, 'name', f'Updated-{item_id}')
            modifier.replace_element_content(item, 'value', str(int(item_id) * 100))
        
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('<name>Updated-1</name>', result)
        self.assertIn('<value>100</value>', result)
        self.assertIn('<name>Updated-2</name>', result)
        self.assertIn('<value>200</value>', result)
    
    def test_content_with_special_characters(self):
        """Test content with XML special characters"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item>
    <text>Simple text</text>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item')
        
        # Set content with special chars that should be in the XML
        # (assuming they're already properly formatted as XML)
        modifier.replace_element_content(item, 'text', 'Text with &quot;quotes&quot; and &lt;tags&gt;')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('Text with &quot;quotes&quot; and &lt;tags&gt;', result)
    
    def test_content_with_nested_elements(self):
        """Test content that contains nested XML elements"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item>
    <content>Plain text</content>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item')
        
        # Set content with nested elements (as string)
        new_content = 'Text with <sub id="1">nested</sub> element'
        modifier.replace_element_content(item, 'content', new_content)
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('Text with <sub id="1">nested</sub> element', result)
    
    def test_preserve_formatting_and_indentation(self):
        """Test that original formatting and indentation is preserved"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item id="test">
    <name>Original</name>
    <value>123</value>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item[@id="test"]')
        modifier.replace_element_content(item, 'name', 'Modified')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Check indentation is preserved
        self.assertIn('  <item id="test">', result)
        self.assertIn('    <name>Modified</name>', result)
        self.assertIn('    <value>123</value>', result)
    
    def test_modify_elements_with_xpath(self):
        """Test the modify_elements method with XPath"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item id="1">
    <status>pending</status>
    <count>5</count>
  </item>
  <item id="2">
    <status>pending</status>
    <count>3</count>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        # Modify all items with pending status
        def update_status(element):
            return {
                'status': 'completed',
                'count': str(int(element.find('count').text) + 1)
            }
        
        modifier.modify_elements('.//item', update_status)
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Both items should be updated
        self.assertEqual(result.count('<status>completed</status>'), 2)
        self.assertIn('<count>6</count>', result)
        self.assertIn('<count>4</count>', result)
    
    def test_preview_without_applying(self):
        """Test getting a preview without modifying the file"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item>
    <name>Original</name>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item')
        modifier.replace_element_content(item, 'name', 'Preview Name')
        
        # Get preview
        preview = modifier.get_preview()
        
        # Preview should have the change
        self.assertIn('<name>Preview Name</name>', preview)
        
        # File should still have original content
        with open(test_file, 'r', encoding='UTF-8') as f:
            original = f.read()
        
        self.assertIn('<name>Original</name>', original)
        self.assertNotIn('<name>Preview Name</name>', original)
    
    def test_namespaced_xml(self):
        """Test handling XML with namespaces"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en" target-language="fr">
    <body>
      <trans-unit id="123">
        <source>Hello</source>
        <target>Bonjour</target>
      </trans-unit>
    </body>
  </file>
</xliff>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        # Register namespace for easier finding
        ns = {'xliff': 'urn:oasis:names:tc:xliff:document:1.2'}
        
        # Find trans-unit
        trans_unit = modifier.root.find('.//xliff:trans-unit[@id="123"]', ns)
        
        if trans_unit is None:
            # Try without namespace in find
            trans_unit = modifier.root.find('.//{urn:oasis:names:tc:xliff:document:1.2}trans-unit[@id="123"]')
        
        self.assertIsNotNone(trans_unit, "Could not find trans-unit element")
        
        # Update target
        modifier.replace_element_content(trans_unit, 'target', 'Salut')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('<target>Salut</target>', result)
        self.assertIn('<source>Hello</source>', result)  # Unchanged
    
    def test_complex_nested_structure(self):
        """Test with complex nested structure"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<catalog>
  <product id="p1">
    <details>
      <name>Product One</name>
      <price currency="USD">29.99</price>
    </details>
    <inventory>
      <stock>100</stock>
    </inventory>
  </product>
</catalog>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        # Find nested elements
        product = modifier.root.find('.//product[@id="p1"]')
        details = product.find('details')
        inventory = product.find('inventory')
        
        # Update nested content
        modifier.replace_element_content(details, 'name', 'Updated Product')
        modifier.replace_element_content(details, 'price', '39.99')
        modifier.replace_element_content(inventory, 'stock', '150')
        
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('<name>Updated Product</name>', result)
        self.assertIn('<price currency="USD">39.99</price>', result)
        self.assertIn('<stock>150</stock>', result)
        
        # Verify structure preserved
        self.assertIn('<details>', result)
        self.assertIn('<inventory>', result)
    
    def test_no_duplication_on_multiple_saves(self):
        """Test that content is not duplicated when modifying multiple times"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item>
    <name>Test</name>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        
        # First modification
        modifier1 = XmlModifier(test_file)
        item1 = modifier1.root.find('.//item')
        modifier1.replace_element_content(item1, 'name', 'First Update')
        modifier1.apply()
        
        # Second modification
        modifier2 = XmlModifier(test_file)
        item2 = modifier2.root.find('.//item')
        modifier2.replace_element_content(item2, 'name', 'Second Update')
        modifier2.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Should appear exactly once
        self.assertEqual(result.count('Second Update'), 1)
        self.assertEqual(result.count('<name>'), 1)
        self.assertEqual(result.count('</name>'), 1)
    
    def test_empty_element_handling(self):
        """Test handling of empty elements"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item>
    <name></name>
    <value>123</value>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item')
        
        # Set empty to non-empty
        modifier.replace_element_content(item, 'name', 'Now has content')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('<name>Now has content</name>', result)
    
    def test_replacement_dataclass(self):
        """Test the XmlReplacement dataclass"""
        replacement = XmlReplacement(100, 200, "new content")
        
        self.assertEqual(replacement.start_pos, 100)
        self.assertEqual(replacement.end_pos, 200)
        self.assertEqual(replacement.new_content, "new content")
        
        # Test repr
        repr_str = repr(replacement)
        self.assertIn('100:200', repr_str)
        self.assertIn('new content', repr_str)
    
    def test_find_element_position(self):
        """Test finding element position in file"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item id="unique123">
    <name>Test</name>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item[@id="unique123"]')
        start, end = modifier.find_element_position(item)
        
        # Verify we found the right element
        element_text = modifier.original_content[start:end]
        self.assertIn('item id="unique123"', element_text)
        self.assertIn('<name>Test</name>', element_text)
        self.assertIn('</item>', element_text)
    
    def test_unicode_content(self):
        """Test handling of Unicode content"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item>
    <text>English text</text>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item')
        
        # Set to Unicode content
        modifier.replace_element_content(item, 'text', '中文文本 with émojis 🎉')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        self.assertIn('中文文本 with émojis 🎉', result)


class TestXmlModifierEdgeCases(unittest.TestCase):
    """Test edge cases and error conditions"""
    
    def setUp(self):
        """Create a temporary directory for test files"""
        self.test_dir = Path(tempfile.mkdtemp())
    
    def tearDown(self):
        """Clean up temporary directory"""
        shutil.rmtree(self.test_dir)
    
    def create_test_xml(self, content: str) -> Path:
        """Helper to create a test XML file"""
        test_file = self.test_dir / "test.xml"
        with open(test_file, 'w', encoding='UTF-8') as f:
            f.write(content)
        return test_file
    
    def test_self_closing_tags(self):
        """Test handling of self-closing tags"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <item id="1" />
  <item id="2">
    <name>Has content</name>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        # Find the self-closing item
        items = modifier.root.findall('.//item')
        self.assertEqual(len(items), 2)
        
        # Should be able to find it
        start, end = modifier.find_element_position(items[0])
        self.assertGreater(end, start)
    
    def test_comments_preserved(self):
        """Test that XML comments are preserved"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <!-- This is a comment -->
  <item>
    <name>Test</name>
  </item>
  <!-- Another comment -->
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item')
        modifier.replace_element_content(item, 'name', 'Updated')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # Comments should still be there
        self.assertIn('<!-- This is a comment -->', result)
        self.assertIn('<!-- Another comment -->', result)
        self.assertIn('<name>Updated</name>', result)
    
    def test_cdata_sections(self):
        """Test handling of CDATA sections"""
        xml = '''<?xml version="1.0" encoding="UTF-8"?>
<root>
  <script><![CDATA[
    function test() {
      return true;
    }
  ]]></script>
  <item>
    <name>Test</name>
  </item>
</root>'''
        
        test_file = self.create_test_xml(xml)
        modifier = XmlModifier(test_file)
        
        item = modifier.root.find('.//item')
        modifier.replace_element_content(item, 'name', 'Updated')
        modifier.apply()
        
        with open(test_file, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        # CDATA should be preserved
        self.assertIn('<![CDATA[', result)
        self.assertIn('function test()', result)
        self.assertIn('<name>Updated</name>', result)


if __name__ == '__main__':
    unittest.main()
