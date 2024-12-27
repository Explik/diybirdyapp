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
Exercise(Exercise)
ExerciseSession(ExerciseSession) 
ExerciseAnswer(?????)

Exercise--hasSession-->ExerciseSession
Exercise--hasAnswer-->ExerciseAnswer
```

Properties:
- id: string
- type: string

### Flashcard select exercise
```mermaid
graph BT
V1(Exercise)
V2(Flashcard)
V3(ExerciseAnswer Multiple Choice Text)

V1--hasContent-->V2
V3--hasExercise-->V1
```

Additional properties: none

### Flashcard review exercise
```mermaid
graph BT
V1(Exercise)
V2(Flashcard)
V3(ExerciseAnswer Regonizability Rating)

V1--hasContent-->V2
V3--hasExercise-->V1
```

Additional properties: none

### Flashcard write exercise 
```mermaid
graph BT
V1(Exercise)
V2(Flashcard)
V3(ExerciseAnswer Text)

V1--hasContent-->V2
V3--hasExercise-->V1
```

Additional properties: none

### Write sentence using word 
```mermaid
graph BT
V1(Exercise)
V2(TextContent)
V3(ExerciseAnswer Text)

V1--hasContent-->V2
V3--hasExercise-->V1
```

Additional properties: 
- targetLanguage: string

### Write translated sentence 
```mermaid
graph BT
V1(Exercise)
V2(TextContent)
V3(ExerciseAnswer Text)

V1--hasContent-->V2
V3--hasExercise-->V1
```

Additional properties:
- targetLanguage: string

## Exercise answer
### General exercise answer
```mermaid
graph RL
V1(ExerciseAnswer)
V2(Exercise)

V1--hasExercise-->V2
```

Properties:
- id: string
- type: string

### Text exercise answer
Additional properties: none

### Multiple choice text exercise answer
Additional properties: none

### Recognizability rating exercise answer
Additional properties: none