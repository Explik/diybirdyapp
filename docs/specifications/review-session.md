# Review session 
## Algorithm
The review session supports multiple underlying algorithms that determine the order and frequency of flashcard reviews. The specific algorithm used can be configured in the session options. Below is an overview of the supported algorithms.  

If the algorithm is changed, the current state will be translated into a new state that fits the new algorithm. For example, if the user switches from the simple-sort algorithm to the SM-2 algorithm, the flashcards in the "I don't know this" pile will be scheduled for review according to the SM-2 algorithm, while the flashcards in the "I know this" pile will be scheduled for review at longer intervals.

# SimpleSort algorithm
The simple-sort algorithm is a straightforward approach, where the user sorts its flashcard into two piles: "I know this" and "I don't know this". Flashcards in the "I don't know this" are reviewed periodically until the user moves them to the "I know this" pile. This algorithm is simple and easy to understand, making it suitable for users who prefer a more hands-on approach to reviewing flashcards.

When the simple-sort is selected, the exercises will all be of type "sort-flashcard-exercise", where the user can select either "I know this" or "I don't know this" for each flashcard. 

# SuperMemo 2 (SM-2) algorithm
The review session algorithm is based on the SuperMemo 2 (SM-2) algorithm, which is a widely used spaced repetition algorithm. The SM-2 algorithm calculates the optimal intervals for reviewing flashcards based on the user's performance. 

When the SM-2 algorithm is selected, the exercises will be of type "review-flashcard-exercise", where the user rates their recall of the flashcard on a scale from 0 to 5. The SM-2 algorithm uses this rating to determine when the flashcard should be reviewed again, with higher ratings leading to longer intervals between reviews.

# Session options
The review session options include:
- Algorithm 
- Initial flashcard language
- Text to speech