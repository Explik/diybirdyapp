# Quick Reference: Local Deck Storage

## Import Required Functions

```python
from shared.import_client import (
    # Local storage functions
    create_local_flashcard_deck,
    add_local_text_flashcard,
    add_local_pronunciation,
    get_local_deck_data,
    create_deck_zip,
    list_local_decks,
    upload_local_deck,
    
    # Language functions
    get_languages,
    get_language_by_name,
    get_language_by_abbrevation,
)
```

## Quick Start

```python
# 1. Create deck
deck = create_local_flashcard_deck("My Deck", "Description")

# 2. Get languages
english = get_language_by_name("English")
spanish = get_language_by_name("Spanish")

# 3. Add flashcards
add_local_text_flashcard(
    deck_metadata=deck,
    deck_order=0,
    front_language=english,
    back_language=spanish,
    front_text="Hello",
    back_text="Hola"
)

# 4. Create ZIP
zip_path = create_deck_zip(deck)

# 5. Upload (optional)
server_deck = upload_local_deck(deck)
```

## Common Patterns

### Create Deck from List

```python
words = [
    ("Hello", "Hola"),
    ("Goodbye", "Adiós"),
    ("Thank you", "Gracias")
]

deck = create_local_flashcard_deck("Spanish Basics")
english = get_language_by_name("English")
spanish = get_language_by_name("Spanish")

for idx, (eng, spa) in enumerate(words):
    add_local_text_flashcard(deck, idx, english, spanish, eng, spa)

create_deck_zip(deck)
```

### Add Pronunciation from TTS

```python
from shared.google_api import text_to_speech

# Generate audio file
audio_file = text_to_speech("Hello", "en")

# Add to flashcard
add_local_pronunciation(
    deck_metadata=deck,
    flashcard_id="flashcard-1",
    side="front",
    audio_file=audio_file
)
```

### Process Multiple Decks

```python
decks = list_local_decks()

for deck_info in decks:
    print(f"Name: {deck_info['name']}")
    print(f"Cards: {deck_info['flashcard_count']}")
    print(f"Path: {deck_info['deck_dir']}")
```

### Inspect Deck Data

```python
data = get_local_deck_data(deck)

print(f"Deck: {data['name']}")
for card in data['flashcards']:
    front = card['frontContent']['text']
    back = card['backContent']['text']
    print(f"  {front} → {back}")
```

## File Paths

```
deck_storage/                    # Default storage directory
  My_Deck/                       # Deck directory
    data.json                    # Deck metadata
    media/                       # Media files
      audio-1.mp3
      audio-2.mp3
  My_Deck.zip                    # ZIP archive
```

## Session State (Streamlit)

```python
# Initialize
if 'deck_metadata' not in st.session_state:
    st.session_state.deck_metadata = None
if 'deck_uploaded' not in st.session_state:
    st.session_state.deck_uploaded = False

# Store deck
st.session_state.deck_metadata = create_local_flashcard_deck(...)

# Check upload status
if not st.session_state.deck_uploaded:
    # Show upload button
    pass
```

## Error Handling

```python
try:
    deck = create_local_flashcard_deck(name, description)
except Exception as e:
    print(f"Error creating deck: {e}")

try:
    server_deck = upload_local_deck(deck)
except Exception as e:
    print(f"Error uploading: {e}")
    # Deck is still saved locally
```

## Tips

1. **Always check language availability** before creating flashcards
2. **Store deck_metadata** in session state for multi-step processes
3. **Create ZIP** after all flashcards are added
4. **Upload is optional** - decks work offline
5. **Media files** must be in the `media/` subdirectory

## Common Issues

### Language not found
```python
# Check available languages first
languages = get_languages()
for lang in languages:
    print(f"{lang.name} ({lang.abbreviation})")
```

### Flashcard ID mismatch
```python
# IDs are auto-generated: flashcard-1, flashcard-2, etc.
# Get from deck data:
data = get_local_deck_data(deck)
flashcard_id = data['flashcards'][0]['id']
```

### Media file not in ZIP
```python
# Ensure file is in media/ subdirectory
import shutil
shutil.copy("audio.mp3", f"{deck['media_dir']}/audio.mp3")
```

## API Quick Lookup

| Function | Purpose | Returns |
|----------|---------|---------|
| `create_local_flashcard_deck(name, desc)` | Create deck | deck_metadata dict |
| `add_local_text_flashcard(deck, order, lang1, lang2, text1, text2)` | Add flashcard | flashcard dict |
| `add_local_pronunciation(deck, id, side, file)` | Add audio | media path string |
| `get_local_deck_data(deck)` | Load deck | deck data dict |
| `create_deck_zip(deck, path?)` | Create ZIP | ZIP path string |
| `list_local_decks()` | List all decks | list of deck info |
| `upload_local_deck(deck)` | Upload to server | server deck object |

## See Also

- `LOCAL_STORAGE_GUIDE.md` - Complete documentation
- `IMPLEMENTATION_SUMMARY.md` - Technical details
- `example_local_storage.py` - Working example
- `README.md` - User guide
