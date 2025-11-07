"""
XML Modifier - A generalized approach for surgical XML editing

This module provides a way to modify specific XML elements while preserving
the original file's formatting, whitespace, comments, and structure.

The approach:
1. Parse XML to understand structure and locate elements
2. Read original file as text to preserve exact formatting
3. Calculate precise positions of elements to modify
4. Apply surgical replacements without affecting the rest of the file
"""

import xml.etree.ElementTree as ET
import re
from typing import Callable, List, Optional, Tuple
from pathlib import Path
from dataclasses import dataclass


@dataclass
class XmlReplacement:
    """Represents a single replacement operation in an XML file"""
    start_pos: int  # Starting position in the original content
    end_pos: int    # Ending position in the original content
    new_content: str  # New content to replace with
    
    def __repr__(self):
        preview = self.new_content[:30] + "..." if len(self.new_content) > 30 else self.new_content
        return f"XmlReplacement({self.start_pos}:{self.end_pos}, '{preview}')"


class XmlModifier:
    """
    Performs surgical modifications to XML files while preserving formatting.
    
    This class allows you to modify specific XML elements without disturbing
    the rest of the file's formatting, whitespace, or structure.
    """
    
    def __init__(self, filepath: Path, encoding: str = 'UTF-8'):
        """
        Initialize the XML modifier.
        
        Args:
            filepath: Path to the XML file to modify
            encoding: File encoding (default: UTF-8)
        """
        self.filepath = filepath
        self.encoding = encoding
        self.tree = ET.parse(filepath)
        self.root = self.tree.getroot()
        
        # Read original content
        with open(filepath, 'rb') as f:
            self.original_content = f.read().decode(encoding)
        
        self.replacements: List[XmlReplacement] = []
    
    def find_element_position(self, element: ET.Element, parent: ET.Element = None) -> Tuple[int, int]:
        """
        Find the position of an XML element in the original file.
        
        Args:
            element: The element to locate
            parent: Parent element (helps with context)
            
        Returns:
            Tuple of (start_position, end_position) in the original content
        """
        # Build a unique identifier for this element
        tag_name = element.tag.split('}')[-1] if '}' in element.tag else element.tag
        
        # Try to find element by unique attributes
        search_patterns = []
        
        # Pattern 1: Using id attribute if available
        if 'id' in element.attrib:
            element_id = element.attrib['id']
            pattern = f'<{tag_name}[^>]*id="{re.escape(element_id)}"'
            search_patterns.append(pattern)
        
        # Pattern 2: Using other unique attributes
        for attr_name, attr_value in element.attrib.items():
            if attr_name != 'id':
                pattern = f'<{tag_name}[^>]*{attr_name}="{re.escape(attr_value)}"'
                search_patterns.append(pattern)
        
        # Pattern 3: Just the tag name (less reliable)
        search_patterns.append(f'<{tag_name}[>\\s]')
        
        # Try each pattern
        for pattern in search_patterns:
            match = re.search(pattern, self.original_content)
            if match:
                start_pos = match.start()
                
                # Find the end of this element
                # Look for the closing tag
                closing_tag = f'</{tag_name}>'
                end_match = re.search(re.escape(closing_tag), self.original_content[start_pos:])
                
                if end_match:
                    end_pos = start_pos + end_match.end()
                    return start_pos, end_pos
                
                # Check if it's a self-closing tag
                self_close_match = re.search(r'/>', self.original_content[start_pos:start_pos+200])
                if self_close_match:
                    end_pos = start_pos + self_close_match.end()
                    return start_pos, end_pos
        
        raise ValueError(f"Could not locate element {tag_name} in the original file")
    
    def find_child_element_position(self, parent_element: ET.Element, child_tag: str) -> Optional[Tuple[int, int]]:
        """
        Find the position of a child element within its parent.
        
        Args:
            parent_element: The parent element
            child_tag: Tag name of the child to find (without namespace)
            
        Returns:
            Tuple of (start_position, end_position) or None if not found
        """
        parent_start, parent_end = self.find_element_position(parent_element)
        parent_section = self.original_content[parent_start:parent_end]
        
        # Remove namespace if present
        simple_tag = child_tag.split('}')[-1] if '}' in child_tag else child_tag
        
        # Search for the child element
        pattern = f'<{simple_tag}[>\\s]'
        match = re.search(pattern, parent_section)
        
        if not match:
            return None
        
        child_start_in_section = match.start()
        
        # Find the end of the child element
        closing_tag = f'</{simple_tag}>'
        end_match = re.search(re.escape(closing_tag), parent_section[child_start_in_section:])
        
        if end_match:
            child_end_in_section = child_start_in_section + end_match.end()
        else:
            # Try self-closing tag
            self_close = re.search(r'/>', parent_section[child_start_in_section:])
            if self_close:
                child_end_in_section = child_start_in_section + self_close.end()
            else:
                return None
        
        # Convert to absolute positions
        abs_start = parent_start + child_start_in_section
        abs_end = parent_start + child_end_in_section
        
        return abs_start, abs_end
    
    def replace_element_content(self, element: ET.Element, child_tag: str, new_content: str):
        """
        Replace the text content of a child element.
        
        Args:
            element: Parent element containing the child
            child_tag: Tag name of the child element to modify
            new_content: New text content (can be None to remove the element)
        """
        child_pos = self.find_child_element_position(element, child_tag)
        
        if new_content is not None and new_content != "":
            # We want to set/update content
            if child_pos:
                # Element exists, update its content
                start_pos, end_pos = child_pos
                
                # Extract just the inner content positions
                parent_section = self.original_content[start_pos:end_pos]
                
                # Find the end of opening tag
                opening_end = parent_section.find('>')
                if opening_end == -1:
                    return
                
                # Find the start of closing tag
                simple_tag = child_tag.split('}')[-1] if '}' in child_tag else child_tag
                closing_start = parent_section.rfind(f'</{simple_tag}>')
                
                if closing_start == -1:
                    # Might be self-closing, skip
                    return
                
                # Calculate absolute positions for content
                content_start = start_pos + opening_end + 1
                content_end = start_pos + closing_start
                
                old_content = self.original_content[content_start:content_end]
                
                # Only add replacement if content changed
                if old_content != new_content:
                    self.replacements.append(XmlReplacement(content_start, content_end, new_content))
            else:
                # Element doesn't exist, need to create it
                parent_start, parent_end = self.find_element_position(element)
                parent_section = self.original_content[parent_start:parent_end]
                
                # Find where to insert (strategy: after first child element or after opening tag)
                # Look for a sibling element to match indentation
                sibling_pattern = r'\n(\s+)<[^/]'
                sibling_match = re.search(sibling_pattern, parent_section)
                
                if sibling_match:
                    indent = sibling_match.group(1)
                    # Insert after the opening tag of parent
                    opening_tag_end = parent_section.find('>')
                    insert_pos = parent_start + opening_tag_end + 1
                else:
                    # Default indentation
                    indent = '    '
                    opening_tag_end = parent_section.find('>')
                    insert_pos = parent_start + opening_tag_end + 1
                
                # Create the new element
                simple_tag = child_tag.split('}')[-1] if '}' in child_tag else child_tag
                new_element = f'\n{indent}<{simple_tag}>{new_content}</{simple_tag}>'
                
                self.replacements.append(XmlReplacement(insert_pos, insert_pos, new_element))
        
        elif child_pos:
            # Content is empty and element exists - remove it
            start_pos, end_pos = child_pos
            
            # Try to include preceding whitespace/newline
            check_start = max(0, start_pos - 10)
            before_section = self.original_content[check_start:start_pos]
            newline_pos = before_section.rfind('\n')
            
            if newline_pos != -1:
                actual_start = check_start + newline_pos
            else:
                actual_start = start_pos
            
            self.replacements.append(XmlReplacement(actual_start, end_pos, ''))
    
    def modify_elements(self, xpath: str, modifier_func: Callable[[ET.Element], dict]):
        """
        Modify multiple elements matching an XPath expression.
        
        Args:
            xpath: XPath expression to find elements
            modifier_func: Function that takes an element and returns a dict of
                          {child_tag: new_content} to modify
        """
        elements = self.root.findall(xpath)
        
        for element in elements:
            modifications = modifier_func(element)
            
            for child_tag, new_content in modifications.items():
                self.replace_element_content(element, child_tag, new_content)
    
    def apply(self) -> bool:
        """
        Apply all pending replacements to the file.
        
        Returns:
            True if file was modified, False if no changes were needed
        """
        if not self.replacements:
            return False
        
        # Sort replacements in reverse order to maintain positions
        self.replacements.sort(reverse=True, key=lambda r: r.start_pos)
        
        modified_content = self.original_content
        
        for replacement in self.replacements:
            modified_content = (
                modified_content[:replacement.start_pos] + 
                replacement.new_content + 
                modified_content[replacement.end_pos:]
            )
        
        # Write back only if changed
        if modified_content != self.original_content:
            with open(self.filepath, 'w', encoding=self.encoding, newline='') as f:
                f.write(modified_content)
            return True
        
        return False
    
    def get_preview(self) -> str:
        """
        Get a preview of what the file would look like after modifications.
        
        Returns:
            The modified content as a string
        """
        if not self.replacements:
            return self.original_content
        
        # Sort replacements in reverse order
        sorted_replacements = sorted(self.replacements, reverse=True, key=lambda r: r.start_pos)
        
        modified_content = self.original_content
        
        for replacement in sorted_replacements:
            modified_content = (
                modified_content[:replacement.start_pos] + 
                replacement.new_content + 
                modified_content[replacement.end_pos:]
            )
        
        return modified_content
