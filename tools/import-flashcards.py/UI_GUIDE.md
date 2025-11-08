# Application Screenshots & UI Guide

## 🏠 Home Page

**URL:** `http://localhost:8501`

```
┌────────────────────────────────────────────────────────────────┐
│  Sidebar                    │  Main Content                    │
├────────────────────────────────────────────────────────────────┤
│                            │                                   │
│  🎴 Flashcard Import Tool  │  🎴 Flashcard Import Tool        │
│                            │  ────────────────────────────     │
│  Navigation:               │                                   │
│  ▸ Home                    │  Welcome to the Flashcard        │
│  ▸ 📝 Create from TXT      │  Import Tool                      │
│  ▸ 📊 Create from CSV      │                                   │
│  ▸ 📦 Create from Anki     │  This application helps you      │
│  ▸ 🔊 Add Pronunciation    │  create flashcard decks...       │
│                            │                                   │
│                            │  Available Features:              │
│                            │  • Create deck from .txt file     │
│                            │  • Create deck from .csv file     │
│                            │  • Create deck from .anki file    │
│                            │  • Add pronunciation              │
│                            │                                   │
│                            │  Getting Started                  │
│                            │  1. Select a feature from         │
│                            │     the sidebar...                │
│                            │                                   │
└────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- Navigation sidebar with emoji icons
- Welcome message
- Feature overview
- Getting started instructions
- System requirements

---

## 📝 Create from TXT Page

**URL:** `http://localhost:8501/Create_from_TXT`

```
┌────────────────────────────────────────────────────────────────┐
│  📝 Create Deck from .txt File                                 │
│  ──────────────────────────────────────────────────────────    │
│                                                                │
│  This tool allows you to create flashcard decks from plain    │
│  text files. Upload a text file with one sentence per line... │
│                                                                │
├──────────────────────────┬─────────────────────────────────────┤
│  Upload and Config       │  Generation Options                 │
├──────────────────────────┼─────────────────────────────────────┤
│                          │                                     │
│  Choose a .txt file      │  Deck Name:                         │
│  ┌────────────────────┐  │  ┌──────────────────────────────┐  │
│  │ Drag and drop     │  │  │ My Flashcard Deck            │  │
│  │ or click to       │  │  └──────────────────────────────┘  │
│  │ browse            │  │                                     │
│  └────────────────────┘  │  Description:                       │
│                          │  ┌──────────────────────────────┐  │
│  📄 File loaded:         │  │ Brief description...         │  │
│  sample_sentences.txt    │  │                              │  │
│  (10 lines)              │  │                              │  │
│                          │  └──────────────────────────────┘  │
│  ▼ Preview file content  │                                     │
│  1. He didn't go...      │                                     │
│  2. She is very happy.   │                                     │
│  ...                     │                                     │
└──────────────────────────┴─────────────────────────────────────┘
│                                                                │
│  Language Settings                                             │
│  ──────────────────────────────────────────────────────────    │
│                                                                │
│  Source Language          Target Language                     │
│  ┌─────────────────┐      ┌─────────────────┐                 │
│  │ Auto-detect    ▼│      │ Spanish        ▼│                 │
│  └─────────────────┘      └─────────────────┘                 │
│                                                                │
│  Additional Options                                            │
│  ──────────────────────────────────────────────────────────    │
│  ☐ Generate Pronunciation Audio                                │
│                                                                │
│  ──────────────────────────────────────────────────────────    │
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │         🚀 Generate Flashcard Deck                       │ │
│  └──────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
```

**During Processing:**
```
┌────────────────────────────────────────────────────────────────┐
│  ⏳ Creating flashcard deck...                                 │
│  ✅ Created deck: My Flashcard Deck                            │
│                                                                │
│  Progress: ████████████████████░░░░░░ 75%                     │
│  Processing flashcard 8/10: Thank you very much...            │
└────────────────────────────────────────────────────────────────┘
```

**After Completion:**
```
┌────────────────────────────────────────────────────────────────┐
│  ✅ Successfully created 10 flashcards!                        │
│  🎈 🎈 🎈                                                       │
│                                                                │
│  ──────────────────────────────────────────────────────────    │
│  📊 Summary                                                    │
│  ──────────────────────────────────────────────────────────    │
│                                                                │
│  Deck Name              Flashcards Created    Languages        │
│  My Flashcard Deck      10                    English → Spanish│
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │         Create Another Deck                              │ │
│  └──────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
```

**Key Features:**
1. **File Upload Zone:** Drag-and-drop or click to browse
2. **File Preview:** Shows first 10 lines before processing
3. **Deck Configuration:** Name and description fields
4. **Language Selection:** Dropdowns populated from API
5. **Progress Tracking:** Real-time progress bar
6. **Status Updates:** Shows current flashcard being processed
7. **Success Feedback:** Balloons animation and summary stats
8. **Reset Option:** Create another deck button

---

## 📊 Create from CSV Page (Placeholder)

**URL:** `http://localhost:8501/Create_from_CSV`

```
┌────────────────────────────────────────────────────────────────┐
│  📊 Create Deck from .csv File                                 │
│  ──────────────────────────────────────────────────────────    │
│                                                                │
│  ℹ️ This feature is coming soon!                              │
│                                                                │
│  Planned Features:                                             │
│  • CSV Import: Upload CSV files with custom field mapping     │
│  • Column Selection: Choose which columns contain content     │
│  • Batch Processing: Import large datasets efficiently        │
│  • Data Validation: Verify data integrity before import       │
│  • Preview Mode: Review flashcards before final creation      │
│                                                                │
│  ▼ Preview: Future Interface                                  │
│  [Shows mockup of planned interface with disabled controls]   │
└────────────────────────────────────────────────────────────────┘
```

---

## 📦 Create from Anki Page (Placeholder)

**URL:** `http://localhost:8501/Create_from_Anki`

```
┌────────────────────────────────────────────────────────────────┐
│  📦 Create Deck from .anki File                                │
│  ──────────────────────────────────────────────────────────    │
│                                                                │
│  ℹ️ This feature is coming soon!                              │
│                                                                │
│  Planned Features:                                             │
│  • Anki Deck Import: Import existing Anki (.apkg) files       │
│  • Media Preservation: Maintain images, audio, and video      │
│  • Card Type Support: Handle various Anki card types          │
│  • Tag Migration: Import and preserve card tags               │
│                                                                │
│  What is an Anki File?                                         │
│  Anki is a popular spaced repetition flashcard application... │
│                                                                │
│  ▼ Preview: Future Interface                                  │
│  [Shows mockup of planned interface with disabled controls]   │
└────────────────────────────────────────────────────────────────┘
```

---

## 🔊 Add Pronunciation Page (Placeholder)

**URL:** `http://localhost:8501/Add_Pronunciation`

```
┌────────────────────────────────────────────────────────────────┐
│  🔊 Add Pronunciation to Flashcards                            │
│  ──────────────────────────────────────────────────────────    │
│                                                                │
│  ℹ️ This feature is coming soon!                              │
│                                                                │
│  Planned Features:                                             │
│  • Text-to-Speech Generation: Automatically generate audio    │
│  • Multi-Language Support: Support for all system languages   │
│  • Voice Selection: Choose from different voice options       │
│  • Batch Processing: Add pronunciation to multiple decks      │
│  • Quality Settings: Select audio quality and format          │
│                                                                │
│  ▼ Preview: Future Interface                                  │
│  [Shows mockup of planned interface with disabled controls]   │
└────────────────────────────────────────────────────────────────┘
```

---

## 🎨 UI Components Used

### Streamlit Elements

1. **Page Config**
   - Title: "Flashcard Import Tool"
   - Icon: 🎴
   - Layout: Wide
   - Sidebar: Expanded

2. **Layout Components**
   - `st.columns()` - Multi-column layouts
   - `st.expander()` - Collapsible sections
   - `st.sidebar` - Navigation menu

3. **Input Widgets**
   - `st.file_uploader()` - File upload
   - `st.text_input()` - Single-line text
   - `st.text_area()` - Multi-line text
   - `st.selectbox()` - Dropdown selection
   - `st.checkbox()` - Boolean options
   - `st.button()` - Action buttons

4. **Display Elements**
   - `st.title()` - Page titles
   - `st.subheader()` - Section headers
   - `st.markdown()` - Formatted text
   - `st.info()` - Info messages (blue)
   - `st.success()` - Success messages (green)
   - `st.error()` - Error messages (red)
   - `st.warning()` - Warning messages (yellow)
   - `st.metric()` - Statistics display

5. **Progress Indicators**
   - `st.progress()` - Progress bar
   - `st.spinner()` - Loading spinner
   - `st.balloons()` - Celebration animation

6. **Session Management**
   - `st.session_state` - Persistent state
   - `st.rerun()` - Force re-render

---

## 🎯 User Flow

### Typical Usage Flow

```
Start App
    ↓
[Home Page]
    ↓
Select "Create from TXT" ────→ [Navigation Sidebar]
    ↓
Upload TXT file
    ↓
Preview content
    ↓
Configure settings:
  - Deck name
  - Description
  - Source language
  - Target language
    ↓
Click "Generate"
    ↓
[Processing Phase]
  - Create deck
  - Translate each line
  - Create flashcards
  - Show progress
    ↓
[Completion]
  - Show success message
  - Display balloons
  - Show summary stats
    ↓
Option: Create another ───→ Reset and start over
    or
Exit app
```

---

## 💡 Design Principles

1. **Progressive Disclosure**
   - Show simple interface first
   - Advanced options in expanders
   - Error details on demand

2. **Feedback at Every Step**
   - File upload confirmation
   - Preview before processing
   - Progress during processing
   - Success/error messages
   - Summary statistics

3. **Validation and Error Handling**
   - Client-side validation
   - Clear error messages
   - Helpful suggestions
   - Graceful degradation

4. **Responsive Layout**
   - Wide layout for more space
   - Column-based organization
   - Adapts to different screens

5. **Accessibility**
   - Emoji for visual cues
   - Clear labels and help text
   - Logical tab order
   - Color-coded messages

---

## 🎨 Color Scheme

Streamlit's default theme with semantic colors:

- **Blue (Info):** ℹ️ Informational messages
- **Green (Success):** ✅ Successful operations
- **Red (Error):** ❌ Errors and failures
- **Yellow (Warning):** ⚠️ Warnings and cautions
- **Gray:** Disabled/placeholder elements

---

## 📱 Responsive Behavior

The app adapts to different screen sizes:

- **Desktop (>1024px):** Wide layout with sidebar
- **Tablet (768-1024px):** Columns stack vertically
- **Mobile (<768px):** Full-width, vertical layout

All pages are mobile-friendly!
