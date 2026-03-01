# Multi-stage tap pairs exercise specification

## Overview

The multi-stage tap pairs exercise presents the user with a set of option pairs and asks them to match left options to right options. Unlike the standard tap-the-pairs exercise where all pairs are evaluated simultaneously, this exercise is continuous: each correctly matched pair is immediately replaced with a new pair, up to a maximum of 10 matched pairs. The frontend never determines whether a pair is correct — all evaluation is delegated to the backend.

---

## Functionality

### Initial state
- The exercise begins with at least 5 visible option pairs (i.e. 5 left options and 5 right options, shuffled independently in their respective columns).
- `answeredCount` starts at `0`.
- `maxPairs` is always `10`.

### Selecting a pair
1. The user taps one option from the left column. It becomes selected (highlighted).
2. The user taps one option from the right column. It becomes selected (highlighted).
3. When both a left and right option are selected, the pair is automatically submitted to the backend.
4. While the submission is in-flight, both selected buttons are disabled to prevent double-submission.

### On correct answer
1. The matched pair receives a brief correct visual state (green highlight).
2. The matched pair is removed from `leftOptions` and `rightOptions`.
3. If a replacement pair is available, the server includes it via `feedback.replacementLeft` and `feedback.replacementRight`.
4. `answeredCount` is incremented by 1.
5. The visual state resets to the updated list.

### On incorrect answer
1. The selected pair receives a brief incorrect visual state (red highlight).
2. The options remain in the list unchanged.
3. `answeredCount` is not incremented.
4. After the brief highlight clears, both selected options are deselected and the user may try again.

### Completion
The exercise advances to the next exercise under either of two conditions:
1. **Max pairs reached:** `answeredCount` reaches `maxPairs` (10). No further pairs are generated; remaining visible options are permanently disabled.
2. **No replacement available:** A correct answer is given but the server has no replacement pair to offer (`feedback.replacementLeft` and `feedback.replacementRight` are both absent). This can occur when all deck pairs have been exhausted before `maxPairs`.

In both cases the container waits ~800 ms after the correct-answer flash before triggering next-exercise navigation.

### Constraints
- Tapping a left option while a different left option is already selected replaces the selection.
- Tapping a right option while a different right option is already selected replaces the selection.
- Disabled options (e.g. during submission) cannot be tapped.

---

## Data model

### Exercise input (`ExerciseInputMultiStagePairOptionsDto`)

| Field | Type | Description |
|---|---|---|
| `type` | `"multi-stage-pair-options"` | Discriminator value |
| `sessionId` | `string` | Current session identifier |
| `leftOptionType` | `"text"` | Type of left options |
| `rightOptionType` | `"text"` | Type of right options |
| `leftOptions` | `PairOptionsInputOptionDto[]` | Currently visible left options (shuffled) |
| `rightOptions` | `PairOptionsInputOptionDto[]` | Currently visible right options (shuffled) |
| `answeredCount` | `number` | Number of correctly matched pairs so far |
| `maxPairs` | `number` | Maximum pairs to be answered (always `10`) |
| `feedback` | `MultiStagePairOptionsFeedback` &#124; `undefined` | Feedback for the most recent submission |

### Feedback object (`MultiStagePairOptionsFeedback`)

| Field | Type | Description |
|---|---|---|
| `correctPairs` | `{ leftId: string, rightId: string }[]` | Pairs confirmed correct in the latest round |
| `incorrectPairs` | `{ leftId: string, rightId: string }[]` | Pairs confirmed incorrect in the latest round |
| `replacementLeft` | `MultiStagePairOptionsOption \| undefined` | New left option to slot in where the matched pair was (absent when no replacement is available) |
| `replacementRight` | `MultiStagePairOptionsOption \| undefined` | New right option to slot in where the matched pair was (absent when no replacement is available) |

### Answer submission payload

```json
{
  "type": "pair-options",
  "sessionId": "<sessionId>",
  "selectedPair": {
    "leftId": "<leftOptionId>",
    "rightId": "<rightOptionId>"
  }
}
```

---

## UI components

### Exercise container: `MultiStageTapPairsContainerComponent`
- Hosts the instruction and the input component.
- Subscribes to the exercise service for the current `ExerciseInputMultiStagePairOptionsDto`.
- On answer submission event from the input component, calls the exercise service to submit the answer and patches the local input with the response.
- Triggers exercise completion (after ~800 ms) when either:
  - `answeredCount >= maxPairs`, **or**
  - The feedback reports a correct pair but carries no `replacementLeft`/`replacementRight` (no more pairs available in the deck).

### Instruction component (reused): `InstructionComponent`
- Displays the static label **"Tap the matching pairs"**.

### Input component: `ExerciseInputMultiStagePairOptionsComponent`
- Renders two columns side by side: left options and right options.
- Each option is a button with the following possible states:

| State | Trigger | Visual |
|---|---|---|
| Default | No selection | White background, light border |
| Selected | User tapped this option | Blue highlight |
| Correct | Server confirmed correct | Green highlight (brief, ~600 ms) |
| Incorrect | Server confirmed incorrect | Red highlight (brief, ~600 ms) |
| Disabled | Submission in-flight, or exercise complete | Dimmed, not interactive |

- Emits a `pairSelected` output event carrying `{ leftId, rightId }` when both sides are chosen.
- When `answeredCount >= maxPairs` and feedback is received, sets all options to the disabled state permanently.
- Option lists animate in/out when a pair is replaced (fade or slide transition).

---

## Server communication

### Loading the exercise

```
GET /exercise/{id}?sessionId={sessionId}
```

**Response:** `ExerciseDto` where `exercise.input` is an `ExerciseInputMultiStagePairOptionsDto` with `answeredCount: 0`, `maxPairs: 10`, and the initial `leftOptions` / `rightOptions` (at least 5 pairs).

---

### Submitting a pair selection

```
POST /exercise/{id}/answer/rich
Content-Type: multipart/form-data

answer (JSON blob):
{
  "type": "multi-stage-pair-options",
  "sessionId": "<sessionId>",
  "selectedPair": { "leftId": "<id>", "rightId": "<id>" }
}
```

**Correct-answer response:** `ExerciseDto` where:
- `input.feedback.correctPairs` contains the matched pair.
- `input.feedback.incorrectPairs` is empty.
- `input.leftOptions` and `input.rightOptions` are empty (the client manages the visible list locally).
- `input.feedback.replacementLeft` and `input.feedback.replacementRight` contain a new pair to display, if one is available; both fields are absent when the deck has no more unused pairs.
- `input.answeredCount` is incremented by 1.

**Incorrect-answer response:** `ExerciseDto` where:
- `input.feedback.incorrectPairs` contains the attempted pair.
- `input.feedback.correctPairs` is empty.
- `input.leftOptions` and `input.rightOptions` are unchanged.
- `input.answeredCount` is unchanged.
- No new pair is appended.

**Completion response** — triggered by either of two conditions:
- *Max pairs reached:* `input.answeredCount === input.maxPairs`. Remaining visible options become permanently disabled.
- *No more pairs:* `input.feedback.replacementLeft` and `input.feedback.replacementRight` are both absent on a correct-answer response, indicating the deck has no unused pairs left.

In both cases the container waits ~800 ms after the correct-answer flash, then triggers next-exercise navigation.

---

## Communication sequence

```
User taps left option
  → frontend: highlights left option

User taps right option
  → frontend: highlights right option, disables both

Frontend: POST /exercise/{id}/answer/rich  { selectedPair }
  → backend: evaluates pair, updates answer state

Backend: 200 OK  { ExerciseDto with updated input + feedback }

If correct:
  → frontend: green flash on pair → removes pair
  → if feedback carries replacementLeft/replacementRight: slots new pair into the freed row
  → if answeredCount >= maxPairs OR no replacement present:
      frontend waits ~800 ms, then triggers exercise completion

If incorrect:
  → frontend: red flash on pair → clears selection
```
