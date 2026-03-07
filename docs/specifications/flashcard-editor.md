# Flashcard editor specification

The flashcard editor allows users to create and edit a flashcard deck, including its deck metadata and individual flashcard content.

## Deck metadata
The deck metadata consists of name, description and global language settings. The global languages are applied to all text-type flashcard sides in the deck, but can be overridden by per-card language assignments. However, this functionality is not exposed in the normal UI and all text sides will simply follow the deck languages.

**Name**
Required text input. Shows the inline error "Title is required" when empty on a save attempt.

**Description**
Optional multi-line text input (3 rows).

**Front language**
Dropdown populated from available languages. If the deck contains any flashcards, the dropdown is pre-selected with the most common language used on front sides. Otherwise, it remains blank to prompt the user to make a selection.

Be aware, the front-language will override any per-card front language settings when saved.

**Back language**
Same behaviour as front language, applied to back sides.

Be aware, the front-language will override any per-card front language settings when saved.

## Flashcard list
- Cards are displayed in deck order, numbered sequentially (`#1`, `#2`, …).
- If the deck has no cards when loaded, one empty card is added automatically.

### Drag to reorder
- A card can be dragged by clicking and holding (mouse: immediate, touch: 50 ms delay).
- Dropping the card at a new position reorders the list and recalculates `deckOrder` for all remaining cards.

### Add card ("＋ Add flashcard" button)
- Appends a new card at the end of the list with default content types and empty content.
- The card is marked `added` internally for downstream change detection.

### Delete card ("Delete" button)
- Immediately removes the card from the list and recalculates the order of remaining cards.
- Cards that were part of the original deck are marked `deleted` on save.

## Per-card content
Each card has an independent **front (left)** side and a **back (right)** side. Each side has a **content type selector** that switches the input shown below it. Upon switching, the previous content is will be hidden, but preserved in case the user switches back. However, it will be permently lost when the user saves the deck changes.

### Content types
**Text** (default)
Single-line text input. Required — an error is shown if the field is empty on a save attempt. When selected, the label area also shows the resolved global language name if one is set.

**Audio**
Audio file input. Required — an error is shown if no file is selected.

**Image**
Image file input. Required — an error is shown if neither a file nor a URL is present.

**Video**
Video file input. Required — an error is shown if neither a file nor a URL is present.

## Save — "Save changes"
1. Marks all form controls as touched so validation errors become visible.
2. Aborts the save if the form is invalid (any required field missing or required language not selected).
3. Writes form values (`name`, `description`, global languages) back to the deck model.
4. Applies the selected global languages to every text-type card side.
5. Compares the current card set against the original snapshot to mark new cards `added` and removed cards `deleted`.
6. Emits the `saveFlashcards` output event for the parent to persist the change.

## Validation summary
**"Title is required"**
Shown when the `name` field is empty.

**"Front language required when text content exists"**
Shown when the front language is unset but at least one front-side text card has content.

**"Back language required when text content exists"**
Shown when the back language is unset but at least one back-side text card has content.

**"Front text required"** / **"Back text required"**
Shown when the content type is text and the input is blank.

**"Front image required"** / **"Back image required"**
Shown when the content type is image and neither a file has not been provided.

**"Front audio required"** / **"Back audio required"**
Shown when the content type is audio and no file has been provided or audio recorded.

**"Front video required"** / **"Back video required"**
Shown when the content type is video and no file has been provided or video recorded.