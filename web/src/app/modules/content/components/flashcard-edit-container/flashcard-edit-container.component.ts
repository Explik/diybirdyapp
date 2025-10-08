import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule } from '@angular/common';
import { FlashcardEditComponent } from "../flashcard-edit/flashcard-edit.component";
import { TextFieldComponent } from "../../../../shared/components/text-field/text-field.component";
import { FormsModule } from '@angular/forms';
import { CdkDrag, CdkDragDrop, CdkDropList, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { EditFlashcard, EditFlashcardDeck, EditFlashcardDeckImpl, EditFlashcardImpl, EditFlashcardLanguageImpl } from '../../models/editFlashcard.model';
import { AudioInputComponent } from "../audio-input/audio-input.component";
import { ImageInputComponent } from "../image-input/image-input.component";
import { VideoInputComponent } from "../video-input/video-input.component";
import { TextInputComponent } from "../text-input/text-input.component";

@Component({
    selector: 'app-flashcard-edit-container',
    standalone: true,
    templateUrl: './flashcard-edit-container.component.html',
    styleUrl: './flashcard-edit-container.component.css',
    imports: [TextButtonComponent, CommonModule, FormsModule, DragDropModule, CdkDropList, CdkDrag, FlashcardEditComponent, TextFieldComponent, AudioInputComponent, ImageInputComponent, VideoInputComponent, TextInputComponent]
})
export class FlashcardEditContainerComponent {
  @Input() flashcardDeck: EditFlashcardDeckImpl | undefined = undefined;
  @Input() flashcardLanguages: EditFlashcardLanguageImpl[] = [];
  
  @Output() saveFlashcards = new EventEmitter<void>();

  currentDragIndex: number | undefined = undefined;



  ngOnChanges(): void {
    if (this.flashcardDeck) {
      // Add default flashcards if none exist to avoid empty state
      if (!this.flashcardDeck.flashcards.length)
        this.handleAddFlashcard()
    }
  }

  handleDragStart(index: number): void {
    this.currentDragIndex = index;
  }

  handleDragEnd(): void {
    this.currentDragIndex = undefined
  }

  handleRearrangeFlashcard(event: CdkDragDrop<Partial<EditFlashcardImpl>[]>): void {
    moveItemInArray(this.flashcardDeck!.flashcards, event.previousIndex, event.currentIndex);
    this.flashcardDeck!.flashcards
      .filter(s => s.state !== 'deleted')
      .forEach((flashcard, index) => flashcard.deckOrder = index + 1);
  }

  handleAddFlashcard() {
    var newFlashcard = EditFlashcardImpl.createDefault();
    newFlashcard.state = 'added';
    newFlashcard.deckId =  this.flashcardDeck!.id;
    newFlashcard.deckOrder = this.flashcardDeck!.flashcards.length + 1;
    
    // Apply already chosen languages from existing flashcards
    this.applyExistingLanguagesToNewFlashcard(newFlashcard);
    
    this.flashcardDeck!.flashcards.push(newFlashcard);
  }

  private applyExistingLanguagesToNewFlashcard(newFlashcard: EditFlashcardImpl): void {
    // Find the most commonly used left language
    const leftLanguageId = this.getMostCommonLanguage('left');
    if (leftLanguageId && newFlashcard.leftTextContent) {
      newFlashcard.leftTextContent.languageId = leftLanguageId;
    }

    // Find the most commonly used right language
    const rightLanguageId = this.getMostCommonLanguage('right');
    if (rightLanguageId && newFlashcard.rightTextContent) {
      newFlashcard.rightTextContent.languageId = rightLanguageId;
    }
  }

  private getMostCommonLanguage(side: 'left' | 'right'): string | undefined {
    const languageCounts = new Map<string, number>();
    
    for (const flashcard of this.flashcardDeck!.flashcards) {
      if (flashcard.state === 'deleted') continue;
      
      let languageId: string | undefined;
      if (side === 'left' && flashcard.leftContentType === 'text' && flashcard.leftTextContent) {
        languageId = flashcard.leftTextContent.languageId;
      } else if (side === 'right' && flashcard.rightContentType === 'text' && flashcard.rightTextContent) {
        languageId = flashcard.rightTextContent.languageId;
      }
      
      if (languageId && languageId !== '') {
        languageCounts.set(languageId, (languageCounts.get(languageId) || 0) + 1);
      }
    }
    
    // Return the most common language, or undefined if no languages found
    if (languageCounts.size === 0) return undefined;
    
    return Array.from(languageCounts.entries())
      .reduce((a, b) => a[1] > b[1] ? a : b)[0];
  }

  handleDeleteFlashcard(flashcard: EditFlashcardImpl): void {
    if (flashcard.state === 'added') {
      this.flashcardDeck!.flashcards = this.flashcardDeck!.flashcards.filter(s => s !== flashcard);
    }
    else flashcard.state = 'deleted';

    this.flashcardDeck!.flashcards
      .filter(s => s.state !== 'deleted')
      .forEach((flashcard, index) => flashcard.deckOrder = index + 1);
  }

  handleLeftLanguageChange(flashcard: EditFlashcardImpl, selectedLanguageId: string): void {
    const selectedLanguage = this.flashcardLanguages.find(l => l.id === selectedLanguageId);
    if (!selectedLanguage || !flashcard.leftTextContent) 
      return;

    // Update this flashcard's language
    flashcard.leftTextContent.languageId = selectedLanguage.id;

    // Apply to all flashcards with left text content
    for(let otherFlashcard of this.flashcardDeck!.flashcards) {
      if (otherFlashcard.state !== 'deleted' && 
          otherFlashcard.leftContentType === 'text' && 
          otherFlashcard.leftTextContent) {
        otherFlashcard.leftTextContent.languageId = selectedLanguage.id;
      }
    }
  }

  handleRightLanguageChange(flashcard: EditFlashcardImpl, selectedLanguageId: string): void {
    const selectedLanguage = this.flashcardLanguages.find(l => l.id === selectedLanguageId);
    if (!selectedLanguage || !flashcard.rightTextContent) 
      return;

    // Update this flashcard's language
    flashcard.rightTextContent.languageId = selectedLanguage.id;

    // Apply to all flashcards with right text content
    for(let otherFlashcard of this.flashcardDeck!.flashcards) {
      if (otherFlashcard.state !== 'deleted' && 
          otherFlashcard.rightContentType === 'text' && 
          otherFlashcard.rightTextContent) {
        otherFlashcard.rightTextContent.languageId = selectedLanguage.id;
      }
    }
  }

  handleSaveFlashcards() {
    this.saveFlashcards?.emit();
  }
}
