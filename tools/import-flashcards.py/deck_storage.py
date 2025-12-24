"""
Deck Storage Module
Handles local storage of flashcard decks in the internal representation format.
Format: ZIP file containing data.json and media files.
"""

import json
import os
import zipfile
from pathlib import Path
from typing import List, Dict, Optional, Any
import shutil


class DeckStorage:
    """Manages local storage of flashcard decks before upload."""
    
    def __init__(self, storage_dir: str = None):
        """
        Initialize the deck storage manager.
        
        Args:
            storage_dir: Directory to store deck files. Defaults to './deck_storage'
        """
        if storage_dir is None:
            # Use a directory in the current working directory
            storage_dir = os.path.join(os.getcwd(), "deck_storage")
        
        self.storage_dir = Path(storage_dir)
        self.storage_dir.mkdir(parents=True, exist_ok=True)
    
    def create_deck(self, name: str, description: str = "") -> Dict[str, Any]:
        """
        Create a new local deck structure.
        
        Args:
            name: Name of the deck
            description: Description of the deck
            
        Returns:
            Dictionary containing deck metadata
        """
        # Create a safe folder name from the deck name
        safe_name = "".join(c for c in name if c.isalnum() or c in (' ', '-', '_')).rstrip()
        safe_name = safe_name.replace(' ', '_')
        
        # Create deck directory
        deck_dir = self.storage_dir / safe_name
        
        # Clean up existing deck if it exists to prevent duplicate media
        if deck_dir.exists():
            shutil.rmtree(deck_dir)
        
        deck_dir.mkdir(parents=True, exist_ok=True)
        
        # Create media directory
        media_dir = deck_dir / "media"
        media_dir.mkdir(exist_ok=True)
        
        # Initialize deck data
        deck_data = {
            "name": name,
            "description": description,
            "flashcards": []
        }
        
        # Save initial data.json
        data_file = deck_dir / "data.json"
        with open(data_file, 'w', encoding='utf-8') as f:
            json.dump(deck_data, f, indent=2, ensure_ascii=False)
        
        return {
            "name": name,
            "description": description,
            "deck_dir": str(deck_dir),
            "media_dir": str(media_dir)
        }
    
    def add_flashcard(
        self, 
        deck_dir: str,
        deck_order: int,
        front_content: Dict[str, Any],
        back_content: Dict[str, Any],
        flashcard_id: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Add a flashcard to a local deck.
        
        Args:
            deck_dir: Path to the deck directory
            deck_order: Order of the flashcard in the deck
            front_content: Front content dict with type and content-specific fields
                For text: {"type": "text", "text": "...", "languageId": "..."}
                For audio-upload: {"type": "audio-upload", "audioFileName": "...", "languageId": "..."}
                For image-upload: {"type": "image-upload", "imageFileName": "..."}
                For video-upload: {"type": "video-upload", "videoFileName": "...", "languageId": "..."}
            back_content: Back content dict (same format as front_content)
            flashcard_id: Optional ID for the flashcard
            
        Returns:
            The created flashcard dictionary
        """
        deck_path = Path(deck_dir)
        data_file = deck_path / "data.json"
        
        # Load existing deck data
        with open(data_file, 'r', encoding='utf-8') as f:
            deck_data = json.load(f)
        
        # Generate ID if not provided
        if flashcard_id is None:
            flashcard_id = f"flashcard-{len(deck_data['flashcards']) + 1}"
        
        # Create flashcard object
        flashcard = {
            "id": flashcard_id,
            "deckOrder": deck_order,
            "frontContent": front_content,
            "backContent": back_content
        }
        
        # Add to deck
        deck_data['flashcards'].append(flashcard)
        
        # Save updated data.json
        with open(data_file, 'w', encoding='utf-8') as f:
            json.dump(deck_data, f, indent=2, ensure_ascii=False)
        
        return flashcard
    
    def add_media_file(
        self,
        deck_dir: str,
        source_file: str,
        media_name: Optional[str] = None
    ) -> str:
        """
        Add a media file to the deck.
        
        Args:
            deck_dir: Path to the deck directory
            source_file: Path to the source media file
            media_name: Optional name for the media file. If not provided, uses source filename.
            
        Returns:
            The relative path to the media file (e.g., "media-1.mp3")
        """
        deck_path = Path(deck_dir)
        media_dir = deck_path / "media"
        
        # Determine media filename
        if media_name is None:
            media_name = Path(source_file).name
        
        # Ensure unique filename
        base_name = Path(media_name).stem
        extension = Path(media_name).suffix
        counter = 1
        final_name = media_name
        
        while (media_dir / final_name).exists():
            final_name = f"{base_name}-{counter}{extension}"
            counter += 1
        
        # Copy file to media directory
        dest_path = media_dir / final_name
        shutil.copy2(source_file, dest_path)
        
        # Return relative path
        return f"media/{final_name}"
    
    def add_pronunciation(
        self,
        deck_dir: str,
        flashcard_id: str,
        side: str,  # "front" or "back"
        audio_file: str,
        media_name: Optional[str] = None
    ) -> str:
        """
        Add pronunciation audio to a flashcard.
        
        Args:
            deck_dir: Path to the deck directory
            flashcard_id: ID of the flashcard
            side: Which side to add pronunciation to ("front" or "back")
            audio_file: Path to the audio file
            media_name: Optional name for the media file (to preserve original filename)
            
        Returns:
            The relative path to the media file
        """
        deck_path = Path(deck_dir)
        data_file = deck_path / "data.json"
        
        # Add media file with specified name to preserve extension
        media_path = self.add_media_file(deck_dir, audio_file, media_name)
        
        # Load deck data
        with open(data_file, 'r', encoding='utf-8') as f:
            deck_data = json.load(f)
        
        # Find the flashcard
        flashcard = next((fc for fc in deck_data['flashcards'] if fc['id'] == flashcard_id), None)
        
        if flashcard is None:
            raise ValueError(f"Flashcard with ID {flashcard_id} not found")
        
        # Add pronunciation to the appropriate side
        content_key = f"{side}Content"
        if content_key not in flashcard:
            raise ValueError(f"Flashcard does not have {content_key}")
        
        if "pronunciation" not in flashcard[content_key]:
            flashcard[content_key]["pronunciation"] = {}
        
        flashcard[content_key]["pronunciation"]["content"] = media_path
        
        # Save updated data.json
        with open(data_file, 'w', encoding='utf-8') as f:
            json.dump(deck_data, f, indent=2, ensure_ascii=False)
        
        return media_path
    
    def add_transcription(
        self,
        deck_dir: str,
        flashcard_id: str,
        side: str,  # "front" or "back"
        transcription: str,
        transcription_system: str
    ):
        """
        Add transcription to a text content flashcard.
        
        Args:
            deck_dir: Path to the deck directory
            flashcard_id: ID of the flashcard
            side: Which side to add transcription to ("front" or "back")
            transcription: The transcription text
            transcription_system: The transcription system (e.g., "pinyin", "romaji", "IPA")
        """
        deck_path = Path(deck_dir)
        data_file = deck_path / "data.json"
        
        # Load deck data
        with open(data_file, 'r', encoding='utf-8') as f:
            deck_data = json.load(f)
        
        # Find the flashcard
        flashcard = next((fc for fc in deck_data['flashcards'] if fc['id'] == flashcard_id), None)
        
        if flashcard is None:
            raise ValueError(f"Flashcard with ID {flashcard_id} not found")
        
        # Add transcription to the appropriate side
        content_key = f"{side}Content"
        if content_key not in flashcard:
            raise ValueError(f"Flashcard does not have {content_key}")
        
        # Validate that this is text content
        if flashcard[content_key].get("type") != "text":
            raise ValueError(f"Transcription can only be added to text content")
        
        # Add transcription
        flashcard[content_key]["transcription"] = {
            "transcription": transcription,
            "transcriptionSystem": transcription_system
        }
        
        # Save updated data.json
        with open(data_file, 'w', encoding='utf-8') as f:
            json.dump(deck_data, f, indent=2, ensure_ascii=False)

    
    def add_media_content(
        self,
        deck_dir: str,
        flashcard_id: str,
        side: str,  # "front" or "back"
        media_file: str,
        media_type: str,  # "audio", "image", or "video"
        language_id: Optional[str] = None,
        media_name: Optional[str] = None
    ) -> str:
        """
        Add media content (audio, image, or video) to a flashcard side.
        Updates the flashcard's front/back content to be of the appropriate upload type.
        
        Args:
            deck_dir: Path to the deck directory
            flashcard_id: ID of the flashcard
            side: Which side to update ("front" or "back")
            media_file: Path to the media file
            media_type: Type of media ("audio", "image", or "video")
            language_id: Optional language ID (required for audio and video)
            media_name: Optional name for the media file (to preserve original filename)
            
        Returns:
            The relative path to the media file
        """
        deck_path = Path(deck_dir)
        data_file = deck_path / "data.json"
        
        # Add media file with specified name to preserve extension
        media_path = self.add_media_file(deck_dir, media_file, media_name)
        
        # Load deck data
        with open(data_file, 'r', encoding='utf-8') as f:
            deck_data = json.load(f)
        
        # Find the flashcard
        flashcard = next((fc for fc in deck_data['flashcards'] if fc['id'] == flashcard_id), None)
        
        if flashcard is None:
            raise ValueError(f"Flashcard with ID {flashcard_id} not found")
        
        # Update the content to be media upload type
        content_key = f"{side}Content"
        
        # Get the filename from the media path
        filename = Path(media_path).name
        
        if media_type == "audio":
            flashcard[content_key] = {
                "type": "audio-upload",
                "audioFileName": filename,
                "languageId": language_id
            }
        elif media_type == "image":
            flashcard[content_key] = {
                "type": "image-upload",
                "imageFileName": filename
            }
        elif media_type == "video":
            flashcard[content_key] = {
                "type": "video-upload",
                "videoFileName": filename,
                "languageId": language_id
            }
        else:
            raise ValueError(f"Unknown media type: {media_type}")
        
        # Save updated data.json
        with open(data_file, 'w', encoding='utf-8') as f:
            json.dump(deck_data, f, indent=2, ensure_ascii=False)
        
        return media_path
    
    def create_zip(self, deck_dir: str, output_path: Optional[str] = None) -> str:
        """
        Create a ZIP file from a deck directory.
        
        Args:
            deck_dir: Path to the deck directory
            output_path: Optional path for the output ZIP file
            
        Returns:
            Path to the created ZIP file
        """
        deck_path = Path(deck_dir)
        
        if output_path is None:
            output_path = deck_path.parent / f"{deck_path.name}.zip"
        
        output_path = Path(output_path)
        
        # Create ZIP file
        with zipfile.ZipFile(output_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
            # Add data.json
            data_file = deck_path / "data.json"
            zipf.write(data_file, "data.json")
            
            # Add all media files
            media_dir = deck_path / "media"
            if media_dir.exists():
                for media_file in media_dir.iterdir():
                    if media_file.is_file():
                        zipf.write(media_file, f"media/{media_file.name}")
        
        return str(output_path)
    
    def get_deck_data(self, deck_dir: str) -> Dict[str, Any]:
        """
        Load and return the deck data from a local deck.
        
        Args:
            deck_dir: Path to the deck directory
            
        Returns:
            Dictionary containing the deck data
        """
        deck_path = Path(deck_dir)
        data_file = deck_path / "data.json"
        
        with open(data_file, 'r', encoding='utf-8') as f:
            return json.load(f)
    
    def list_decks(self) -> List[Dict[str, Any]]:
        """
        List all local decks in the storage directory.
        
        Returns:
            List of deck metadata dictionaries with:
            - name: deck name
            - description: deck description
            - flashcard_count: number of flashcards
            - deck_dir: path to deck directory
            - data: the full deck data (for convenience)
        """
        decks = []
        
        for deck_dir in self.storage_dir.iterdir():
            if deck_dir.is_dir():
                data_file = deck_dir / "data.json"
                if data_file.exists():
                    with open(data_file, 'r', encoding='utf-8') as f:
                        deck_data = json.load(f)
                        decks.append({
                            "name": deck_data.get("name", deck_dir.name),
                            "description": deck_data.get("description", ""),
                            "flashcard_count": len(deck_data.get("flashcards", [])),
                            "deck_dir": str(deck_dir),
                            "data": deck_data
                        })
        
        return decks
    
    def delete_deck(self, deck_dir: str):
        """
        Delete a local deck directory.
        
        Args:
            deck_dir: Path to the deck directory
        """
        deck_path = Path(deck_dir)
        if deck_path.exists():
            shutil.rmtree(deck_path)
