# Angular Locale Manager

A Streamlit-based UI tool for managing Angular i18n translations (XLIFF files).

## Features

- 📁 **File Management**: Select existing translation files or create new ones
- 📝 **Translation Editor**: Edit translations with a clean, side-by-side interface
- 🔍 **Filtering**: Filter by translated/untranslated status and search by ID or text
- 🌐 **Auto-translation**: Integrate with Google Translate API for automatic translations
- 💾 **Change Tracking**: Track unsaved changes before committing
- 📊 **Statistics**: View translation progress and completion metrics
- 📍 **Context Info**: View source file and line number context for each translation

## Installation

1. Ensure you have the required dependencies:
```bash
pip install streamlit
pip install google-cloud-translate  # Optional, for auto-translate feature
```

2. If using Google Translate API, set up authentication:
```bash
# Set the environment variable for Google Cloud credentials
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/credentials.json"
```

## Usage

### Running the Application

From the `tools` directory, run:

```bash
streamlit run locale_manager.py
```

Or from the repository root:

```bash
cd tools
streamlit run locale_manager.py
```

The application will open in your default web browser at `http://localhost:8501`.

### Creating a New Locale

1. In the sidebar, expand **"➕ Create New Locale"**
2. Enter the language code (e.g., `fr-FR`, `es-ES`, `de-DE`)
3. Enter the language name (e.g., `French`, `Spanish`, `German`)
4. Click **"Create Locale"**

The tool will create a new translation file with all translation units from `messages.xlf` with empty target fields.

### Editing Translations

1. Select a locale file from the dropdown in the sidebar
2. Use filters to show/hide translated and untranslated items
3. Use the search box to find specific translations by ID or text content
4. Edit translations in the **Target** text areas
5. **Changes are automatically saved** when you modify a translation

**Auto-save:** The tool automatically saves changes to the XLIFF file immediately after you edit any translation. Git is recommended for tracking and reverting changes if needed.

### Using Auto-translate

1. For any translation unit, click the **"🌐 Auto-translate"** button
2. The tool will call Google Translate API to generate a translation
3. The translation is automatically saved to the file
4. Review the auto-generated translation and edit if needed

**Note**: Auto-translate requires Google Cloud credentials to be configured.

### Understanding the Interface

#### Metrics Dashboard
- **Total Translations**: Total number of translation units
- **Translated**: Number of units with translations
- **Untranslated**: Number of units without translations
- **Progress Bar**: Visual representation of completion percentage

#### Translation Units
Each translation unit displays:
- **ID**: Unique identifier for the translation
- **Source**: Original text in the source language (read-only)
- **Target**: Translation text (editable)
- **Context Info**: Source file location and line numbers

## File Structure

The locale manager works with XLIFF files in:
```
web/src/locale/
├── messages.xlf          # Source file (not editable via this tool)
├── messages.zh-CN.xlf   # Chinese translation
├── messages.fr-FR.xlf   # French translation (example)
└── ...
```

## XLIFF Format

The tool handles XLIFF 1.2 format files with:
- Translation units with unique IDs
- Source and target text elements
- Placeholder elements (e.g., `<x id="INTERPOLATION"/>`)
- Context groups with location information
- Proper XML namespacing

## Tips

1. **Use Git**: All changes are auto-saved immediately. Use Git to track changes and revert if needed
2. **Search Feature**: Use search to quickly find specific translations
3. **Filter Strategy**: Start with untranslated items to see what needs work
4. **Context Info**: Check context info to understand where the text appears in the app
5. **Placeholders**: Be careful with placeholder tags like `<x id="..."/>` - they must remain intact
6. **Testing**: After editing, test your Angular app to ensure translations display correctly
7. **Batch Translation**: Use auto-translate for quick first-pass translations, then refine manually

## Troubleshooting

### Auto-translate not working
- Ensure Google Cloud credentials are properly configured
- Check that the Google Cloud Translation API is enabled in your project
- Verify you have the `google-cloud-translate` package installed

### Changes not saving
- The tool uses auto-save, so changes should save immediately
- Check file permissions on the locale directory
- Ensure the XLIFF file is not open in another program
- Check the Streamlit console for error messages
- Look for the "✓ Auto-saved" indicator after making changes

### Placeholders breaking
- The tool attempts to preserve XML placeholders like `<x id="..."/>`
- If translations break, check that all placeholder tags are intact
- Compare with the source text to ensure placeholders match

## Development

The locale manager consists of:

- `LocaleManager` class: Handles XLIFF file parsing and saving
- `TranslationUnit` class: Represents individual translation entries
- Streamlit UI: Provides interactive interface for editing

### Key Functions

- `parse_xliff()`: Parse XLIFF files into TranslationUnit objects
- `save_xliff()`: Write TranslationUnit objects back to XLIFF format using position-based replacement
- `create_new_locale()`: Generate new translation files from source
- `_extract_text_with_placeholders()`: Handle XML elements within translations
- `_serialize_target_content()`: Prepare content for writing back to file

### Testing

The project includes comprehensive unit tests to ensure data integrity:

```bash
cd tools
python test_locale_manager_save.py -v
```

**Test Coverage:**
- Simple text target updates
- XML placeholders (e.g., `<x id="INTERPOLATION" />`)
- Content duplication prevention
- Escaped quotes preservation (e.g., `&quot;`)
- No-change scenarios (file not modified if content identical)
- Adding new target elements
- Removing empty targets
- Multiple unit updates
- Parse and re-save cycles

The save mechanism uses **position-based replacement** to ensure:
- Only `<target>` element content is modified
- All other XML formatting, whitespace, and escaping is preserved byte-for-byte
- Git diffs show only actual translation changes

## Contributing

When contributing to the locale manager:

1. Test with various XLIFF files to ensure compatibility
2. Preserve XML structure and formatting
3. Handle edge cases (empty translations, special characters, etc.)
4. Update this README with new features

## License

Part of the DIY Birdy App project.
