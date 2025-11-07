# XmlModifier Quick Reference

## Installation
No installation needed - just import from `shared.xml_modifier`

## Basic Import
```python
from pathlib import Path
from shared.xml_modifier import XmlModifier
```

## Quick Start

### 1. Update an Element
```python
modifier = XmlModifier(Path("data.xml"))
item = modifier.root.find('.//item[@id="123"]')
modifier.replace_element_content(item, 'name', 'New Name')
modifier.apply()
```

### 2. Add a New Element
```python
modifier = XmlModifier(Path("data.xml"))
item = modifier.root.find('.//item')
modifier.replace_element_content(item, 'new_field', 'New Value')
modifier.apply()
```

### 3. Remove an Element
```python
modifier = XmlModifier(Path("data.xml"))
item = modifier.root.find('.//item')
modifier.replace_element_content(item, 'old_field', '')  # Empty string removes
modifier.apply()
```

### 4. Update Multiple Elements
```python
modifier = XmlModifier(Path("data.xml"))

def update_status(element):
    if element.find('status').text == 'pending':
        return {'status': 'completed', 'updated_at': '2025-11-07'}
    return {}

modifier.modify_elements('.//task', update_status)
modifier.apply()
```

### 5. Preview Before Applying
```python
modifier = XmlModifier(Path("data.xml"))
item = modifier.root.find('.//item')
modifier.replace_element_content(item, 'name', 'Preview')

# Preview changes
preview = modifier.get_preview()
print(preview)

# File is still unchanged - call apply() to commit
modifier.apply()
```

### 6. Working with Namespaced XML (XLIFF)
```python
modifier = XmlModifier(Path("messages.xlf"))
XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"

# Find element with namespace
trans_unit = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="123"]')

# Update (use simple tag name)
modifier.replace_element_content(trans_unit, 'target', 'New translation')
modifier.apply()
```

## API Cheat Sheet

### XmlModifier(filepath, encoding='UTF-8')
Create a modifier instance.

### .root
Access to ElementTree root element for finding elements.

### .replace_element_content(element, child_tag, new_content)
- `element`: Parent element
- `child_tag`: Child element tag name (without namespace)
- `new_content`: New text content (empty string to remove)

### .modify_elements(xpath, modifier_func)
- `xpath`: XPath expression
- `modifier_func`: Function returning {tag: content} dict

### .apply() → bool
Apply changes to file. Returns True if modified, False if no changes.

### .get_preview() → str
Preview changes without modifying file.

## Common Patterns

### Pattern 1: Conditional Update
```python
modifier = XmlModifier(filepath)
for item in modifier.root.findall('.//item'):
    status = item.find('status').text
    if status == 'old':
        modifier.replace_element_content(item, 'status', 'new')
modifier.apply()
```

### Pattern 2: Bulk Translation Update
```python
modifier = XmlModifier(xliff_file)
XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"

translations = {
    '001': 'Translation 1',
    '002': 'Translation 2',
}

for unit_id, translation in translations.items():
    trans_unit = modifier.root.find(f'.//{{{XLIFF_NS}}}trans-unit[@id="{unit_id}"]')
    if trans_unit:
        modifier.replace_element_content(trans_unit, 'target', translation)

modifier.apply()
```

### Pattern 3: Add Missing Fields
```python
modifier = XmlModifier(filepath)
for item in modifier.root.findall('.//item'):
    # Add field if it doesn't exist
    if item.find('timestamp') is None:
        modifier.replace_element_content(item, 'timestamp', '2025-11-07')
modifier.apply()
```

## Key Features Checklist

- ✅ Preserves formatting, indentation, whitespace
- ✅ Only modifies targeted elements
- ✅ Avoids unnecessary file writes (change detection)
- ✅ Supports adding, updating, removing elements
- ✅ Works with namespaced XML
- ✅ Handles complex content (placeholders, nested elements)
- ✅ Preview support
- ✅ No content duplication
- ✅ Unicode support

## Testing

Run tests:
```bash
cd tools
python -m unittest test_xml_modifier -v
python -m unittest test_xml_modifier_xliff_integration -v
```

Run examples:
```bash
cd tools
python example_xml_modifier.py
```

## When to Use

**Good for:**
- XLIFF/translation files
- Configuration files
- Data files with specific structure
- Any XML where formatting matters
- Files edited by both humans and code

**Not ideal for:**
- Very large files (>100MB)
- High-frequency updates (use database)
- Streaming scenarios
- XML without unique element identifiers

## Documentation

- Full docs: `shared/XML_MODIFIER_README.md`
- Summary: `shared/XML_MODIFIER_SUMMARY.md`
- Examples: `example_xml_modifier.py`
- Tests: `test_xml_modifier.py`, `test_xml_modifier_xliff_integration.py`
