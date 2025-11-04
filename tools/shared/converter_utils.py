def create_language_query(name = None, abbreviation = None): 
    buffer = dict()

    if name:
        buffer["name"] = name
    if abbreviation:
        buffer["abbreviation"] = abbreviation

    return buffer

def create_text_content(text, language):
    return {
        "type": "text",
        "text": text,
        "languageId": language["id"]
    }

def create_image_content(image_url):
    return {
        "type": "image",
        "imageUrl": image_url
    }

def create_image_content_upload(image_file_name):
    return {
        "type": "image-upload",
        "imageFileName": image_file_name
    } 

def create_audio_content(audio_url, language):
    return {
        "type": "audio",
        "audioUrl": audio_url,
        "language": language
    }

def create_audio_content_upload(audio_file_name, language):
    return {
        "type": "audio-upload",
        "audioFileName": audio_file_name,
        "language": language
    }

def create_video_content(video_url):
    return {
        "type": "video",
        "videoUrl": video_url
    }

def create_video_content_upload(video_file_name):
    return {
        "type": "video-upload",
        "videoFileName": video_file_name
    }

def create_flashcard(front_content, back_content):
    return {
        "frontContent": front_content,
        "backContent": back_content
    }

def create_text_flashcard(front_text, back_text, front_language, back_language):
    return create_flashcard(
        create_text_content(front_text, front_language),
        create_text_content(back_text, back_language)
    )

def create_flashcard_deck(name, description, flashcards = []):
    return {
        "name": name,
        "description": description,
        "flashcards": flashcards
    }