"""
Example demonstrating the XmlModifier in action.

This script shows how to use XmlModifier to update XML files
while preserving formatting.
"""

from pathlib import Path
import tempfile
from shared.xml_modifier import XmlModifier


def example_basic_usage():
    """Basic example: Update a simple XML file"""
    print("=" * 60)
    print("Example 1: Basic Element Update")
    print("=" * 60)
    
    # Create sample XML
    xml_content = '''<?xml version="1.0" encoding="UTF-8"?>
<catalog>
  <product id="p001">
    <name>Widget</name>
    <price>19.99</price>
    <stock>100</stock>
  </product>
  <product id="p002">
    <name>Gadget</name>
    <price>29.99</price>
    <stock>50</stock>
  </product>
</catalog>'''
    
    # Create temp file
    with tempfile.NamedTemporaryFile(mode='w', suffix='.xml', delete=False, encoding='UTF-8') as f:
        f.write(xml_content)
        temp_path = Path(f.name)
    
    print("\nOriginal XML:")
    print(xml_content)
    
    try:
        # Create modifier
        modifier = XmlModifier(temp_path)
        
        # Find and update first product
        product = modifier.root.find('.//product[@id="p001"]')
        modifier.replace_element_content(product, 'price', '24.99')
        modifier.replace_element_content(product, 'stock', '75')
        
        # Preview changes
        print("\n" + "-" * 60)
        print("Preview of changes:")
        print("-" * 60)
        preview = modifier.get_preview()
        
        # Show just the changed product
        import re
        product_match = re.search(r'<product id="p001">.*?</product>', preview, re.DOTALL)
        if product_match:
            print(product_match.group(0))
        
        # Apply changes
        modified = modifier.apply()
        print(f"\nFile modified: {modified}")
        
        # Read back to show result
        with open(temp_path, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        print("\nFinal XML:")
        print(result)
        
    finally:
        temp_path.unlink()


def example_batch_update():
    """Example: Update multiple elements at once"""
    print("\n" + "=" * 60)
    print("Example 2: Batch Update with modify_elements")
    print("=" * 60)
    
    xml_content = '''<?xml version="1.0" encoding="UTF-8"?>
<tasks>
  <task id="1">
    <status>pending</status>
    <priority>low</priority>
  </task>
  <task id="2">
    <status>pending</status>
    <priority>high</priority>
  </task>
  <task id="3">
    <status>completed</status>
    <priority>medium</priority>
  </task>
</tasks>'''
    
    with tempfile.NamedTemporaryFile(mode='w', suffix='.xml', delete=False, encoding='UTF-8') as f:
        f.write(xml_content)
        temp_path = Path(f.name)
    
    print("\nOriginal XML:")
    print(xml_content)
    
    try:
        modifier = XmlModifier(temp_path)
        
        # Update all pending tasks to in-progress
        def update_pending_tasks(element):
            status_elem = element.find('status')
            if status_elem is not None and status_elem.text == 'pending':
                return {'status': 'in-progress'}
            return {}
        
        modifier.modify_elements('.//task', update_pending_tasks)
        modifier.apply()
        
        with open(temp_path, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        print("\nUpdated XML (pending → in-progress):")
        print(result)
        
    finally:
        temp_path.unlink()


def example_add_remove_elements():
    """Example: Add new elements and remove existing ones"""
    print("\n" + "=" * 60)
    print("Example 3: Add and Remove Elements")
    print("=" * 60)
    
    xml_content = '''<?xml version="1.0" encoding="UTF-8"?>
<config>
  <settings>
    <debug>true</debug>
    <temp_setting>delete_me</temp_setting>
  </settings>
</config>'''
    
    with tempfile.NamedTemporaryFile(mode='w', suffix='.xml', delete=False, encoding='UTF-8') as f:
        f.write(xml_content)
        temp_path = Path(f.name)
    
    print("\nOriginal XML:")
    print(xml_content)
    
    try:
        modifier = XmlModifier(temp_path)
        
        settings = modifier.root.find('.//settings')
        
        # Add new element
        modifier.replace_element_content(settings, 'version', '2.0')
        modifier.replace_element_content(settings, 'max_connections', '100')
        
        # Remove temp_setting
        modifier.replace_element_content(settings, 'temp_setting', '')
        
        modifier.apply()
        
        with open(temp_path, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        print("\nUpdated XML (added version & max_connections, removed temp_setting):")
        print(result)
        
    finally:
        temp_path.unlink()


def example_xliff_translation():
    """Example: Update XLIFF translation file (like locale_manager)"""
    print("\n" + "=" * 60)
    print("Example 4: XLIFF Translation File")
    print("=" * 60)
    
    xliff_content = '''<?xml version="1.0" encoding="UTF-8"?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="zh-CN" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="123" datatype="html">
        <source>Hello</source>
        <target>你好</target>
      </trans-unit>
      <trans-unit id="456" datatype="html">
        <source>Welcome</source>
      </trans-unit>
    </body>
  </file>
</xliff>'''
    
    with tempfile.NamedTemporaryFile(mode='w', suffix='.xlf', delete=False, encoding='UTF-8') as f:
        f.write(xliff_content)
        temp_path = Path(f.name)
    
    print("\nOriginal XLIFF:")
    print(xliff_content)
    
    try:
        modifier = XmlModifier(temp_path)
        
        XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"
        
        # Update translation for id=123
        trans_unit_123 = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="123"]')
        modifier.replace_element_content(trans_unit_123, 'target', '您好')
        
        # Add translation for id=456
        trans_unit_456 = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="456"]')
        modifier.replace_element_content(trans_unit_456, 'target', '欢迎')
        
        modifier.apply()
        
        with open(temp_path, 'r', encoding='UTF-8') as f:
            result = f.read()
        
        print("\nUpdated XLIFF:")
        print(result)
        
    finally:
        temp_path.unlink()


def example_no_unnecessary_writes():
    """Example: File is not written when content is unchanged"""
    print("\n" + "=" * 60)
    print("Example 5: No Unnecessary File Writes")
    print("=" * 60)
    
    xml_content = '''<?xml version="1.0" encoding="UTF-8"?>
<data>
  <value>123</value>
</data>'''
    
    with tempfile.NamedTemporaryFile(mode='w', suffix='.xml', delete=False, encoding='UTF-8') as f:
        f.write(xml_content)
        temp_path = Path(f.name)
    
    try:
        # Get original modification time
        original_mtime = temp_path.stat().st_mtime
        
        import time
        time.sleep(0.1)
        
        # Try to set same value
        modifier = XmlModifier(temp_path)
        # root is the data element itself in this case
        root = modifier.root
        modifier.replace_element_content(root, 'value', '123')
        
        was_modified = modifier.apply()
        
        new_mtime = temp_path.stat().st_mtime
        
        print(f"\nOriginal content: {xml_content.strip()}")
        print(f"Attempted to set value to same content ('123')")
        print(f"File was modified: {was_modified}")
        print(f"Modification time changed: {original_mtime != new_mtime}")
        
        if not was_modified and original_mtime == new_mtime:
            print("✓ File was NOT unnecessarily written!")
        
    finally:
        temp_path.unlink()


if __name__ == '__main__':
    example_basic_usage()
    example_batch_update()
    example_add_remove_elements()
    example_xliff_translation()
    example_no_unnecessary_writes()
    
    print("\n" + "=" * 60)
    print("All examples completed!")
    print("=" * 60)
