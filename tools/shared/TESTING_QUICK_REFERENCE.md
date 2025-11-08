# Google API Testing - Quick Reference

## One-Line Test Command
```bash
python tools/shared/test_google_api.py
```

## Installation Check
```bash
pip install google-cloud-translate
```

## Set Credentials (Windows PowerShell)
```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\credentials.json"
```

## Set Credentials (Windows CMD)
```cmd
set GOOGLE_APPLICATION_CREDENTIALS=C:\path\to\credentials.json
```

## Set Credentials (Linux/Mac)
```bash
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/credentials.json"
```

## Test Status Quick Interpretation

### ✅ Expected: All tests skipped
**Meaning:** Package not installed yet  
**Action:** `pip install google-cloud-translate`

### ✅ Expected: Integration tests skipped  
**Meaning:** Credentials not configured  
**Action:** Set `GOOGLE_APPLICATION_CREDENTIALS` environment variable

### ✅ Perfect: Only 2 credential tests skipped
**Meaning:** Everything working! Integration tests just need credentials for full run  
**Action:** (Optional) Configure credentials to run integration tests

### ❌ Unexpected: Tests fail
**Meaning:** Something is misconfigured  
**Action:** Check error messages and see `GOOGLE_API_TEST_README.md`

## Cost Info
- **Unit tests (mocked)**: FREE ✅
- **Integration tests**: ~$0.0001 per run (negligible)
- First 500K characters/month on Google Cloud: FREE

## Files Reference
- `google_api.py` - Source code
- `test_google_api.py` - Unit tests
- `GOOGLE_API_TEST_README.md` - Full documentation
- `TEST_SUMMARY.md` - Detailed summary

## Common Issues

### "Module 'google' has no attribute 'cloud'"
→ Install package: `pip install google-cloud-translate`

### "GOOGLE_APPLICATION_CREDENTIALS not set"
→ Set environment variable (see commands above)

### "Credentials file does not exist"
→ Check path in environment variable

### "Permission Denied" or "API Error"
→ Verify service account has "Cloud Translation API User" role
→ Ensure Translation API is enabled in Google Cloud project

## For CI/CD: Run Only Unit Tests
```bash
pytest test_google_api.py -k "not Integration" -v
```

## Help
See `GOOGLE_API_TEST_README.md` for comprehensive guide
