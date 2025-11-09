"""
Unit tests for google_api.py

These tests verify:
1. Google Cloud credentials are properly configured
2. Translation API functionality works correctly
3. Language listing functionality works correctly
4. Error handling for missing credentials

Run these tests to ensure proper setup of the local environment.

Usage:
    python -m pytest test_google_api.py -v
    or
    python test_google_api.py
"""

import sys
import unittest
from pathlib import Path
from unittest.mock import Mock, patch, MagicMock, create_autospec
import os

# Add shared directory to path
shared_dir = Path(__file__).parent
sys.path.insert(0, str(shared_dir))

# Check if google-cloud-translate is installed
try:
    import google.cloud.translate_v2 as translate_v2
    GOOGLE_CLOUD_AVAILABLE = True
except ImportError:
    GOOGLE_CLOUD_AVAILABLE = False
    # Create a mock module for testing
    translate_v2 = None

# Only import if we're going to skip or if package is available
if GOOGLE_CLOUD_AVAILABLE:
    from google_api import translate_text, list_languages


class TestGoogleAPISetup(unittest.TestCase):
    """Test Google Cloud API setup and credentials"""
    
    def test_google_credentials_env_variable(self):
        """Test that GOOGLE_APPLICATION_CREDENTIALS environment variable is set"""
        creds_path = os.getenv('GOOGLE_APPLICATION_CREDENTIALS')
        
        if creds_path is None:
            self.skipTest(
                "GOOGLE_APPLICATION_CREDENTIALS not set. "
                "Set this environment variable to run integration tests. "
                "Example: export GOOGLE_APPLICATION_CREDENTIALS=/path/to/credentials.json"
            )
        
        self.assertIsNotNone(creds_path, "GOOGLE_APPLICATION_CREDENTIALS should be set")
        self.assertTrue(
            Path(creds_path).exists(),
            f"Credentials file does not exist at: {creds_path}"
        )
        print(f"✅ Credentials file found at: {creds_path}")
    
    def test_credentials_file_is_json(self):
        """Test that credentials file is valid JSON"""
        creds_path = os.getenv('GOOGLE_APPLICATION_CREDENTIALS')
        
        if creds_path is None:
            self.skipTest("GOOGLE_APPLICATION_CREDENTIALS not set")
        
        import json
        
        try:
            with open(creds_path, 'r') as f:
                creds_data = json.load(f)
            
            # Verify basic structure of service account credentials
            self.assertIn('type', creds_data, "Credentials should have 'type' field")
            self.assertIn('project_id', creds_data, "Credentials should have 'project_id' field")
            self.assertIn('private_key', creds_data, "Credentials should have 'private_key' field")
            self.assertIn('client_email', creds_data, "Credentials should have 'client_email' field")
            
            print(f"✅ Credentials file is valid JSON")
            print(f"   Project ID: {creds_data.get('project_id', 'N/A')}")
            print(f"   Client Email: {creds_data.get('client_email', 'N/A')}")
            
        except json.JSONDecodeError as e:
            self.fail(f"Credentials file is not valid JSON: {e}")
        except Exception as e:
            self.fail(f"Error reading credentials file: {e}")


class TestTranslateTextFunction(unittest.TestCase):
    """Test translate_text function with mocked Google Cloud API"""
    
    def setUp(self):
        """Skip tests if google-cloud-translate is not installed"""
        if not GOOGLE_CLOUD_AVAILABLE:
            self.skipTest("google-cloud-translate package not installed. Install with: pip install google-cloud-translate")
    
    @patch('google.cloud.translate_v2.Client')
    def test_translate_text_with_string(self, mock_client_class):
        """Test translate_text with a single string"""
        # Mock the translate client
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        
        # Mock the translation response
        mock_client.translate.return_value = [
            {
                'translatedText': 'Hello friends!',
                'detectedSourceLanguage': 'es',
                'input': '¡Hola amigos y amigas!'
            }
        ]
        
        # Call the function
        result = translate_text(
            text="¡Hola amigos y amigas!",
            target_language="en",
            source_language=None
        )
        
        # Verify the client was created
        mock_client_class.assert_called_once()
        
        # Verify translate was called with correct parameters
        mock_client.translate.assert_called_once_with(
            values=['¡Hola amigos y amigas!'],
            target_language='en',
            source_language=None
        )
        
        # Verify the result
        self.assertIsInstance(result, list)
        self.assertEqual(len(result), 1)
        self.assertEqual(result[0]['translatedText'], 'Hello friends!')
        self.assertEqual(result[0]['detectedSourceLanguage'], 'es')
    
    @patch('google.cloud.translate_v2.Client')
    def test_translate_text_with_bytes(self, mock_client_class):
        """Test translate_text with bytes input"""
        # Mock the translate client
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        
        # Mock the translation response
        mock_client.translate.return_value = [
            {
                'translatedText': 'Hello',
                'input': 'Hola'
            }
        ]
        
        # Call the function with bytes
        result = translate_text(
            text=b"Hola",
            target_language="en",
            source_language="es"
        )
        
        # Verify translate was called with decoded string
        mock_client.translate.assert_called_once_with(
            values=['Hola'],  # Should be decoded
            target_language='en',
            source_language='es'
        )
        
        self.assertIsInstance(result, list)
    
    @patch('google.cloud.translate_v2.Client')
    def test_translate_text_with_list(self, mock_client_class):
        """Test translate_text with a list of strings"""
        # Mock the translate client
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        
        # Mock the translation response
        mock_client.translate.return_value = [
            {'translatedText': 'Hello', 'input': 'Hola'},
            {'translatedText': 'Goodbye', 'input': 'Adiós'},
            {'translatedText': 'Thank you', 'input': 'Gracias'}
        ]
        
        # Call the function with list
        texts = ["Hola", "Adiós", "Gracias"]
        result = translate_text(
            text=texts,
            target_language="en",
            source_language="es"
        )
        
        # Verify translate was called with the list
        mock_client.translate.assert_called_once_with(
            values=texts,
            target_language='en',
            source_language='es'
        )
        
        # Verify the result
        self.assertIsInstance(result, list)
        self.assertEqual(len(result), 3)
        self.assertEqual(result[0]['translatedText'], 'Hello')
        self.assertEqual(result[1]['translatedText'], 'Goodbye')
        self.assertEqual(result[2]['translatedText'], 'Thank you')
    
    @patch('google.cloud.translate_v2.Client')
    def test_translate_text_auto_detect_language(self, mock_client_class):
        """Test translate_text with auto-detect source language"""
        # Mock the translate client
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        
        # Mock the translation response with detected language
        mock_client.translate.return_value = [
            {
                'translatedText': 'Hello World',
                'detectedSourceLanguage': 'fr',
                'input': 'Bonjour le monde'
            }
        ]
        
        # Call the function without source_language
        result = translate_text(
            text="Bonjour le monde",
            target_language="en",
            source_language=None  # Auto-detect
        )
        
        # Verify source_language is None in the call
        mock_client.translate.assert_called_once_with(
            values=['Bonjour le monde'],
            target_language='en',
            source_language=None
        )
        
        # Verify detected language is in result
        self.assertEqual(result[0]['detectedSourceLanguage'], 'fr')
    
    @patch('google.cloud.translate_v2.Client')
    def test_translate_text_various_languages(self, mock_client_class):
        """Test translate_text with various language codes"""
        # Mock the translate client
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        
        # Test various language pairs
        test_cases = [
            ("en", "es"),  # English to Spanish
            ("en", "fr"),  # English to French
            ("en", "de"),  # English to German
            ("en", "zh"),  # English to Chinese
            ("en", "ja"),  # English to Japanese
            ("es", "en"),  # Spanish to English
        ]
        
        for source, target in test_cases:
            mock_client.translate.reset_mock()
            mock_client.translate.return_value = [
                {'translatedText': 'Translated', 'input': 'Test'}
            ]
            
            result = translate_text(
                text="Test",
                target_language=target,
                source_language=source
            )
            
            # Verify correct languages were used
            call_kwargs = mock_client.translate.call_args[1]
            self.assertEqual(call_kwargs['target_language'], target)
            self.assertEqual(call_kwargs['source_language'], source)


class TestListLanguagesFunction(unittest.TestCase):
    """Test list_languages function with mocked Google Cloud API"""
    
    def setUp(self):
        """Skip tests if google-cloud-translate is not installed"""
        if not GOOGLE_CLOUD_AVAILABLE:
            self.skipTest("google-cloud-translate package not installed. Install with: pip install google-cloud-translate")
    
    @patch('google.cloud.translate_v2.Client')
    def test_list_languages_basic(self, mock_client_class):
        """Test list_languages returns language list"""
        # Mock the translate client
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        
        # Mock the language list response
        mock_client.get_languages.return_value = [
            {'language': 'en', 'name': 'English'},
            {'language': 'es', 'name': 'Spanish'},
            {'language': 'fr', 'name': 'French'},
            {'language': 'de', 'name': 'German'},
            {'language': 'zh', 'name': 'Chinese'},
        ]
        
        # Call the function
        result = list_languages()
        
        # Verify the client was created
        mock_client_class.assert_called_once()
        
        # Verify get_languages was called
        mock_client.get_languages.assert_called_once()
        
        # Verify the result
        self.assertIsInstance(result, list)
        self.assertEqual(len(result), 5)
        
        # Verify structure
        for lang in result:
            self.assertIn('language', lang)
            self.assertIn('name', lang)
    
    @patch('google.cloud.translate_v2.Client')
    def test_list_languages_contains_common_languages(self, mock_client_class):
        """Test that list_languages includes common languages"""
        # Mock the translate client
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        
        # Mock a comprehensive language list
        mock_languages = [
            {'language': 'en', 'name': 'English'},
            {'language': 'es', 'name': 'Spanish'},
            {'language': 'fr', 'name': 'French'},
            {'language': 'de', 'name': 'German'},
            {'language': 'zh', 'name': 'Chinese'},
            {'language': 'ja', 'name': 'Japanese'},
            {'language': 'ko', 'name': 'Korean'},
            {'language': 'pt', 'name': 'Portuguese'},
            {'language': 'ru', 'name': 'Russian'},
            {'language': 'ar', 'name': 'Arabic'},
        ]
        mock_client.get_languages.return_value = mock_languages
        
        # Call the function
        result = list_languages()
        
        # Extract language codes
        lang_codes = [lang['language'] for lang in result]
        
        # Verify common languages are present
        common_languages = ['en', 'es', 'fr', 'de', 'zh', 'ja']
        for lang_code in common_languages:
            self.assertIn(
                lang_code,
                lang_codes,
                f"Language '{lang_code}' should be in the list"
            )


class TestGoogleAPIIntegration(unittest.TestCase):
    """Integration tests that actually call Google Cloud API
    
    These tests require valid Google Cloud credentials and will be skipped
    if credentials are not configured or if the package is not installed.
    """
    
    def setUp(self):
        """Check if Google Cloud credentials are configured and package is installed"""
        if not GOOGLE_CLOUD_AVAILABLE:
            self.skipTest("google-cloud-translate package not installed. Install with: pip install google-cloud-translate")
        
        creds_path = os.getenv('GOOGLE_APPLICATION_CREDENTIALS')
        if creds_path is None or not Path(creds_path).exists():
            self.skipTest(
                "Google Cloud credentials not configured. "
                "Set GOOGLE_APPLICATION_CREDENTIALS environment variable to run integration tests."
            )
    
    def test_translate_simple_text_integration(self):
        """Integration test: Translate a simple Spanish phrase to English"""
        try:
            result = translate_text(
                text="Hola mundo",
                target_language="en",
                source_language="es"
            )
            
            self.assertIsInstance(result, list)
            self.assertEqual(len(result), 1)
            self.assertIn('translatedText', result[0])
            self.assertIn('input', result[0])
            
            # Verify the translation is reasonable
            translated = result[0]['translatedText'].lower()
            self.assertIn('hello', translated, f"Translation '{translated}' should contain 'hello'")
            
            print(f"✅ Translation test passed: '{result[0]['input']}' -> '{result[0]['translatedText']}'")
            
        except Exception as e:
            self.fail(f"Translation failed: {e}")
    
    def test_translate_with_auto_detect_integration(self):
        """Integration test: Translate with auto-detection of source language"""
        try:
            result = translate_text(
                text="Bonjour",
                target_language="en",
                source_language=None  # Auto-detect
            )
            
            self.assertIsInstance(result, list)
            self.assertEqual(len(result), 1)
            self.assertIn('translatedText', result[0])
            self.assertIn('detectedSourceLanguage', result[0])
            
            # Verify French was detected
            detected = result[0].get('detectedSourceLanguage', '').lower()
            self.assertIn('fr', detected, f"Should detect French, got: {detected}")
            
            print(f"✅ Auto-detect test passed: Detected '{detected}', translated to '{result[0]['translatedText']}'")
            
        except Exception as e:
            self.fail(f"Auto-detect translation failed: {e}")
    
    def test_translate_multiple_texts_integration(self):
        """Integration test: Translate multiple texts at once"""
        try:
            texts = ["Hello", "Goodbye", "Thank you"]
            result = translate_text(
                text=texts,
                target_language="es",
                source_language="en"
            )
            
            self.assertIsInstance(result, list)
            self.assertEqual(len(result), 3)
            
            # Verify all have translations
            for item in result:
                self.assertIn('translatedText', item)
                self.assertIn('input', item)
            
            print(f"✅ Batch translation test passed:")
            for item in result:
                print(f"   '{item['input']}' -> '{item['translatedText']}'")
            
        except Exception as e:
            self.fail(f"Batch translation failed: {e}")
    
    def test_list_languages_integration(self):
        """Integration test: List all available languages"""
        try:
            result = list_languages()
            
            self.assertIsInstance(result, list)
            self.assertGreater(len(result), 50, "Should have at least 50 languages")
            
            # Verify structure of first few languages
            for lang in result[:5]:
                self.assertIn('language', lang)
                self.assertIn('name', lang)
            
            # Extract some language codes
            lang_codes = [lang['language'] for lang in result]
            
            # Verify common languages are present
            for code in ['en', 'es', 'fr', 'de', 'zh', 'ja', 'ko']:
                self.assertIn(code, lang_codes, f"Language '{code}' should be available")
            
            print(f"✅ Language list test passed: {len(result)} languages available")
            sample_langs = ', '.join([f"{l['name']} ({l['language']})" for l in result[:5]])
            print(f"   Sample languages: {sample_langs}")
            
        except Exception as e:
            self.fail(f"List languages failed: {e}")


class TestGoogleAPIErrorHandling(unittest.TestCase):
    """Test error handling in google_api functions"""
    
    def setUp(self):
        """Skip tests if google-cloud-translate is not installed"""
        if not GOOGLE_CLOUD_AVAILABLE:
            self.skipTest("google-cloud-translate package not installed. Install with: pip install google-cloud-translate")
    
    @patch('google.cloud.translate_v2.Client')
    def test_translate_handles_api_error(self, mock_client_class):
        """Test that translate_text handles API errors appropriately"""
        # Mock the translate client to raise an exception
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        mock_client.translate.side_effect = Exception("API Error")
        
        # Verify that the exception is propagated
        with self.assertRaises(Exception) as context:
            translate_text(
                text="Test",
                target_language="en",
                source_language="es"
            )
        
        self.assertIn("API Error", str(context.exception))
    
    @patch('google.cloud.translate_v2.Client')
    def test_list_languages_handles_api_error(self, mock_client_class):
        """Test that list_languages handles API errors appropriately"""
        # Mock the translate client to raise an exception
        mock_client = MagicMock()
        mock_client_class.return_value = mock_client
        mock_client.get_languages.side_effect = Exception("API Error")
        
        # Verify that the exception is propagated
        with self.assertRaises(Exception) as context:
            list_languages()
        
        self.assertIn("API Error", str(context.exception))


def run_tests():
    """Run all tests"""
    # Create test suite
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()
    
    # Add all test cases
    suite.addTests(loader.loadTestsFromTestCase(TestGoogleAPISetup))
    suite.addTests(loader.loadTestsFromTestCase(TestTranslateTextFunction))
    suite.addTests(loader.loadTestsFromTestCase(TestListLanguagesFunction))
    suite.addTests(loader.loadTestsFromTestCase(TestGoogleAPIIntegration))
    suite.addTests(loader.loadTestsFromTestCase(TestGoogleAPIErrorHandling))
    
    # Run tests
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    
    # Print summary
    print("\n" + "="*70)
    print("TEST SUMMARY")
    print("="*70)
    print(f"Tests run: {result.testsRun}")
    print(f"Successes: {result.testsRun - len(result.failures) - len(result.errors) - len(result.skipped)}")
    print(f"Failures: {len(result.failures)}")
    print(f"Errors: {len(result.errors)}")
    print(f"Skipped: {len(result.skipped)}")
    
    if result.skipped:
        print("\nSkipped tests (usually due to missing credentials):")
        for test, reason in result.skipped:
            print(f"  - {test}: {reason}")
    
    print("="*70)
    
    # Return exit code
    return 0 if result.wasSuccessful() else 1


if __name__ == "__main__":
    sys.exit(run_tests())
