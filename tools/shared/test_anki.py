import sys
import unittest
from pathlib import Path

# Allow direct imports from tools/shared when running this test file standalone.
shared_dir = Path(__file__).parent
sys.path.insert(0, str(shared_dir))

from anki import AnkiDeck, AnkiCard


class TestAnkiMediaParsing(unittest.TestCase):
    def setUp(self):
        self.deck = AnkiDeck()
        self.deck.field_names = ["Front", "Back"]
        self.deck.media_map = {
            "lesson-video.mp4": "C:/tmp/lesson-video.mp4",
            "lesson-audio.mp3": "C:/tmp/lesson-audio.mp3",
        }

    def test_video_field_detected_from_sound_tag_mp4(self):
        self.deck.flashcards = [
            AnkiCard(self.deck, {"Front": "[sound:lesson-video.mp4]", "Back": "Definition"})
        ]

        video_fields = self.deck.get_video_field_names()

        self.assertEqual(video_fields, ["Front"])

    def test_media_type_sound_tag_mp4_is_video(self):
        card = AnkiCard(self.deck, {"Front": "[sound:lesson-video.mp4]", "Back": "Definition"})

        self.assertEqual(card.get_media_type("Front"), "video")

    def test_media_type_sound_tag_mp3_is_audio(self):
        card = AnkiCard(self.deck, {"Front": "[sound:lesson-audio.mp3]", "Back": "Definition"})

        self.assertEqual(card.get_media_type("Front"), "audio")


if __name__ == "__main__":
    unittest.main()
