import streamlit as st
import xml.etree.ElementTree as ET
from pathlib import Path
import os
import sys
import re
from typing import Dict, List, Optional

# Add parent directory to path to import shared modules
sys.path.insert(0, str(Path(__file__).parent.parent))

from shared.google_api import translate_text as translate_text_google, list_languages

# Constants
LOCALE_DIR = Path(__file__).parent.parent.parent / "web" / "src" / "locale"
SOURCE_FILE = "messages.xlf"
XLIFF_NS = "urn:oasis:names:tc:xliff:document:1.2"


def translate_text_openai(
    text: str | list[str],
    target_language: str,
    source_language: str | None = None,
) -> list[dict]:
    """Translates text using OpenAI's ChatGPT API.
    
    Args:
        text: The text to translate. Can be a string or a list of strings.
        target_language: The ISO 639 language code to translate the text into
                         (e.g., 'en' for English, 'es' for Spanish).
        source_language: Optional. The ISO 639 language code of the input text.
                         If None, the model will auto-detect.
    
    Returns:
        A list of dictionaries containing the translation results.
        Each dict has keys: 'input', 'translatedText'
    """
    try:
        from openai import OpenAI
    except ImportError:
        raise ImportError(
            "OpenAI package not installed. Install it with: pip install openai"
        )
    
    # Load .env file if it exists
    try:
        from dotenv import load_dotenv
        # Look for .env in the locale-manager.py directory
        env_path = Path(__file__).parent / ".env"
        if env_path.exists():
            load_dotenv(env_path)
    except ImportError:
        pass  # python-dotenv not installed, skip
    
    # Initialize OpenAI client
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        raise ValueError(
            "OPENAI_API_KEY environment variable not set. "
            "Please set it in a .env file or as an environment variable."
        )
    
    client = OpenAI(api_key=api_key)
    
    # Convert single string to list
    if isinstance(text, str):
        text = [text]
    
    results = []
    
    for input_text in text:
        # Construct prompt
        if source_language:
            prompt = (
                f"Translate the following text from {source_language} to {target_language}. "
                f"Only provide the translation, nothing else.\n\n{input_text}"
            )
        else:
            prompt = (
                f"Translate the following text to {target_language}. "
                f"Only provide the translation, nothing else.\n\n{input_text}"
            )
        
        # Call OpenAI API
        response = client.chat.completions.create(
            model="gpt-4o-mini",  # Using the smaller, faster model
            messages=[
                {
                    "role": "system",
                    "content": "You are a professional translator. Provide only the translation without any explanations or additional text."
                },
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            temperature=0.3,  # Lower temperature for more consistent translations
        )
        
        translated_text = response.choices[0].message.content.strip()
        
        results.append({
            'input': input_text,
            'translatedText': translated_text
        })
    
    return results


# Translation service options
TRANSLATORS = {
    "Google Translate": "google",
    "OpenAI (ChatGPT)": "openai"
}

class TranslationUnit:
    """Represents a single translation unit"""
    def __init__(self, unit_id: str, source: str, target: str = "", contexts: List[Dict] = None):
        self.id = unit_id
        self.source = source
        self.target = target
        self.contexts = contexts or []
    
    def __repr__(self):
        return f"TranslationUnit(id={self.id}, source={self.source[:30]}..., target={self.target[:30]}...)"

class LocaleManager:
    """Manages Angular locale (XLIFF) files"""
    
    def __init__(self, locale_dir: Path = LOCALE_DIR):
        self.locale_dir = locale_dir
        self.source_file = locale_dir / SOURCE_FILE
        
    def get_translation_files(self) -> List[str]:
        """Get list of all translation files (excluding messages.xlf)"""
        if not self.locale_dir.exists():
            return []
        
        files = [f.name for f in self.locale_dir.glob("messages.*.xlf")]
        return sorted(files)
    
    def extract_language_code(self, filename: str) -> str:
        """Extract language code from filename (e.g., 'messages.zh-CN.xlf' -> 'zh-CN')"""
        match = re.search(r'messages\.(.+)\.xlf', filename)
        return match.group(1) if match else ""
    
    def parse_xliff(self, filepath: Path) -> tuple[str, str, List[TranslationUnit]]:
        """Parse XLIFF file and extract translation units
        
        CRITICAL: This method extracts target content directly from the raw file
        to preserve exact formatting. This prevents false-positive change detection
        that would occur if we used ElementTree's serialization, which reformats
        XML (e.g., changing "/> to " />" or &quot; to ").
        """
        if not filepath.exists():
            return "", "", []
        
        tree = ET.parse(filepath)
        root = tree.getroot()
        
        # Register namespace
        ET.register_namespace('', XLIFF_NS)
        
        # Also read the raw file content to extract target text exactly as written
        with open(filepath, 'r', encoding='UTF-8') as f:
            raw_content = f.read()
        
        # Get source and target languages
        file_elem = root.find('.//{%s}file' % XLIFF_NS)
        source_lang = file_elem.get('source-language', '') if file_elem is not None else ''
        target_lang = file_elem.get('target-language', '') if file_elem is not None else ''
        
        # Extract translation units
        units = []
        for trans_unit in root.findall('.//{%s}trans-unit' % XLIFF_NS):
            unit_id = trans_unit.get('id', '')
            
            source_elem = trans_unit.find('{%s}source' % XLIFF_NS)
            source_text = self._extract_text_with_placeholders(source_elem) if source_elem is not None else ''
            
            # Extract target text directly from raw content to preserve exact formatting
            target_text = self._extract_target_from_raw(raw_content, unit_id)
            
            # Extract context information
            contexts = []
            for context_group in trans_unit.findall('.//{%s}context-group' % XLIFF_NS):
                context_info = {}
                for context in context_group.findall('{%s}context' % XLIFF_NS):
                    context_type = context.get('context-type', '')
                    context_info[context_type] = context.text or ''
                contexts.append(context_info)
            
            units.append(TranslationUnit(unit_id, source_text, target_text, contexts))
        
        return source_lang, target_lang, units
    
    def _extract_target_from_raw(self, raw_content: str, unit_id: str) -> str:
        """Extract target content directly from raw file content to preserve exact formatting"""
        # Find the trans-unit with this ID
        trans_unit_pattern = f'<trans-unit id="{re.escape(unit_id)}"'
        trans_unit_start = raw_content.find(trans_unit_pattern)
        
        if trans_unit_start == -1:
            return ""
        
        # Find the end of this trans-unit
        trans_unit_end = raw_content.find('</trans-unit>', trans_unit_start)
        if trans_unit_end == -1:
            return ""
        
        # Extract the trans-unit section
        trans_unit_section = raw_content[trans_unit_start:trans_unit_end]
        
        # Find target element within this section
        target_start = trans_unit_section.find('<target>')
        if target_start == -1:
            return ""
        
        target_end = trans_unit_section.find('</target>', target_start)
        if target_end == -1:
            return ""
        
        # Extract just the content between <target> and </target>
        target_content = trans_unit_section[target_start + len('<target>'):target_end]
        
        return target_content
    
    def _extract_text_with_placeholders(self, element) -> str:
        """Extract text content including placeholders like <x id="..."/>
        
        This extracts the raw XML content exactly as it appears in the file,
        without any reformatting or re-serialization.
        """
        if element is None:
            return ""
        
        # Serialize the inner XML content exactly as is
        result = element.text or ""
        
        for child in element:
            # Use xml method to preserve structure without escaping
            child_str = ET.tostring(child, encoding='unicode', method='xml')
            # Remove namespace declaration that gets added
            child_str = child_str.replace(f' xmlns="{XLIFF_NS}"', '')
            result += child_str
            # Add tail text if present (text after the child element)
            if child.tail:
                result += child.tail
        
        return result
    
    def save_xliff(self, filepath: Path, source_lang: str, target_lang: str, units: List[TranslationUnit]):
        """Save translation units to XLIFF file, preserving original formatting"""
        # Parse the file to get element positions
        tree = ET.parse(filepath)
        root = tree.getroot()
        
        # Read the original file content as bytes to preserve exact formatting
        with open(filepath, 'rb') as f:
            original_bytes = f.read()
        
        original_content = original_bytes.decode('UTF-8')
        
        # Track all replacements we need to make (position, old_text, new_text)
        replacements = []
        
        # Process each trans-unit element
        for trans_unit_elem in root.findall('.//{%s}trans-unit' % XLIFF_NS):
            unit_id = trans_unit_elem.get('id', '')
            
            # Find the corresponding unit in our data
            matching_unit = None
            for unit in units:
                if unit.id == unit_id:
                    matching_unit = unit
                    break
            
            if not matching_unit:
                continue
            
            # Find target element
            target_elem = trans_unit_elem.find('{%s}target' % XLIFF_NS)
            source_elem = trans_unit_elem.find('{%s}source' % XLIFF_NS)
            
            # Get the position of this trans-unit in the file
            # We'll search for the unique ID in the file
            trans_unit_pattern = f'<trans-unit id="{re.escape(unit_id)}"'
            trans_unit_start = original_content.find(trans_unit_pattern)
            
            if trans_unit_start == -1:
                continue
            
            # Find the end of this trans-unit
            trans_unit_end_tag = '</trans-unit>'
            trans_unit_end = original_content.find(trans_unit_end_tag, trans_unit_start)
            trans_unit_end += len(trans_unit_end_tag)
            
            if trans_unit_end == -1:
                continue
            
            # Extract the trans-unit section
            trans_unit_section = original_content[trans_unit_start:trans_unit_end]
            
            # Find target element within this section
            target_start_in_section = trans_unit_section.find('<target>')
            
            if matching_unit.target:
                # We have content to save
                new_target_content = self._serialize_target_content(matching_unit.target)
                
                if target_start_in_section != -1:
                    # Target exists, find its end
                    target_end_in_section = trans_unit_section.find('</target>', target_start_in_section)
                    
                    if target_end_in_section != -1:
                        # Calculate absolute positions
                        abs_target_start = trans_unit_start + target_start_in_section + len('<target>')
                        abs_target_end = trans_unit_start + target_end_in_section
                        
                        # Old target content (exactly as it appears in the file)
                        old_target_content = original_content[abs_target_start:abs_target_end]
                        
                        # Only replace if content actually changed
                        # Direct string comparison - don't reformat if content hasn't changed
                        if old_target_content != new_target_content:
                            replacements.append((abs_target_start, abs_target_end, new_target_content))
                else:
                    # Target doesn't exist, need to create it
                    # Find where to insert (after </source>)
                    source_end_in_section = trans_unit_section.find('</source>')
                    
                    if source_end_in_section != -1:
                        source_end_in_section += len('</source>')
                        abs_insert_pos = trans_unit_start + source_end_in_section
                        
                        # Determine indentation by looking at the source line
                        # Find the newline before <source>
                        source_start_in_section = trans_unit_section.find('<source>')
                        newline_before_source = trans_unit_section.rfind('\n', 0, source_start_in_section)
                        
                        if newline_before_source != -1:
                            # Extract indentation
                            indent = trans_unit_section[newline_before_source + 1:source_start_in_section]
                        else:
                            indent = '        '  # Default indentation
                        
                        # Create new target element
                        new_target_element = f'\n{indent}<target>{new_target_content}</target>'
                        
                        # Insert at position
                        replacements.append((abs_insert_pos, abs_insert_pos, new_target_element))
            
            elif target_start_in_section != -1:
                # Target exists but should be removed (empty content)
                # Find the complete target element including surrounding whitespace
                target_end_in_section = trans_unit_section.find('</target>', target_start_in_section)
                target_end_in_section += len('</target>')
                
                # Look for newline before target
                newline_before_target = trans_unit_section.rfind('\n', 0, target_start_in_section)
                
                if newline_before_target != -1:
                    # Include the newline in removal
                    actual_start = newline_before_target
                else:
                    actual_start = target_start_in_section
                
                abs_remove_start = trans_unit_start + actual_start
                abs_remove_end = trans_unit_start + target_end_in_section
                
                replacements.append((abs_remove_start, abs_remove_end, ''))
        
        # Apply replacements in reverse order (from end to start) to maintain positions
        replacements.sort(reverse=True, key=lambda x: x[0])
        
        modified_content = original_content
        for start_pos, end_pos, new_content in replacements:
            modified_content = modified_content[:start_pos] + new_content + modified_content[end_pos:]
        
        # Write back only if there were changes
        if modified_content != original_content:
            with open(filepath, 'w', encoding='UTF-8', newline='') as f:
                f.write(modified_content)
    
    def _normalize_xml_content(self, text: str) -> str:
        """Normalize XML content for comparison by parsing and re-serializing
        
        This ensures that equivalent XML is treated as equal even if formatting differs.
        For example: <x id="foo"/> and <x id="foo"></x> are equivalent.
        """
        if not text or not text.strip():
            return text.strip() if text else ""
        
        # Check if text contains XML tags
        has_xml_tags = '<' in text
        
        if not has_xml_tags:
            # Plain text, return as-is (but stripped for consistent comparison)
            return text
        
        try:
            # Wrap in a temporary element and parse
            # This normalizes the XML structure
            wrapped = f'<root xmlns="{XLIFF_NS}">{text}</root>'
            temp_elem = ET.fromstring(wrapped)
            
            # Re-serialize the content in a consistent way
            result = temp_elem.text or ""
            for child in temp_elem:
                child_str = ET.tostring(child, encoding='unicode', method='xml')
                # Remove namespace declarations that get auto-added
                child_str = child_str.replace(f' xmlns="{XLIFF_NS}"', '')
                result += child_str
                if child.tail:
                    result += child.tail
            
            return result
        except ET.ParseError:
            # If parsing fails, return original text
            # This handles plain text that might have < characters but isn't valid XML
            return text
    
    def _serialize_target_content(self, text: str) -> str:
        """Serialize target content, preserving XML structure"""
        if not text:
            return ""
        
        # If text contains <x tags (not escaped), it's already in proper XML form
        if '<x ' in text or '<X ' in text:
            return text
        
        # If text contains escaped tags, unescape them
        if '&lt;x ' in text or '&lt;X ' in text:
            import html
            return html.unescape(text)
        
        # Plain text - return as is
        return text
    
    def _set_element_with_placeholders(self, element, text: str):
        """Set element text content, parsing any HTML-like placeholders"""
        if not text:
            element.text = None
            element.tail = element.tail  # Preserve tail
            # Clear all children
            for child in list(element):
                element.remove(child)
            return
        
        # Check if text contains actual XML tags (not escaped)
        has_xml_tags = '<x ' in text or '<X ' in text
        # Check if text is already escaped
        has_escaped_tags = '&lt;x ' in text or '&lt;X ' in text
        
        if has_xml_tags and not has_escaped_tags:
            # Text contains raw XML tags, parse them
            # Clear element children but preserve tail
            tail = element.tail
            element.clear()
            element.tail = tail
            
            # Parse and reconstruct
            try:
                # Wrap in temporary root for parsing
                wrapped = f'<root xmlns="{XLIFF_NS}">{text}</root>'
                temp_elem = ET.fromstring(wrapped)
                
                # Copy text and children
                element.text = temp_elem.text
                for child in temp_elem:
                    element.append(child)
            except ET.ParseError:
                # If parsing fails, just set as text
                element.text = text
        elif has_escaped_tags:
            # Text is already escaped (like from the original file read as string)
            # We need to unescape and parse it
            import html
            unescaped = html.unescape(text)
            
            # Clear element children but preserve tail
            tail = element.tail
            element.clear()
            element.tail = tail
            
            try:
                wrapped = f'<root xmlns="{XLIFF_NS}">{unescaped}</root>'
                temp_elem = ET.fromstring(wrapped)
                
                element.text = temp_elem.text
                for child in temp_elem:
                    element.append(child)
            except ET.ParseError:
                # If parsing still fails, set as escaped text
                element.text = text
        else:
            # Plain text, no special handling needed
            # Clear children but preserve tail
            tail = element.tail
            for child in list(element):
                element.remove(child)
            element.text = text
            element.tail = tail
    
    def create_new_locale(self, language_code: str, language_name: str) -> Path:
        """Create a new locale file based on the source file"""
        new_filename = f"messages.{language_code}.xlf"
        new_filepath = self.locale_dir / new_filename
        
        if new_filepath.exists():
            raise FileExistsError(f"Locale file {new_filename} already exists")
        
        # Parse source file to get structure
        tree = ET.parse(self.source_file)
        root = tree.getroot()
        
        # Register namespace
        ET.register_namespace('', XLIFF_NS)
        
        # Find the file element and update target-language
        file_elem = root.find('.//{%s}file' % XLIFF_NS)
        if file_elem is not None:
            file_elem.set('target-language', language_code)
        
        # Remove all <target> elements from trans-units (keep only source)
        for trans_unit in root.findall('.//{%s}trans-unit' % XLIFF_NS):
            target_elem = trans_unit.find('{%s}target' % XLIFF_NS)
            if target_elem is not None:
                trans_unit.remove(target_elem)
        
        # Write the new file
        tree.write(new_filepath, encoding='UTF-8', xml_declaration=True)
        
        return new_filepath
    
    def bulk_translate(self, filepath: Path, source_lang: str, target_lang: str, 
                      units: List[TranslationUnit], translator: str = 'google', progress_callback=None) -> int:
        """Translate all untranslated units in bulk
        
        Args:
            filepath: Path to the XLIFF file to update
            source_lang: Source language code
            target_lang: Target language code
            units: List of TranslationUnit objects to process
            translator: Translation service to use ('google' or 'openai')
            progress_callback: Optional callback function(current, total, message) for progress updates
        
        Returns:
            Number of units successfully translated
        """
        import re
        
        # Filter to only untranslated units
        untranslated_units = [u for u in units if not u.target.strip()]
        
        if not untranslated_units:
            return 0
        
        total_to_translate = len(untranslated_units)
        translated_count = 0
        failed_count = 0
        
        # Extract base language codes (e.g., 'zh-CN' -> 'zh', 'en-US' -> 'en')
        source_base = source_lang.split('-')[0] if source_lang else None
        target_base = target_lang.split('-')[0] if target_lang else None
        
        # Translate in batches to avoid API limits
        batch_size = 10
        
        for i in range(0, len(untranslated_units), batch_size):
            batch = untranslated_units[i:i + batch_size]
            
            # Prepare texts for translation (remove XML tags)
            texts_to_translate = []
            for unit in batch:
                plain_text = re.sub(r'<[^>]+>', '', unit.source)
                texts_to_translate.append(plain_text)
            
            try:
                # Call translate API with batch
                if translator == 'openai':
                    results = translate_text_openai(
                        text=texts_to_translate,
                        target_language=target_base,
                        source_language=source_base
                    )
                else:  # google
                    results = translate_text_google(
                        text=texts_to_translate,
                        target_language=target_base,
                        source_language=source_base
                    )
                
                # Update units with translations
                for j, result in enumerate(results):
                    if 'translatedText' in result:
                        batch[j].target = result['translatedText']
                        translated_count += 1
                    else:
                        failed_count += 1
                
                # Report progress
                if progress_callback:
                    progress_callback(
                        i + len(batch),
                        total_to_translate,
                        f"Translated {translated_count}/{total_to_translate} units..."
                    )
                
            except Exception as e:
                # If batch translation fails, try individual translations
                for j, unit in enumerate(batch):
                    try:
                        plain_text = re.sub(r'<[^>]+>', '', unit.source)
                        if translator == 'openai':
                            result = translate_text_openai(
                                text=plain_text,
                                target_language=target_base,
                                source_language=source_base
                            )
                        else:  # google
                            result = translate_text_google(
                                text=plain_text,
                                target_language=target_base,
                                source_language=source_base
                            )
                        
                        if result and len(result) > 0 and 'translatedText' in result[0]:
                            unit.target = result[0]['translatedText']
                            translated_count += 1
                        else:
                            failed_count += 1
                    except Exception as inner_e:
                        failed_count += 1
                        if progress_callback:
                            progress_callback(
                                i + j + 1,
                                total_to_translate,
                                f"Failed to translate unit {unit.id}: {str(inner_e)}"
                            )
                
                if progress_callback:
                    progress_callback(
                        i + len(batch),
                        total_to_translate,
                        f"Batch translation failed, processed individually. Translated {translated_count}/{total_to_translate}"
                    )
        
        # Save all changes at once
        if translated_count > 0:
            self.save_xliff(filepath, source_lang, target_lang, units)
        
        return translated_count

def main():
    st.set_page_config(page_title="Angular Locale Manager", layout="wide")
    
    st.title("🌍 Angular Locale Manager")
    st.markdown("Manage translations for your Angular application")
    
    manager = LocaleManager()
    
    # Sidebar for file selection/creation
    with st.sidebar:
        st.header("Locale Files")
        
        # Get available translation files
        translation_files = manager.get_translation_files()
        
        # Create new locale section
        with st.expander("➕ Create New Locale", expanded=False):
            new_lang_code = st.text_input("Language Code (e.g., fr-FR, es-ES)", key="new_lang_code")
            new_lang_name = st.text_input("Language Name (e.g., French, Spanish)", key="new_lang_name")
            
            if st.button("Create Locale"):
                if new_lang_code and new_lang_name:
                    try:
                        new_file = manager.create_new_locale(new_lang_code, new_lang_name)
                        st.success(f"Created {new_file.name}")
                        st.rerun()
                    except FileExistsError as e:
                        st.error(str(e))
                    except Exception as e:
                        st.error(f"Error creating locale: {str(e)}")
                else:
                    st.warning("Please enter both language code and name")
        
        # Select existing locale
        st.subheader("Select Locale")
        if translation_files:
            selected_file = st.selectbox(
                "Translation File",
                translation_files,
                format_func=lambda x: f"{manager.extract_language_code(x)} ({x})"
            )
        else:
            st.info("No translation files found. Create one above!")
            selected_file = None
        
        # Filter options
        st.subheader("Filters")
        show_translated = st.checkbox("Show translated", value=True)
        show_untranslated = st.checkbox("Show untranslated", value=True)
        search_term = st.text_input("Search (ID or text)", "")
        
        # Translator selection
        st.subheader("Translator")
        selected_translator = st.selectbox(
            "Translation Service",
            options=list(TRANSLATORS.keys()),
            index=0,
            key="translator_selector"
        )
        # Store translator code in session state
        st.session_state.translator = TRANSLATORS[selected_translator]
    
    # Main content area
    if selected_file:
        filepath = manager.locale_dir / selected_file
        source_lang, target_lang, units = manager.parse_xliff(filepath)
        
        # Header with file info
        col1, col2, col3 = st.columns(3)
        with col1:
            st.metric("Locale File", selected_file)
        with col2:
            st.metric("Source Language", source_lang)
        with col3:
            st.metric("Target Language", target_lang)
        
        # Statistics
        total_units = len(units)
        translated_units = sum(1 for u in units if u.target.strip())
        untranslated_units = total_units - translated_units
        
        col1, col2, col3 = st.columns(3)
        with col1:
            st.metric("Total Translations", total_units)
        with col2:
            st.metric("Translated", translated_units)
        with col3:
            st.metric("Untranslated", untranslated_units)
        
        st.progress(translated_units / total_units if total_units > 0 else 0)
        
        # Bulk translate button
        if untranslated_units > 0:
            st.divider()
            col1, col2 = st.columns([3, 1])
            with col1:
                st.info(f"💡 {untranslated_units} untranslated items can be auto-translated")
            with col2:
                if st.button("🌐 Bulk Translate All", type="primary", use_container_width=True):
                    progress_bar = st.progress(0)
                    status_text = st.empty()
                    
                    def progress_callback(current, total, message):
                        progress_bar.progress(current / total)
                        status_text.text(message)
                    
                    try:
                        status_text.text("Starting bulk translation...")
                        count = manager.bulk_translate(
                            filepath, 
                            source_lang, 
                            target_lang, 
                            units,
                            st.session_state.translator,
                            progress_callback
                        )
                        
                        progress_bar.progress(1.0)
                        status_text.empty()
                        progress_bar.empty()
                        
                        if count > 0:
                            st.success(f"✅ Successfully translated {count} items!")
                            st.rerun()
                        else:
                            st.warning("No items were translated")
                    except Exception as e:
                        status_text.empty()
                        progress_bar.empty()
                        st.error(f"Bulk translation failed: {str(e)}")
        
        st.divider()
        
        # Filter units
        filtered_units = []
        for unit in units:
            # Apply translated/untranslated filter
            is_translated = bool(unit.target.strip())
            if is_translated and not show_translated:
                continue
            if not is_translated and not show_untranslated:
                continue
            
            # Apply search filter
            if search_term:
                search_lower = search_term.lower()
                if not (search_lower in unit.id.lower() or 
                       search_lower in unit.source.lower() or 
                       search_lower in unit.target.lower()):
                    continue
            
            filtered_units.append(unit)
        
        st.subheader(f"Translations ({len(filtered_units)} items)")
        
        # Initialize session state for tracking last saved state
        if 'last_saved_state' not in st.session_state:
            st.session_state.last_saved_state = {}
        
        # Initialize default translator if not set
        if 'translator' not in st.session_state:
            st.session_state.translator = 'google'
        
        # Display translation units
        for idx, unit in enumerate(filtered_units):
            with st.container():
                col1, col2 = st.columns([1, 1])
                
                with col1:
                    st.markdown(f"**ID:** `{unit.id}`")
                    st.markdown(f"**Source ({source_lang}):**")
                    st.text_area(
                        f"Source {idx}",
                        value=unit.source,
                        height=80,
                        disabled=True,
                        key=f"source_{unit.id}_{idx}",
                        label_visibility="collapsed"
                    )
                    
                    # Show context info
                    if unit.contexts:
                        with st.expander("📍 Context Info"):
                            for ctx in unit.contexts:
                                for key, value in ctx.items():
                                    st.text(f"{key}: {value}")
                
                with col2:
                    st.text("-")
                    st.markdown(f"**Target ({target_lang}):**")
                    
                    new_target = st.text_area(
                        f"Target {idx}",
                        value=unit.target,
                        height=80,
                        key=f"target_{unit.id}_{idx}",
                        label_visibility="collapsed"
                    )
                    
                    # Auto-save on change
                    if new_target != unit.target:
                        # Update the unit
                        unit.target = new_target
                        
                        # Save immediately
                        try:
                            manager.save_xliff(filepath, source_lang, target_lang, units)
                            st.session_state.last_saved_state[unit.id] = new_target
                            # Show subtle success indicator
                            st.caption("✓ Auto-saved")
                        except Exception as e:
                            st.error(f"Auto-save failed: {str(e)}")
                    
                    # Auto-translate button
                    translator_name = [name for name, code in TRANSLATORS.items() if code == st.session_state.translator][0]
                    if st.button(f"🌐 Translate with {translator_name}", key=f"translate_{unit.id}_{idx}"):
                        try:
                            # Extract plain text for translation (remove HTML tags)
                            import re
                            plain_source = re.sub(r'<[^>]+>', '', unit.source)
                            
                            if st.session_state.translator == 'openai':
                                result = translate_text_openai(
                                    text=plain_source,
                                    target_language=target_lang.split('-')[0],  # Use base language code
                                    source_language=source_lang.split('-')[0]
                                )
                            else:  # google
                                result = translate_text_google(
                                    text=plain_source,
                                    target_language=target_lang.split('-')[0],  # Use base language code
                                    source_language=source_lang.split('-')[0]
                                )
                            
                            if result and len(result) > 0:
                                translated = result[0].get('translatedText', '')
                                # Update unit and save
                                unit.target = translated
                                manager.save_xliff(filepath, source_lang, target_lang, units)
                                st.success("Translated and saved!")
                                st.rerun()
                        except Exception as e:
                            st.error(f"Translation failed: {str(e)}")
                
                st.divider()
    else:
        st.info("👈 Select or create a locale file from the sidebar to get started")

if __name__ == "__main__":
    main()

