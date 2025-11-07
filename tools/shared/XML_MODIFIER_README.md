# XML Modifier - Generalized XML Editing

## Overview

The `XmlModifier` class provides a generalized approach for making surgical edits to XML files while preserving the original file's formatting, whitespace, comments, and structure.

## Key Features

- **Preserves Formatting**: Maintains original indentation, whitespace, and line breaks
- **Surgical Edits**: Only modifies targeted elements, leaving everything else untouched
- **No Duplication**: Ensures content is never accidentally duplicated
- **Change Detection**: Only writes to disk if content actually changed
- **Preview Support**: Can preview changes before applying them
- **Namespace Support**: Works with namespaced XML (like XLIFF)

## How It Works

The approach uses a hybrid strategy:

1. **Parse XML** - Uses ElementTree to understand structure and locate elements
2. **Read as Text** - Reads the original file as text to preserve exact formatting
3. **Calculate Positions** - Determines precise character positions of elements to modify
4. **Apply Replacements** - Makes surgical string replacements in reverse order
5. **Write Back** - Only writes if content changed

## Basic Usage

```python
from pathlib import Path
from shared.xml_modifier import XmlModifier

# Create modifier for an XML file
file_path = Path("data.xml")
modifier = XmlModifier(file_path)

# Find an element using standard ElementTree methods
item = modifier.root.find('.//item[@id="123"]')

# Replace the content of a child element
modifier.replace_element_content(item, 'name', 'New Name')
modifier.replace_element_content(item, 'value', '999')

# Apply changes
modifier.apply()
```

## Advanced Usage

### Modifying Multiple Elements

```python
# Modify all matching elements
def update_status(element):
    current_status = element.find('status').text
    if current_status == 'pending':
        return {
            'status': 'completed',
            'updated_at': '2025-11-07'
        }
    return {}

modifier.modify_elements('.//task', update_status)
modifier.apply()
```

### Preview Changes

```python
# Preview without modifying the file
modifier.replace_element_content(item, 'name', 'Preview Name')
preview_content = modifier.get_preview()
print(preview_content)

# File is still unchanged at this point
# Call apply() to commit changes
modifier.apply()
```

### Adding New Elements

```python
# If the child element doesn't exist, it will be created
modifier.replace_element_content(item, 'description', 'New description')
modifier.apply()
```

### Removing Elements

```python
# Setting content to empty string removes the element
modifier.replace_element_content(item, 'old_field', '')
modifier.apply()
```

### Working with Namespaced XML

```python
# For namespaced XML like XLIFF
modifier = XmlModifier(xliff_file)

# Use namespace in XPath
ns = {'xliff': 'urn:oasis:names:tc:xliff:document:1.2'}
trans_unit = modifier.root.find('.//xliff:trans-unit[@id="123"]', ns)

# Or use full namespace
trans_unit = modifier.root.find(
    './/{urn:oasis:names:tc:xliff:document:1.2}trans-unit[@id="123"]'
)

# Modify (use simple tag name without namespace)
modifier.replace_element_content(trans_unit, 'target', 'New translation')
modifier.apply()
```

## Example: Refactoring locale_manager.py

Here's how to refactor the existing `locale_manager.py` to use `XmlModifier`:

```python
from shared.xml_modifier import XmlModifier

def save_xliff(self, filepath: Path, source_lang: str, target_lang: str, 
               units: List[TranslationUnit]):
    """Save translation units to XLIFF file, preserving original formatting"""
    
    # Create modifier
    modifier = XmlModifier(filepath)
    
    # Define namespace
    XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"
    
    # Process each trans-unit
    for trans_unit_elem in modifier.root.findall(f'.//{{{XLIFF_NS}}}trans-unit'):
        unit_id = trans_unit_elem.get('id', '')
        
        # Find matching unit in our data
        matching_unit = next((u for u in units if u.id == unit_id), None)
        
        if not matching_unit:
            continue
        
        # Serialize target content
        new_target_content = self._serialize_target_content(matching_unit.target)
        
        # Update target element
        modifier.replace_element_content(trans_unit_elem, 'target', new_target_content)
    
    # Apply all changes
    modifier.apply()
```

This refactored version is:
- **Much shorter** (from ~130 lines to ~20 lines)
- **Easier to understand** (declarative rather than imperative)
- **Less error-prone** (no manual position calculation)
- **Well-tested** (leverages tested XmlModifier)

## API Reference

### XmlModifier Class

#### Constructor

```python
XmlModifier(filepath: Path, encoding: str = 'UTF-8')
```

- `filepath`: Path to the XML file to modify
- `encoding`: File encoding (default: UTF-8)

#### Methods

##### replace_element_content

```python
replace_element_content(element: ET.Element, child_tag: str, new_content: str)
```

Replace the text content of a child element.

- `element`: Parent element containing the child
- `child_tag`: Tag name of the child element to modify
- `new_content`: New text content (or empty string to remove element)

##### modify_elements

```python
modify_elements(xpath: str, modifier_func: Callable[[ET.Element], dict])
```

Modify multiple elements matching an XPath expression.

- `xpath`: XPath expression to find elements
- `modifier_func`: Function that returns dict of {child_tag: new_content}

##### apply

```python
apply() -> bool
```

Apply all pending replacements to the file.

Returns: True if file was modified, False if no changes needed

##### get_preview

```python
get_preview() -> str
```

Get a preview of the modified content without applying changes.

Returns: The modified content as a string

##### find_element_position

```python
find_element_position(element: ET.Element) -> Tuple[int, int]
```

Find the position of an element in the original file.

Returns: Tuple of (start_position, end_position)

## Testing

The module includes comprehensive unit tests in `test_xml_modifier.py`:

```bash
cd tools
python -m unittest test_xml_modifier -v
```

Tests cover:
- Simple element updates
- Adding new elements
- Removing elements
- Multiple element updates
- Formatting preservation
- Namespace handling
- Unicode content
- Edge cases (comments, CDATA, self-closing tags)
- No duplication on multiple saves

## Performance Considerations

- **Efficient**: Only parses XML once and makes targeted replacements
- **Safe**: Only writes if content actually changed
- **Memory**: Keeps original content in memory (fine for typical XML files)
- **Large Files**: For very large files (>100MB), consider streaming approaches

## Limitations

- Requires well-formed XML
- Assumes elements can be uniquely identified (via id or position)
- Best suited for files with unique element identifiers
- Not optimized for extremely large files (>100MB)

## Future Enhancements

Potential improvements:
- Support for batch processing multiple files
- XPath-based modification patterns
- Diff generation
- Rollback/undo support
- Streaming support for large files
