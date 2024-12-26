# Data Representation
This document describes the data representation used for the Tinkerpop graph. 

## Metadata
### Language
```mermaid
graph LR
V1(Language)
```

Properties:
- id: string
- name: string
- abbreviation: string

## Content 
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
V3(TextContent)

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

Properties:
- id: string
- type: string

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
TextContent(TextContent)

Exercise--hasContent-->Flashcard
Exercise--hasAnswer-->TextContent
``` 

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
