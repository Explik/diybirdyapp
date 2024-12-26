# Data Representation
This document describes the data representation used for the Tinkerpop graph. 

## Metadata
### Language
```mermaid
graph TB
V1(Language)
```

Properties:
- id: string
- name: string
- abbreviation: string

## Content 
### Text content
```mermaid 
graph TB
V1(TextContent)
V2(Language)
V1--hasLanguage-->V2
```

Properties: 
- id: string
- text: string

### Flashcard (with text content)
```mermaid
graph TB
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
graph BT
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
graph BT
V1(Exercise)
V2(ExerciseSession) 
V3(ExerciseAnswer)

V1--hasSession-->V2
V3--hasExercise-->V1
```

properties:
- id: string
- type: string

### Flashcard review exercise
```mermaid
graph BT
V1(Exercise)
V2(Flashcard)
V3(ExerciseAnswer Regonizability Rating)

V1--hasContent-->V2
V3--hasExercise-->V1
```

properties:
- [General exercise properties]

### Flashcard write exercise 
```mermaid
graph BT
V1(Exercise)
V2(Flashcard)
V3(ExerciseAnswer Text)

V1--hasContent-->V2
V3--hasExercise-->V1
```

properties:
- [General exercise properties]

## Exercise answer


### General exercise answer

### Text exercise answer 


