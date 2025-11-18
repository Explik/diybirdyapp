# Data Representation
This document describes the data representation used for the Tinkerpop graph. 

## Concepts
### Language
A language represents a common group of content and concepts, like words, text, pronunciation, etc. 
```mermaid
graph LR
V1(Language)
```

Properties:
- id: string
- name: string
- isoCode: string

### Word
A word represents an entry in a language's vocabulary in the system. 
The systems usage of word is analogous to a dictionary entry. 
A word may have multiple spellings, where each spelling is indicated by hasTextContent. 
A word may also have example of usage like a sentence or phrase, indicated by hasExample.  
A word may also have a pronunciation, indicated by hasAudioContent.

```mermaid
graph LR
V1(Word)
V2(TextContent)
V3(TextContent)
V4(AudioContent)
V4(Language)
V1--hasExample-->V2
V1--hasExample-->V3
V1--hasTextContent-->V3
V1--hasAudioContent-->V4
V1--hasLanguage-->V4
```

Properties: 
- id: string
- values: string[] (dictionary spellings of a word)

### Pronunciation
A pronunciation represents a single instance of a word's pronunciation in a language.
Mostly in the form of an audio file, but may also be represented as text.

Pronunciation for text content. 
```mermaid
graph LR
AudioContent(AudioContent)
TextContent(TextContent)
Pronunciation(Pronunciation)

TextContent--hasPronunciation-->Pronunciation
Pronunciation--hasAudioContent-->TextContent
Pronunciation--hasTextContent-->TextContent
```

Properties:
- id: string

## Configuration 
### TextToSpeechConfig
Represents the configuration for Google Text-to-speech API.

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

### SpeechToTextConfig
Represents the configuration for Google Speech-to-text API.

```mermaid
graph LR
Language(Language)
SpeechToTextConfig(SpeechToTextConfig)

SpeechToTextConfig--hasLanguage-->Language
```

Properties:
- id: string
- languageCode: string

## Basic Content 
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

### Image content
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

### Video content
```mermaid
graph LR
V1(VideoContent)
V2(Language)
V1--hasLanguage-->V2
```

Properties:
- id: string
- url: string

## Flashcard content
### Flashcard
A flashcard will always have both left and right content, however, they may be of different types.
```mermaid
graph LR
V1(Flashcard)
V2(<T> Content)
V3(<U> Content)

V1--hasLeftContent-->V2
V1--hasRightContent-->V3
```

Flashcard properties:
- id: string

Allowed content types are:
- AudioContent
- ImageContent
- TextContent
- VideoContent

### Flashcard deck 
A flashcard may contain 0, 1, or more flashcards.
Each flashcard is ordered according to the order property located on the hasFlashcard edge.

```mermaid
graph LR
V1(FlashcardDeck)
V2(Flashcard)
V3(Flashcard)
V1--hasFlashcard-->V2
V1--hasFlashcard-->V3
```

Flashcard deck properties:
- id: string
- name: string
- description: string

hasFlashcard properties:
- order: integer

## Exercise content
This section describes the components of an exercise. 
It first describes the overall relationship between exercises, exercise answers, and exercise sessions. 
Then it describes the different available exercise contents and inputs. 

Documentation for each of specific exercise type is provided in exercise-types.md.

### Exercise, ExerciseAnswer, ExerciseSession
This section describes the overall relationship between exercises, exercise answers, and exercise sessions.
```mermaid
graph TB
Exercise(Exercise)
ExerciseAnswer(ExerciseAnswer)
ExerciseSession(ExerciseSession)

Exercise--hasSession-->ExerciseSession
ExerciseAnswer--hasSession-->ExerciseSession
ExerciseAnswer--hasExercise-->Exercise
```

Exercise properties:
- id: string
- type: string

Exercise answer properties:
- id: string

Exercise session properties:
- id: string

### Exercise with Audio/Image/Text/Video Content
```mermaid 
graph TB
Exercise(Exercise)
AnyContent(<T> Content)

Exercise--hasContent-->AnyContent
```

Exercise can by default have any basic content type. 
However, some exercise types may restrict the allowed content types.

### Exercise with Flashcard (Side) Content 
```mermaid
graph TB
Exercise(Exercise)
Flashcard(Flashcard)

Exercise--hasContent-->Flashcard
```

The flashcard content can exist in two forms: 
- A regular flashcard (two-sided) is indicated by the hasContent edge (without any flashcardSide attribute).
- A flashcard side (one-sided) is indicated by the flashcardSide property on the hasContent edge.

### Exercise with Select Options Input
This section describes the graph representation for the select-option input.

```mermaid
graph TB
Exercise(Exercise)
ExerciseAnswer(ExerciseAnswer)
AnyContent1(<T> Content)
AnyContent2(<T> Content)
AnyContent3(<T> Content)
AnyContent4(<T> Content)

Exercise--hasCorrectOption-->AnyContent1
Exercise--hasCorrectOption-->AnyContent2
Exercise--hasOption-->AnyContent3
Exercise--hasIncorrectOption-->AnyContent4
Exercise--hasAnswer-->ExerciseAnswer
ExerciseAnswer--hasSelectedOption-->AnyContent1
```
The allowed option types are: 
- AudioContent
- TextContent
- ImageContent

All options must be of the same type. Each option can either be correct, incorrect and undecided (indicated by hasOption). 

### Exercise with Recognizability Rating Input
This section describes the graph representation for the recognizability rating input.
```mermaid
graph TB
Exercise(Exercise)
ExerciseAnswer(ExerciseAnswer)
RecognizabilityRating(RecognizabilityRating)
```

The available ratings are stored in code and are not represented in the graph.
The selected rating is stored in the RecognizabilityRating vertex.

### Exercise with Write Text Input
This section describes the graph representation for the write text input.
```mermaid
graph TB
Exercise(Exercise)
ExerciseAnswer(ExerciseAnswer)
TextContent1(TextContent)
TextContent2(TextContent)
TextContent3(TextContent)

Exercise--hasCorrectOption-->TextContent1
Exercise--hasIncorrectOption-->TextContent2
Exercise--hasAnswer-->ExerciseAnswer
ExerciseAnswer--hasContent-->TextContent3
```
The correct option(s) represents a clearly correct answer to the exercise. \
The incorrect option(s) represents a clearly incorrect answer to the exercise. \
The actual answer will be stored in the ExerciseAnswer vertex.

NB, it is possible for an exercise to have no correct or incorrect options.