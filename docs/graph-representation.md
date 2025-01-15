# Data Representation
This document describes the data representation used for the Tinkerpop graph. 

## Concepts
### Language
```mermaid
graph LR
V1(Language)
```

Properties:
- id: string
- name: string
- abbreviation: string

### TextToSpeechConfig
```mermaid
graph LR
Language(Language)
TextToSpeechConfig(TextToSpeechConfig)

TextToSpeechConfig--hasLanguage-->Language
```

Properties:
- id: string
- languageCode: string
- voiceName: string

Represents the configuration for Google Text-to-Speech API.

### Word
```mermaid
graph LR
V1(Word)
V2(TextContent)
V3(TextContent)
V4(Language)
V1--hasExample-->V2
V1--hasExample-->V3
V1--hasMainExample-->V3
V1--hasLanguage-->V4
```

Properties:
- id: string
- value: string

### Pronunciation
```mermaid
graph LR
TextContent(TextContent)
Pronunciation1(Pronunciation)
Pronunciation2(Pronunciation)
AudioContent1(AudioContent)
AudioContent2(AudioContent)
TextContent--hasPronunciation-->Pronunciation1
TextContent--hasPronunciation-->Pronunciation2
TextContent--hasMainPronunciation-->Pronunciation2
Pronunciation1--hasAudioContent-->AudioContent1
Pronunciation2--hasAudioContent-->AudioContent2
```

Properties:
- id: string


## Content 
### Audio content
```mermaid
graph LR
V1(AudioContent)
V2(Language)
V1--hasLanguage-->V2
```

Properties:
- id: string
- url: string

## Image content
```mermaid
graph LR
V1(ImageContent)
```

Properties:
- id: string
- url: string

### Text content
```mermaid 
graph LR
V1(TextContent)
V2(Language)
V1--hasLanguage-->V2
```

Properties: 
- id: string
- text: string

### Flashcard (with text content)
```mermaid
graph LR
V1(Flashcard)
V2(TextContent)
V3(ImageContent)

V1--hasLeftContent-->V2
V1--hasRightContent-->V3
```

Properties:
- id: string

### Flashcard deck 
```mermaid
graph LR
V1(FlashcardDeck)
V2(Flashcard)
V3(Flashcard)
V1--hasFlashcard-->V2
V1--hasFlashcard-->V3
```

Properties:
- id: string
- name: string
- description: string

## Exercise
### General exercise 
```mermaid
graph TB
ExerciseSession(ExerciseSession) 
Exercise(Exercise)
ExerciseAnswer("[Any]")

Exercise--hasSession-->ExerciseSession
Exercise--hasAnswer-->ExerciseAnswer
```

Exercise properties:
- id: string
- type: string

All exercise may have none or multiple answers. 
All writing exercises will have TextContent answers. All TextContent answers will have a Language defined by the exercise type. 

### Flashcard select exercise
```mermaid
graph TB
Exercise(Exercise)
Flashcard1(Flashcard)
Flashcard2(Flashcard)
Flashcard3(Flashcard)

Exercise--hasContent-->Flashcard1
Exercise--hasAnswer-->Flashcard2
Exercise--hasOption-->Flashcard2
Exercise--hasOption-->Flashcard3
```
Note, the content is also an option, the correct option. 

Additional exercise properties: 
- flashcardSide: string

### Flashcard review exercise
```mermaid
graph TB
Exercise(Exercise)
Flashcard(Flashcard)
RecognizabilityRating(RecognizabilityRating)

Exercise--hasContent-->Flashcard
Exercise--hasAnswer-->RecognizabilityRating
```

Additional exercise properties: 
- flashcardSide: string

### Flashcard write exercise 
```mermaid
graph TB
Exercise(Exercise)
Flashcard(Flashcard)
TextContent1(TextContent)
TextContent2A(TextContent)
TextContent2B(TextContent)
Language1(Language)
Language2(Language)

Exercise--hasContent-->Flashcard
Flashcard--hasLeftContent-->TextContent2A
Flashcard--hasRightContent-->TextContent2B
TextContent2A--hasLanguage-->Language1
TextContent2B--hasLanguage-->Language2
Exercise--hasAnswer-->TextContent1
TextContent1--hasLanguage-->Language1
```
Note, the answer will either have the same language as either side of the flashcard's content. 

Additional exercise properties: 
- flashcardSide: string

### Write sentence using word 
```mermaid
graph TB
Exercise(Exercise)
TextContent1(TextContent)
TextContent2(TextContent)
Language(Language)

Exercise--hasContent-->TextContent1
Exercise--hasAnswer-->TextContent2
TextContent1--hasLanguage-->Language
TextContent2--hasLanguage-->Language
```

Additional exercise properties: none

### Write translated sentence 
```mermaid
graph TB
Exercise(Exercise)
TextContent1(TextContent)
TextContent2(TextContent)
Language1(Language)
Language2(Language)

Exercise--hasContent-->TextContent1
TextContent1--hasLanguage-->Language1
Exercise--hasAnswer-->TextContent2
TextContent2--hasLanguage-->Language2
Exercise--hasTargetLanguage-->Language2
```

Additional exercise properties: none
