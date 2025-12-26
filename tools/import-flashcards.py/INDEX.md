# 📚 Documentation Index

Complete documentation for the Flashcard Import Tool Streamlit application.

## 🚀 Getting Started

Start here if you're new to the application:

1. **[QUICKSTART.md](QUICKSTART.md)** - Quick setup and first run guide
2. **[README.md](README.md)** - Updated overview with features
3. **[BUILD_SUMMARY.md](BUILD_SUMMARY.md)** - What was built and how it works

## 📖 Main Documentation

### User Documentation

- **[README_APP.md](README_APP.md)** - Complete application documentation
  - Installation instructions
  - Usage guide
  - Project structure
  - Dependencies
  - Internal data formats

- **[UI_GUIDE.md](UI_GUIDE.md)** - Visual UI guide
  - Page layouts
  - UI components
  - User flows
  - Design principles

### Technical Documentation

- **[config.py](config.py)** - Configuration settings
  - API endpoints
  - File upload limits
  - UI preferences

- **[app_utils.py](app_utils.py)** - Utility functions
  - Reusable UI components
  - Helper functions
  - Validation utilities

### Reference Documentation

- **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** - Problem solving guide
  - Common errors and solutions
  - API connection issues
  - Translation problems
  - Module import issues
  - Performance optimization
  - Platform-specific issues
  - Diagnostics checklist

## 📁 Application Files

### Core Application

- **[app.py](app.py)** - Main entry point and home page
- **[run.ps1](run.ps1)** - PowerShell launcher script

### Pages (Multi-page Streamlit structure)

Located in `pages/` directory:

1. **[1_📝_Create_from_TXT.py](pages/1_📝_Create_from_TXT.py)** - ✅ Implemented
   - Upload .txt files
   - Automatic translation
   - Flashcard creation
   - Progress tracking

2. **[2_📊_Create_from_CSV.py](pages/2_📊_Create_from_CSV.py)** - 🚧 Placeholder
   - CSV import (coming soon)
   - UI mockup included

3. **[3_📦_Create_from_Anki.py](pages/3_📦_Create_from_Anki.py)** - 🚧 Placeholder
   - Anki deck import (coming soon)
   - UI mockup included

4. **[4_🔊_Add_Pronunciation.py](pages/4_🔊_Add_Pronunciation.py)** - 🚧 Placeholder
   - TTS pronunciation (coming soon)
   - UI mockup included

### Supporting Files

- **[requirements.txt](requirements.txt)** - Python dependencies
- **[.gitignore](.gitignore)** - Git ignore rules
- **[sample_sentences.txt](sample_sentences.txt)** - Test data

## 🔧 Dependencies

### External Dependencies

The app depends on shared modules from the parent `tools` directory:

- `import_client.py` (local) - API client for flashcard operations and backend translation
- `../shared/api_client/` - Auto-generated OpenAPI client

### Python Packages

See [requirements.txt](requirements.txt) for the full list:
- `streamlit>=1.28.0` - Web application framework
- `requests>=2.31.0` - HTTP library

## 🎯 Quick Navigation

### I want to...

**...get started quickly**
→ [QUICKSTART.md](QUICKSTART.md)

**...understand what was built**
→ [BUILD_SUMMARY.md](BUILD_SUMMARY.md)

**...see the UI and user flows**
→ [UI_GUIDE.md](UI_GUIDE.md)

**...fix a problem**
→ [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

**...understand the code**
→ [app.py](app.py) and [pages/1_📝_Create_from_TXT.py](pages/1_📝_Create_from_TXT.py)

**...configure the app**
→ [config.py](config.py)

**...add a new feature**
→ [README_APP.md](README_APP.md#contributing)

**...understand the data format**
→ [README_APP.md](README_APP.md#internal-data-format)

## 📊 Document Status

| Document | Status | Last Updated | Purpose |
|----------|--------|--------------|---------|
| QUICKSTART.md | ✅ Complete | 2025-11-08 | Quick setup guide |
| README.md | ✅ Updated | 2025-11-08 | Overview with quick start |
| README_APP.md | ✅ Complete | 2025-11-08 | Full documentation |
| BUILD_SUMMARY.md | ✅ Complete | 2025-11-08 | Build summary |
| UI_GUIDE.md | ✅ Complete | 2025-11-08 | Visual guide |
| TROUBLESHOOTING.md | ✅ Complete | 2025-11-08 | Problem solving |
| config.py | ✅ Complete | 2025-11-08 | Configuration |
| app_utils.py | ✅ Complete | 2025-11-08 | Utilities |
| INDEX.md | ✅ Complete | 2025-11-08 | This file |

## 🔄 Version History

### v1.0.0 (2025-11-08)
- ✅ Initial multi-page Streamlit app created
- ✅ TXT import feature fully implemented
- ✅ Placeholder pages for future features
- ✅ Complete documentation suite
- ✅ Launcher scripts and utilities
- ✅ Sample data and test files

## 📝 Documentation Conventions

### Emoji Usage

- 🚀 Getting started / Quick actions
- 📖 Documentation / Reading material
- 🔧 Configuration / Technical details
- 🎯 Goals / Objectives / Navigation
- ✅ Completed / Working features
- 🚧 In progress / Coming soon
- ❌ Errors / Problems
- ⚠️ Warnings / Cautions
- ℹ️ Information / Tips
- 📁 Files / Folders
- 📊 Data / Statistics
- 🎨 UI / Design
- 💡 Ideas / Concepts

### File Naming

- `README*.md` - Main documentation files
- `*_GUIDE.md` - Detailed guides
- `*.md` - General markdown documents
- `*.py` - Python source files
- `*.txt` - Text data files
- `*.ps1` - PowerShell scripts

## 🤝 Contributing

To improve the documentation:

1. Follow the existing structure and style
2. Use consistent emoji conventions
3. Update this INDEX.md when adding new docs
4. Include code examples where appropriate
5. Keep documents focused and concise

## 📞 Support

For issues or questions:

1. Check [TROUBLESHOOTING.md](TROUBLESHOOTING.md) first
2. Review relevant documentation sections
3. Check application logs
4. Verify prerequisites are met

## 🎓 Learning Path

Recommended reading order for new users:

1. **Getting Started**
   - QUICKSTART.md
   - README.md

2. **Understanding the App**
   - BUILD_SUMMARY.md
   - UI_GUIDE.md

3. **Using the App**
   - README_APP.md
   - Sample workflows

4. **Troubleshooting**
   - TROUBLESHOOTING.md
   - Common issues

5. **Advanced Topics**
   - config.py
   - app_utils.py
   - Contributing guide

## 🏗️ Architecture Overview

```
User Interface (Streamlit)
         ↓
  app.py (Home)
         ↓
    Pages/*.py
         ↓
   app_utils.py
         ↓
Shared Modules (../shared/)
         ↓
  import_client.py
         ↓
  OpenAPI Client
         ↓
    API Server
```

## 📦 File Organization

```
import-flashcards.py/
├── Documentation/
│   ├── INDEX.md (this file)
│   ├── QUICKSTART.md
│   ├── README_APP.md
│   ├── BUILD_SUMMARY.md
│   ├── UI_GUIDE.md
│   └── TROUBLESHOOTING.md
│
├── Application/
│   ├── app.py
│   ├── app_utils.py
│   ├── config.py
│   └── pages/
│       ├── 1_📝_Create_from_TXT.py
│       ├── 2_📊_Create_from_CSV.py
│       ├── 3_📦_Create_from_Anki.py
│       └── 4_🔊_Add_Pronunciation.py
│
├── Configuration/
│   ├── requirements.txt
│   ├── .gitignore
│   └── config.py
│
└── Resources/
    ├── sample_sentences.txt
    ├── run.ps1
    └── README.md
```

---

**Last Updated:** November 8, 2025
**Version:** 1.0.0
**Status:** Complete and Ready to Use

Happy flashcard creation! 🎴
