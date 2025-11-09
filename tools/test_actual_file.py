"""Quick test of actual messages.zh-CN.xlf file"""
from pathlib import Path
from locale_manager import LocaleManager

manager = LocaleManager()
file = Path('../web/src/locale/messages.zh-CN.xlf')

if file.exists():
    source_lang, target_lang, units = manager.parse_xliff(file)
    print(f'✓ Parsed {len(units)} translation units')
    
    # Check for duplication in the problematic unit
    problem_units = [u for u in units if '选择一种语言' in u.target]
    if problem_units:
        unit = problem_units[0]
        count = unit.target.count('选择一种语言...')
        if count == 1:
            print(f'✓ No duplication in "选择一种语言..." (appears {count} time)')
        else:
            print(f'✗ DUPLICATION FOUND: "选择一种语言..." appears {count} times')
            print(f'  Content: {unit.target[:100]}...')
    
    # Check all units for any duplication patterns
    issues = []
    for unit in units:
        # Look for repeated long strings
        if len(unit.target) > 10:
            half = len(unit.target) // 2
            if unit.target[:half] == unit.target[half:half*2]:
                issues.append(f"Unit {unit.id}: possible duplication")
    
    if issues:
        print(f'✗ Found {len(issues)} potential issues:')
        for issue in issues:
            print(f'  {issue}')
    else:
        print(f'✓ No duplication patterns found in any units')
else:
    print(f'✗ File not found: {file}')
