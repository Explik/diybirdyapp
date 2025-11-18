"""
Example: Working with Local Deck Storage

This script demonstrates how to:
1. Create a deck locally
2. Add flashcards
3. Add pronunciation audio
4. Create a ZIP archive
5. Upload to server (optional)
"""

import sys
from pathlib import Path

# Add parent directory to path
sys.path.append(str(Path(__file__).parent.parent))
sys.path.append(str(Path(__file__).parent))

from import_client import (
    create_local_flashcard_deck,
    add_local_text_flashcard,
    add_local_pronunciation,
    create_deck_zip,
    get_local_deck_data,
    upload_local_deck,
    get_language_by_name,
    get_languages
)

def main():
    print("=" * 60)
    print("Local Deck Storage Example")
    print("=" * 60)
    
    # Step 1: Get languages
    print("\n1. Fetching available languages...")
    try:
        languages = get_languages()
        english = get_language_by_name("English")
        spanish = get_language_by_name("Spanish")
        print(f"   ✓ Found {len(languages)} languages")
        print(f"   ✓ English: {english.name} ({english.isoCode})")
        print(f"   ✓ Spanish: {spanish.name} ({spanish.isoCode})")
    except Exception as e:
        print(f"   ✗ Error: {e}")
        print("   Make sure the API server is running on http://localhost:8080")
        return
    
    # Step 2: Create a deck locally
    print("\n2. Creating deck locally...")
    deck_metadata = create_local_flashcard_deck(
        name="Example Deck",
        description="A sample deck created with local storage"
    )
    print(f"   ✓ Deck created at: {deck_metadata['deck_dir']}")
    
    # Step 3: Add flashcards
    print("\n3. Adding flashcards...")
    flashcards = [
        ("Hello", "Hola"),
        ("Goodbye", "Adiós"),
        ("Thank you", "Gracias"),
        ("How are you?", "¿Cómo estás?"),
        ("Good morning", "Buenos días")
    ]
    
    for idx, (english_text, spanish_text) in enumerate(flashcards):
        flashcard = add_local_text_flashcard(
            deck_metadata=deck_metadata,
            deck_order=idx,
            front_language=english,
            back_language=spanish,
            front_text=english_text,
            back_text=spanish_text
        )
        print(f"   ✓ Added: {english_text} → {spanish_text}")
    
    # Step 4: View deck data
    print("\n4. Viewing deck data...")
    deck_data = get_local_deck_data(deck_metadata)
    print(f"   ✓ Deck name: {deck_data['name']}")
    print(f"   ✓ Description: {deck_data['description']}")
    print(f"   ✓ Flashcard count: {len(deck_data['flashcards'])}")
    
    # Step 5: Create ZIP archive
    print("\n5. Creating ZIP archive...")
    zip_path = create_deck_zip(deck_metadata)
    print(f"   ✓ ZIP created at: {zip_path}")
    
    # Step 6: (Optional) Upload to server
    print("\n6. Upload to server (optional)")
    print("   To upload this deck to the server, uncomment the following code:")
    print("   # server_deck = upload_local_deck(deck_metadata)")
    print("   # print(f'   ✓ Uploaded! Server ID: {server_deck.id}')")
    
    # Example of adding pronunciation (commented out as it requires audio files)
    print("\n7. Adding pronunciation (example - requires audio files)")
    print("   To add pronunciation audio:")
    print("   # media_path = add_local_pronunciation(")
    print("   #     deck_metadata=deck_metadata,")
    print("   #     flashcard_id='flashcard-1',")
    print("   #     side='front',")
    print("   #     audio_file='path/to/audio.mp3'")
    print("   # )")
    
    print("\n" + "=" * 60)
    print("Example completed successfully!")
    print("=" * 60)
    print(f"\nYour deck is saved at: {deck_metadata['deck_dir']}")
    print(f"ZIP file location: {zip_path}")
    print("\nYou can now:")
    print("- Add pronunciation audio to flashcards")
    print("- Review the data.json file")
    print("- Upload to the server when ready")
    print("=" * 60)

if __name__ == "__main__":
    main()
