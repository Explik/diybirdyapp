"""OpenAI API integration for translation services."""

def translate_text_openai(
    text: str | list[str],
    target_language: str,
    source_language: str | None = None,
) -> list[dict]:
    """Translates text using OpenAI's ChatGPT API.
    
    Args:
        text: The text to translate. Can be a string or a list of strings.
        target_language: The ISO 639 language code to translate the text into
                         (e.g., 'en' for English, 'es' for Spanish).
        source_language: Optional. The ISO 639 language code of the input text.
                         If None, the model will auto-detect.
    
    Returns:
        A list of dictionaries containing the translation results.
        Each dict has keys: 'input', 'translatedText'
    """
    try:
        from openai import OpenAI
    except ImportError:
        raise ImportError(
            "OpenAI package not installed. Install it with: pip install openai"
        )
    
    import os
    from pathlib import Path
    
    # Load .env file if it exists
    try:
        from dotenv import load_dotenv
        # Look for .env in the locale-manager.py directory
        env_path = Path(__file__).parent.parent / "locale-manager.py" / ".env"
        if env_path.exists():
            load_dotenv(env_path)
    except ImportError:
        pass  # python-dotenv not installed, skip
    
    # Initialize OpenAI client
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        raise ValueError(
            "OPENAI_API_KEY environment variable not set. "
            "Please set it in a .env file or as an environment variable."
        )
    
    client = OpenAI(api_key=api_key)
    
    # Convert single string to list
    if isinstance(text, str):
        text = [text]
    
    results = []
    
    for input_text in text:
        # Construct prompt
        if source_language:
            prompt = (
                f"Translate the following text from {source_language} to {target_language}. "
                f"Only provide the translation, nothing else.\n\n{input_text}"
            )
        else:
            prompt = (
                f"Translate the following text to {target_language}. "
                f"Only provide the translation, nothing else.\n\n{input_text}"
            )
        
        # Call OpenAI API
        response = client.chat.completions.create(
            model="gpt-4o-mini",  # Using the smaller, faster model
            messages=[
                {
                    "role": "system",
                    "content": "You are a professional translator. Provide only the translation without any explanations or additional text."
                },
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            temperature=0.3,  # Lower temperature for more consistent translations
        )
        
        translated_text = response.choices[0].message.content.strip()
        
        results.append({
            'input': input_text,
            'translatedText': translated_text
        })
    
    return results
