"""
Script to fix OpenAPI client imports from absolute to relative.
This allows the openapi_client package to work when nested under tools/shared/api_client/
"""
import os
import re
from pathlib import Path

def fix_imports_in_file(file_path):
    """Fix absolute imports to relative imports in a Python file."""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    file_path_str = str(file_path).replace('\\', '/')
    
    # Determine location context
    is_in_api_dir = '/openapi_client/api/' in file_path_str and '__init__.py' not in file_path_str
    is_in_models_dir = '/openapi_client/models/' in file_path_str and '__init__.py' not in file_path_str
    is_api_init = '/openapi_client/api/__init__.py' in file_path_str
    is_models_init = '/openapi_client/models/__init__.py' in file_path_str
    is_root_init = file_path_str.endswith('/openapi_client/__init__.py')
    is_api_client = file_path_str.endswith('/openapi_client/api_client.py')
    
    # Fix imports based on file location
    if is_api_init:
        # In api/__init__.py: from openapi_client.api.xxx -> from .xxx
        content = re.sub(
            r'from openapi_client\.api\.([a-zA-Z0-9_]+) import',
            r'from .\1 import',
            content
        )
    elif is_models_init:
        # In models/__init__.py: from openapi_client.models.xxx -> from .xxx
        content = re.sub(
            r'from openapi_client\.models\.([a-zA-Z0-9_]+) import',
            r'from .\1 import',
            content
        )
    elif is_root_init:
        # In root __init__.py: from openapi_client.models.xxx -> from .models.xxx
        content = re.sub(
            r'from openapi_client\.models\.([a-zA-Z0-9_]+) import',
            r'from .models.\1 import',
            content
        )
        # In root __init__.py: from openapi_client.api.xxx -> from .api.xxx
        content = re.sub(
            r'from openapi_client\.api\.([a-zA-Z0-9_]+) import',
            r'from .api.\1 import',
            content
        )
        # In root __init__.py: from openapi_client.xxx -> from .xxx (for root modules like api_client, configuration, exceptions)
        content = re.sub(
            r'from openapi_client\.([a-zA-Z0-9_]+) import',
            r'from .\1 import',
            content
        )
    elif is_api_client:
        # In api_client.py: from openapi_client.xxx import -> from .xxx import
        content = re.sub(
            r'from openapi_client\.([a-zA-Z0-9_]+) import',
            r'from .\1 import',
            content
        )
        # In api_client.py: from openapi_client import rest -> from . import rest
        content = re.sub(
            r'from openapi_client import ([a-zA-Z0-9_]+)',
            r'from . import \1',
            content
        )
    elif is_in_api_dir:
        # In api/*.py files: from openapi_client.models.xxx -> from ..models.xxx
        content = re.sub(
            r'from openapi_client\.models\.([a-zA-Z0-9_]+) import',
            r'from ..models.\1 import',
            content
        )
        # In api/*.py files: from openapi_client.xxx import -> from ..xxx import
        content = re.sub(
            r'from openapi_client\.([a-zA-Z0-9_]+) import',
            r'from ..\1 import',
            content
        )
        # In api/*.py files: from openapi_client import xxx -> from .. import xxx
        content = re.sub(
            r'from openapi_client import ([a-zA-Z0-9_]+)',
            r'from .. import \1',
            content
        )
    elif is_in_models_dir:
        # In models/*.py files: from openapi_client.models.xxx -> from .xxx
        content = re.sub(
            r'from openapi_client\.models\.([a-zA-Z0-9_]+) import',
            r'from .\1 import',
            content
        )
    else:
        # Other root level files: from openapi_client.xxx import -> from .xxx import
        content = re.sub(
            r'from openapi_client\.([a-zA-Z0-9_]+) import',
            r'from .\1 import',
            content
        )
        # Handle: from openapi_client import xxx -> from . import xxx
        content = re.sub(
            r'from openapi_client import ([a-zA-Z0-9_]+)',
            r'from . import \1',
            content
        )
    
    # Pattern: import openapi_client.xxx (standalone import statement)
    content = re.sub(
        r'^import openapi_client\.([a-zA-Z0-9_\.]+)',
        r'from . import \1',
        content,
        flags=re.MULTILINE
    )
    
    # Pattern: import_module("openapi_client.xxx")
    content = re.sub(
        r'import_module\("openapi_client\.([a-zA-Z0-9_\.]+)"\)',
        r'import_module(".\1", package=__package__)',
        content
    )
    
    # Pattern: getattr(openapi_client.models, xxx) -> getattr(models, xxx)
    # This is specific to api_client.py where models is already imported
    if is_api_client:
        content = re.sub(
            r'getattr\(openapi_client\.models,',
            r'getattr(models,',
            content
        )
    
    # Only write if content changed
    if content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    """Fix all Python files in the openapi_client directory."""
    base_dir = Path(__file__).parent / 'shared' / 'api_client' / 'openapi_client'
    
    if not base_dir.exists():
        print(f"Error: Directory not found: {base_dir}")
        return
    
    fixed_count = 0
    for py_file in base_dir.rglob('*.py'):
        if fix_imports_in_file(py_file):
            print(f"Fixed: {py_file.relative_to(base_dir)}")
            fixed_count += 1
    
    print(f"\nTotal files fixed: {fixed_count}")

if __name__ == '__main__':
    main()
