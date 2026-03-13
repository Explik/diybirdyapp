# Learn session specification
## Session options 
The learning session options are grouped according to the exercise type. Some options are shared across all exercise types, while others are specific to certain exercise types. Below is a breakdown of the options. Exercise type options are presented as collapsable sections. Each section has a header containing the exercise name and an enable/disable toggle. Exercise-specific options are only visible when the corresponding exercise type is enabled.

General exercise options: 
- Target language
- Shuffle flashcards
- Text to speech 
- Enable review exercises
- Enable multiple-choice exercises
  - Answer languages (multiple-choice specific)
  - Initially hide options
- Enable writing exercises
  - Answer languages (writing specific)
  - Retype correct answer 
- Enable listening exercises
- Enable pronunciation exercises

## Exercise types 
- Review exercise 
- Pair options exercise
etc. 

## Option update behavior
When learn-session options are updated through `POST /exercise-session/{id}/apply-options`:
- The updated options are stored on the session.
- Active learning content is rebuilt according to the new options.
- A new current exercise is generated immediately.
- The endpoint response returns the updated session state and the newly generated exercise.