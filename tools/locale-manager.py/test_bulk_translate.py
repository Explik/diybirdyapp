"""
Unit tests for bulk translate functionality in locale_manager.py
"""

import sys
import unittest
from pathlib import Path
from unittest.mock import Mock, patch, MagicMock
import tempfile
import shutil

# Add tools directory to path
tools_dir = Path(__file__).parent
sys.path.insert(0, str(tools_dir))

from app import LocaleManager, TranslationUnit


class TestBulkTranslate(unittest.TestCase):
    """Test cases for bulk translation functionality"""
    
    def setUp(self):
        """Set up test fixtures"""
        # Create a temporary directory for test files
        self.test_dir = Path(tempfile.mkdtemp())
        self.locale_dir = self.test_dir / "locale"
        self.locale_dir.mkdir()
        
        # Create a sample XLIFF file
        self.sample_xliff = """<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en-US" target-language="es-ES" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="unit1" datatype="html">
        <source>Hello World</source>
        <target></target>
      </trans-unit>
      <trans-unit id="unit2" datatype="html">
        <source>Welcome to our app</source>
        <target></target>
      </trans-unit>
      <trans-unit id="unit3" datatype="html">
        <source>Already translated</source>
        <target>Ya traducido</target>
      </trans-unit>
      <trans-unit id="unit4" datatype="html">
        <source>Click <x id="INTERPOLATION"/> to continue</source>
        <target></target>
      </trans-unit>
    </body>
  </file>
</xliff>"""
        
        self.test_file = self.locale_dir / "messages.es-ES.xlf"
        with open(self.test_file, 'w', encoding='UTF-8') as f:
            f.write(self.sample_xliff)
        
        # Initialize LocaleManager with test directory
        self.manager = LocaleManager(self.locale_dir)
    
    def tearDown(self):
        """Clean up test fixtures"""
        # Remove temporary directory
        shutil.rmtree(self.test_dir)
    
    def test_bulk_translate_no_untranslated(self):
        """Test bulk translate when all units are already translated"""
        # Create units where all have translations
        units = [
            TranslationUnit("id1", "Source 1", "Target 1"),
            TranslationUnit("id2", "Source 2", "Target 2"),
            TranslationUnit("id3", "Source 3", "Target 3"),
        ]
        
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "es-ES",
            units
        )
        
        self.assertEqual(count, 0, "Should return 0 when no units need translation")
    
    @patch('app.translate_text')
    def test_bulk_translate_all_untranslated(self, mock_translate):
        """Test bulk translate when all units need translation"""
        # Mock the translation API
        mock_translate.return_value = [
            {'translatedText': 'Hola Mundo', 'input': 'Hello World'},
            {'translatedText': 'Bienvenido a nuestra aplicación', 'input': 'Welcome to our app'},
        ]
        
        units = [
            TranslationUnit("id1", "Hello World", ""),
            TranslationUnit("id2", "Welcome to our app", ""),
        ]
        
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "es-ES",
            units
        )
        
        # Verify translation was called
        mock_translate.assert_called_once()
        
        # Verify all units were translated
        self.assertEqual(count, 2, "Should translate 2 units")
        self.assertEqual(units[0].target, "Hola Mundo")
        self.assertEqual(units[1].target, "Bienvenido a nuestra aplicación")
    
    @patch('app.translate_text')
    def test_bulk_translate_mixed(self, mock_translate):
        """Test bulk translate with mix of translated and untranslated units"""
        # Mock the translation API
        mock_translate.return_value = [
            {'translatedText': 'Hola Mundo', 'input': 'Hello World'},
        ]
        
        units = [
            TranslationUnit("id1", "Hello World", ""),  # Untranslated
            TranslationUnit("id2", "Already done", "Ya hecho"),  # Already translated
            TranslationUnit("id3", "Goodbye", ""),  # Untranslated
        ]
        
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "es-ES",
            units
        )
        
        # Should only translate the 2 untranslated units
        self.assertGreaterEqual(count, 1, "Should translate at least 1 unit")
        # Second unit should remain unchanged
        self.assertEqual(units[1].target, "Ya hecho", "Should not modify already translated units")
    
    @patch('app.translate_text')
    def test_bulk_translate_with_placeholders(self, mock_translate):
        """Test bulk translate strips XML placeholders before translation"""
        # Mock the translation API
        mock_translate.return_value = [
            {'translatedText': 'Haz clic para continuar', 'input': 'Click  to continue'},
        ]
        
        units = [
            TranslationUnit("id1", 'Click <x id="INTERPOLATION"/> to continue', ""),
        ]
        
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "es-ES",
            units
        )
        
        # Verify translation was called with cleaned text
        mock_translate.assert_called_once()
        call_args = mock_translate.call_args
        
        # The text passed should have XML tags removed
        text_arg = call_args[1]['text'][0] if 'text' in call_args[1] else call_args[0][0]
        self.assertNotIn('<x id=', text_arg, "Should strip XML placeholders from source text")
        self.assertIn('to continue', text_arg, "Should preserve actual text content")
        
        self.assertEqual(count, 1, "Should translate 1 unit")
    
    @patch('app.translate_text')
    def test_bulk_translate_progress_callback(self, mock_translate):
        """Test that progress callback is called during translation"""
        # Mock the translation API
        mock_translate.return_value = [
            {'translatedText': 'Traducido', 'input': 'Test'},
        ]
        
        units = [
            TranslationUnit("id1", "Test 1", ""),
            TranslationUnit("id2", "Test 2", ""),
        ]
        
        # Create a mock callback
        callback = Mock()
        
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "es-ES",
            units,
            progress_callback=callback
        )
        
        # Verify callback was called
        self.assertTrue(callback.called, "Progress callback should be called")
        
        # Verify callback was called with correct arguments
        # callback(current, total, message)
        call_args = callback.call_args_list[0][0]
        self.assertIsInstance(call_args[0], int, "First arg should be current count")
        self.assertIsInstance(call_args[1], int, "Second arg should be total count")
        self.assertIsInstance(call_args[2], str, "Third arg should be message")
    
    @patch('app.translate_text')
    def test_bulk_translate_api_error_fallback(self, mock_translate):
        """Test that individual translation is attempted when batch fails"""
        # First call (batch) raises exception, subsequent calls succeed
        mock_translate.side_effect = [
            Exception("API Error"),  # Batch fails
            [{'translatedText': 'Uno', 'input': 'One'}],  # Individual 1 succeeds
            [{'translatedText': 'Dos', 'input': 'Two'}],  # Individual 2 succeeds
        ]
        
        units = [
            TranslationUnit("id1", "One", ""),
            TranslationUnit("id2", "Two", ""),
        ]
        
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "es-ES",
            units
        )
        
        # Should still translate units individually after batch failure
        self.assertEqual(count, 2, "Should fall back to individual translation")
        self.assertEqual(units[0].target, "Uno")
        self.assertEqual(units[1].target, "Dos")
    
    @patch('app.translate_text')
    def test_bulk_translate_saves_to_file(self, mock_translate):
        """Test that bulk translate saves results to file"""
        # Mock the translation API
        mock_translate.return_value = [
            {'translatedText': 'Hola', 'input': 'Hello'},
        ]
        
        # Parse the real test file
        source_lang, target_lang, units = self.manager.parse_xliff(self.test_file)
        
        # Run bulk translate
        count = self.manager.bulk_translate(
            self.test_file,
            source_lang,
            target_lang,
            units
        )
        
        # Verify file was modified
        self.assertGreater(count, 0, "Should translate at least one unit")
        
        # Re-parse the file and verify translations were saved
        _, _, updated_units = self.manager.parse_xliff(self.test_file)
        
        # Find a unit that was untranslated and should now be translated
        unit1 = next(u for u in updated_units if u.id == 'unit1')
        self.assertNotEqual(unit1.target, "", "Translation should be saved to file")
    
    @patch('app.translate_text')
    def test_bulk_translate_language_code_extraction(self, mock_translate):
        """Test that language codes are correctly extracted for API calls"""
        mock_translate.return_value = [
            {'translatedText': '你好', 'input': 'Hello'},
        ]
        
        units = [
            TranslationUnit("id1", "Hello", ""),
        ]
        
        # Use language codes with region (e.g., zh-CN, en-US)
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "zh-CN",
            units
        )
        
        # Verify translate was called with base language codes
        mock_translate.assert_called_once()
        call_kwargs = mock_translate.call_args[1]
        
        self.assertEqual(call_kwargs['source_language'], 'en', "Should extract base language code 'en' from 'en-US'")
        self.assertEqual(call_kwargs['target_language'], 'zh', "Should extract base language code 'zh' from 'zh-CN'")
    
    @patch('app.translate_text')
    def test_bulk_translate_large_batch(self, mock_translate):
        """Test bulk translate handles large batches correctly"""
        # Mock the translation API to return results for batches
        def mock_translate_side_effect(*args, **kwargs):
            texts = kwargs.get('text', args[0] if args else [])
            return [{'translatedText': f'Translated: {text}', 'input': text} for text in texts]
        
        mock_translate.side_effect = mock_translate_side_effect
        
        # Create 25 untranslated units (should require 3 batches of 10)
        units = [TranslationUnit(f"id{i}", f"Text {i}", "") for i in range(25)]
        
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "es-ES",
            units
        )
        
        self.assertEqual(count, 25, "Should translate all 25 units")
        
        # Verify all units have translations
        for unit in units:
            self.assertNotEqual(unit.target, "", f"Unit {unit.id} should be translated")
            self.assertIn("Translated:", unit.target, f"Unit {unit.id} should have mocked translation")
    
    def test_bulk_translate_empty_units_list(self):
        """Test bulk translate handles empty units list gracefully"""
        units = []
        
        count = self.manager.bulk_translate(
            self.test_file,
            "en-US",
            "es-ES",
            units
        )
        
        self.assertEqual(count, 0, "Should return 0 for empty units list")


class TestBulkTranslateIntegration(unittest.TestCase):
    """Integration tests for bulk translate with real file operations"""
    
    def setUp(self):
        """Set up test fixtures"""
        # Create a temporary directory for test files
        self.test_dir = Path(tempfile.mkdtemp())
        self.locale_dir = self.test_dir / "locale"
        self.locale_dir.mkdir()
        
        # Create a more complex XLIFF file
        self.complex_xliff = """<?xml version="1.0" encoding="UTF-8" ?>
<xliff version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2">
  <file source-language="en" target-language="fr" datatype="plaintext" original="ng2.template">
    <body>
      <trans-unit id="welcomeMessage" datatype="html">
        <source>Welcome to DIY Birdy!</source>
        <context-group purpose="location">
          <context context-type="sourcefile">app.component.ts</context>
          <context context-type="linenumber">10</context>
        </context-group>
      </trans-unit>
      <trans-unit id="buttonText" datatype="html">
        <source>Click here</source>
      </trans-unit>
      <trans-unit id="interpolationExample" datatype="html">
        <source>You have <x id="INTERPOLATION"/> new messages</source>
      </trans-unit>
    </body>
  </file>
</xliff>"""
        
        self.test_file = self.locale_dir / "messages.fr.xlf"
        with open(self.test_file, 'w', encoding='UTF-8') as f:
            f.write(self.complex_xliff)
        
        self.manager = LocaleManager(self.locale_dir)
    
    def tearDown(self):
        """Clean up test fixtures"""
        shutil.rmtree(self.test_dir)
    
    @patch('app.translate_text')
    def test_integration_full_workflow(self, mock_translate):
        """Test complete workflow: parse, translate, save, re-parse"""
        # Mock translations
        mock_translate.return_value = [
            {'translatedText': 'Bienvenue à DIY Birdy!', 'input': 'Welcome to DIY Birdy!'},
            {'translatedText': 'Cliquez ici', 'input': 'Click here'},
            {'translatedText': 'Vous avez  nouveaux messages', 'input': 'You have  new messages'},
        ]
        
        # Step 1: Parse original file
        source_lang, target_lang, units = self.manager.parse_xliff(self.test_file)
        
        original_count = len(units)
        self.assertEqual(original_count, 3, "Should have 3 units")
        
        # Verify all are untranslated
        untranslated_count = sum(1 for u in units if not u.target.strip())
        self.assertEqual(untranslated_count, 3, "All units should be untranslated initially")
        
        # Step 2: Bulk translate
        translated_count = self.manager.bulk_translate(
            self.test_file,
            source_lang,
            target_lang,
            units
        )
        
        self.assertEqual(translated_count, 3, "Should translate all 3 units")
        
        # Step 3: Re-parse and verify
        _, _, updated_units = self.manager.parse_xliff(self.test_file)
        
        self.assertEqual(len(updated_units), original_count, "Unit count should remain the same")
        
        # Verify all units now have translations
        translated_after = sum(1 for u in updated_units if u.target.strip())
        self.assertEqual(translated_after, 3, "All units should be translated after bulk translate")
        
        # Verify specific translations
        welcome_unit = next(u for u in updated_units if u.id == 'welcomeMessage')
        self.assertEqual(welcome_unit.target, 'Bienvenue à DIY Birdy!')
        
        # Verify context is preserved
        self.assertTrue(len(welcome_unit.contexts) > 0, "Context should be preserved")


def run_tests():
    """Run all tests"""
    # Create test suite
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()
    
    # Add all test cases
    suite.addTests(loader.loadTestsFromTestCase(TestBulkTranslate))
    suite.addTests(loader.loadTestsFromTestCase(TestBulkTranslateIntegration))
    
    # Run tests
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    
    # Return exit code
    return 0 if result.wasSuccessful() else 1


if __name__ == "__main__":
    sys.exit(run_tests())
