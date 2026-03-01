# DIY Birdy App
DIY Birdy is a self-hosted language learning application. It is built around flashcard decks and provides a set of exercises for practicing vocabulary, translation, pronunciation, and listening.

> The project source is publicly visible but is not open for contributions or reuse. An open-source license will be decided upon before public release.

## Installation
### Local development
Local development is possible, but requires some setup. The frontend and backend are separate applications that need to be run concurrently. The backend uses a TinkerPop graph database, which can be set up using Docker.

> A detailed local development guide will be provided in the future.

### Digital Ocean App Platform
The application can be deployed to Digital Ocean App Platform using the provided `do-app.yaml` configuration file. This will set up the frontend and backend as separate services, along with a managed graph database.

## Built-in features

### Flashcard decks
Users can create, edit, and delete flashcard decks. Each flashcard supports multiple content types: text, image, audio, and video. Concept types such as translation, pronunciation, and romanization are also supported.

### Exercises
Exercises are dynamically generated from flashcard deck content. The following exercise types are available:

**Flashcard-based**
- Review flashcard (Again / Hard / Good / Easy rating)
- Sort flashcard (know / don't know)
- Select flashcard (multiple choice)
- Tap the pairs (multiple choice)
- Write flashcard

**Pronunciation and listening**
- Pronounce text
- Listen and select

## Extended features
Tools are used to extend the built-in features. These are written as standalone applications that interact with the main application through a defined API. Examples of extended features include:
- Flashcard import (incl. txt, csv, ANKI)
- Language management tool
- Localization tool