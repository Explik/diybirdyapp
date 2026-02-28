# Asynchronous Text-to-Speech Generation

## Overview
The system supports asynchronous text-to-speech (TTS) generation for TextContentVertex nodes that have a language with TTS configuration. The pronunciation audio is generated in the background and saved as a PronunciationVertex connected to an AudioContentVertex on the graph.

## Architecture

### Components

#### TextToSpeechContentCreationManager
The core manager responsible for dispatching async TTS generation tasks.

**Location**: `api/src/main/java/com/explik/diybirdyapp/manager/contentCreationManager/TextToSpeechContentCreationManager.java`

**Key Methods**:
- `dispatchTextToSpeechGeneration(TextContentVertex)` - Dispatches async TTS generation for a text content vertex (annotated with `@Async`)
- `hasTtsConfiguration(TextContentVertex)` - Checks if a text content vertex has a language with TTS configuration

**Behavior**:
1. Checks if the text content has a language with TTS configuration
2. Verifies that pronunciation doesn't already exist
3. Generates audio using the TTS service
4. Saves audio file to binary storage
5. Creates a PronunciationVertex connected to an AudioContentVertex
6. Handles errors gracefully without blocking (since it's async)

#### FlashcardDeckAssociatedContentCreationManager
Orchestrates content creation for flashcard decks, dispatching TTS generation for all text content in a batch.

**Location**: `api/src/main/java/com/explik/diybirdyapp/manager/exerciseSessionManager/LearnFlashcardDeck/FlashcardDeckAssociatedContentCreationManager.java`

**Key Methods**:
- `dispatchContentCreation(List<ContentVertex>)` - Processes a list of content vertices and dispatches TTS for TextContentVertex instances

#### ExerciseSessionManagerLearnFlashcardDeck
Integrates content creation into the learning session lifecycle.

**Location**: `api/src/main/java/com/explik/diybirdyapp/manager/exerciseSessionManager/LearnFlashcardDeck/ExerciseSessionManagerLearnFlashcardDeck.java`

**Integration Point**:
- Called during session initialization (`init` method)
- Dispatches content creation for all flashcards in the deck
- Does not wait for completion (fire-and-forget)

## Data Flow

```
ExerciseSessionManagerLearnFlashcardDeck.init()
    ↓
dispatchContentCreation(sessionVertex)
    ↓
FlashcardDeckAssociatedContentCreationManager.dispatchContentCreation(contentVertices)
    ↓ (for each TextContentVertex)
TextToSpeechContentCreationManager.dispatchTextToSpeechGeneration(textContentVertex)
    ↓ (async - no waiting)
[Check TTS Configuration] → [Generate Audio] → [Save to Storage] → [Create PronunciationVertex + AudioVertex]
```

## Graph Representation

When TTS generation completes successfully, the following vertices are created:

```
TextContentVertex
    ↓ (hasPronunciation edge)
PronunciationVertex
    ↓ (hasAudioContent edge)
AudioContentVertex
    ↓ (hasLanguage edge)
LanguageVertex
```

**Important**: The process only **adds** new vertices to the graph. It does **not** modify existing TextContentVertex properties.

## Configuration Requirements

For TTS generation to work, the language must have one of the following configurations:

1. **Google Text-to-Speech Configuration**
   - Configuration type: `google-text-to-speech`
   - Required properties: `languageCode`, `voiceName`

2. **Microsoft Text-to-Speech Configuration**
   - Configuration type: `microsoft-text-to-speech`
   - Required property: `voiceName`

The configuration is checked via `ConfigurationVertex.findByLanguageAndType()`.

## Async Execution

The TTS generation is annotated with `@Async`, which means:
- Executes in a separate thread pool
- Does not block the exercise session initialization
- Failures are logged but don't affect the session flow
- The Spring Boot application must have `@EnableAsync` annotation (already present in `DiybirdyappApplication.java`)

## Error Handling

The async process handles errors gracefully:
- If no TTS configuration exists, the process silently skips
- If pronunciation already exists, the process skips generation
- If audio generation fails, the error is logged to `System.err` without throwing
- The session continues normally regardless of TTS generation success/failure

## Example Usage

### Starting a Learn Session
```java
// User initiates a learning session
var context = ExerciseCreationContext.createForSession(sessionModel);
var sessionDto = sessionManager.init(traversalSource, context);

// Behind the scenes:
// 1. Session is created
// 2. Async TTS generation is dispatched for all flashcard text content
// 3. First exercise is generated immediately
// 4. TTS continues in background
```

### TTS Generation Process (Async)
```java
// For each TextContentVertex in the flashcard deck:
// 1. Check language has TTS config
if (hasTtsConfiguration(textContentVertex)) {
    // 2. Dispatch async generation
    dispatchTextToSpeechGeneration(textContentVertex);
    // [Async execution starts here]
    
    // 3. Generate audio
    byte[] audioData = textToSpeechService.generateAudio(textToSpeechModel);
    
    // 4. Save to storage
    binaryStorageService.set(audioFileName, audioData);
    
    // 5. Create pronunciation vertex
    createPronunciationVertexCommandHandler.handle(command);
}
```

## Testing Considerations

When testing TTS functionality:
1. Ensure the language has appropriate TTS configuration
2. Mock the async execution or use `@EnableAsync` in tests
3. Verify that PronunciationVertex and AudioContentVertex are created
4. Confirm that existing pronunciations are not duplicated
5. Test error scenarios (missing config, generation failure)

## Performance Considerations

- TTS generation happens asynchronously to avoid blocking the UI
- Multiple TTS requests can be processed in parallel
- Audio files are stored in the binary storage system
- Pronunciations are cached (not regenerated if they already exist)
- Failed TTS attempts don't retry automatically

## Future Enhancements

Potential improvements to consider:
- Batch processing of TTS requests to optimize API calls
- Priority queue for frequently accessed content
- Retry mechanism for failed TTS generation
- Progress tracking for TTS generation status
- Ability to regenerate pronunciations for specific content
