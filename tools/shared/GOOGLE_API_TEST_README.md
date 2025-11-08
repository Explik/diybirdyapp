# Google API Testing Guide

This guide explains how to run unit tests for `google_api.py` to verify proper setup of your local environment.

## Prerequisites

1. **Python Environment**: Ensure you have Python 3.7+ installed with required packages
2. **Google Cloud Credentials**: Valid Google Cloud service account credentials

## Setup Google Cloud Credentials

### Option 1: Using Environment Variable (Recommended)

Set the `GOOGLE_APPLICATION_CREDENTIALS` environment variable to point to your service account JSON file:

#### Windows (PowerShell)
```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\your\credentials.json"
```

#### Windows (Command Prompt)
```cmd
set GOOGLE_APPLICATION_CREDENTIALS=C:\path\to\your\credentials.json
```

#### Linux/Mac
```bash
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/credentials.json"
```

### Option 2: Default Application Credentials

If you've set up gcloud CLI, you can use:
```bash
gcloud auth application-default login
```

## Required Python Packages

Install the required packages:

```bash
pip install google-cloud-translate
pip install pytest  # Optional, for running with pytest
```

Or if you have a requirements file:
```bash
pip install -r requirements.txt
```

## Running the Tests

### Method 1: Direct Execution

Run the test file directly:
```bash
python test_google_api.py
```

### Method 2: Using pytest (Recommended)

Run with pytest for better output:
```bash
pytest test_google_api.py -v
```

For even more detailed output:
```bash
pytest test_google_api.py -v -s
```

### Method 3: Using Python unittest

```bash
python -m unittest test_google_api -v
```

## Test Categories

The test suite includes several categories of tests:

### 1. Setup Tests (`TestGoogleAPISetup`)
- ✅ Verifies `GOOGLE_APPLICATION_CREDENTIALS` is set
- ✅ Validates credentials file exists and is valid JSON
- ✅ Checks basic credential structure

### 2. Unit Tests (`TestTranslateTextFunction`, `TestListLanguagesFunction`)
- ✅ Tests with **mocked** Google Cloud API (no actual API calls)
- ✅ Tests various input types (string, bytes, list)
- ✅ Tests language auto-detection
- ✅ Tests batch translation

### 3. Integration Tests (`TestGoogleAPIIntegration`)
- ⚠️ Makes **real** API calls to Google Cloud
- ⚠️ Requires valid credentials and may incur costs
- ✅ Tests actual translation functionality
- ✅ Tests language listing
- ✅ Tests auto-detection with real API

### 4. Error Handling Tests (`TestGoogleAPIErrorHandling`)
- ✅ Tests API error handling
- ✅ Tests exception propagation

## Skipped Tests

If you see tests being skipped, it usually means:
- `GOOGLE_APPLICATION_CREDENTIALS` is not set
- Credentials file doesn't exist
- API credentials are not valid

Example output:
```
test_translate_simple_text_integration (test_google_api.TestGoogleAPIIntegration) ... skipped 'Google Cloud credentials not configured.'
```

## Expected Output

Successful test run:
```
test_google_credentials_env_variable ... ok
test_credentials_file_is_json ... ok
test_translate_text_with_string ... ok
test_translate_text_with_bytes ... ok
test_translate_text_with_list ... ok
...

======================================================================
TEST SUMMARY
======================================================================
Tests run: 20
Successes: 18
Failures: 0
Errors: 0
Skipped: 2

Skipped tests (usually due to missing credentials):
  - test_translate_simple_text_integration: Google Cloud credentials not configured.
  - test_list_languages_integration: Google Cloud credentials not configured.
======================================================================

Ran 20 tests in 0.123s

OK (skipped=2)
```

## Troubleshooting

### Issue: All tests are skipped
**Solution**: Set the `GOOGLE_APPLICATION_CREDENTIALS` environment variable

### Issue: "Credentials file does not exist"
**Solution**: Verify the path in `GOOGLE_APPLICATION_CREDENTIALS` is correct

### Issue: "API Error" or "Permission Denied"
**Solution**: 
1. Verify your service account has the "Cloud Translation API User" role
2. Ensure the Translation API is enabled in your Google Cloud project
3. Check that your credentials haven't expired

### Issue: "Module not found: google.cloud.translate"
**Solution**: Install the required package:
```bash
pip install google-cloud-translate
```

### Issue: Integration tests fail with timeout
**Solution**: 
1. Check your internet connection
2. Verify Google Cloud services are accessible
3. Check for any firewall/proxy issues

## Running Only Specific Test Categories

### Run only unit tests (no API calls):
```bash
pytest test_google_api.py::TestTranslateTextFunction -v
pytest test_google_api.py::TestListLanguagesFunction -v
```

### Run only integration tests:
```bash
pytest test_google_api.py::TestGoogleAPIIntegration -v
```

### Run only setup validation:
```bash
pytest test_google_api.py::TestGoogleAPISetup -v
```

## Cost Considerations

⚠️ **Important**: Integration tests make real API calls to Google Cloud Translation API

- Unit tests (with mocks): **FREE** - No API calls
- Integration tests: **May incur charges** - Makes actual API calls

Google Cloud Translation pricing (as of 2024):
- First 500,000 characters per month: FREE
- After that: $20 per 1 million characters

The integration tests translate only a few short phrases, costing fractions of a cent per run.

## Continuous Integration

To run tests in CI/CD without credentials (unit tests only):
```bash
pytest test_google_api.py -k "not Integration" -v
```

This skips integration tests that require actual credentials.

## Additional Resources

- [Google Cloud Translation API Documentation](https://cloud.google.com/translate/docs)
- [Authentication Guide](https://cloud.google.com/docs/authentication/getting-started)
- [Python Client Library](https://cloud.google.com/python/docs/reference/translate/latest)

## Quick Start Checklist

- [ ] Install `google-cloud-translate` package
- [ ] Obtain Google Cloud service account credentials JSON
- [ ] Set `GOOGLE_APPLICATION_CREDENTIALS` environment variable
- [ ] Run `python test_google_api.py` to verify setup
- [ ] Check that unit tests pass
- [ ] Verify integration tests (if credentials are configured)

## Support

If you continue to have issues:
1. Review the test output carefully
2. Check the error messages
3. Verify your Google Cloud project setup
4. Ensure billing is enabled for your project (for integration tests)
