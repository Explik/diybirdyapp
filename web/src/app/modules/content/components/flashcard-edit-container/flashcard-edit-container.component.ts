import { Component, EventEmitter, Input, Output } from '@angular/core';
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
import { LabelComponent } from "../../../../shared/components/label/label.component";
import { FormFieldComponent } from "../../../../shared/components/form-field/form-field.component";
import { SelectComponent } from "../../../../shared/components/select/select.component";
import { OptionComponent } from "../../../../shared/components/option/option.component";
import { ButtonComponent } from "../../../../shared/components/button/button.component";

@Component({
    selector: 'app-flashcard-edit-container',
    standalone: true,
    templateUrl: './flashcard-edit-container.component.html',
    styleUrl: './flashcard-edit-container.component.css',
    imports: [CommonModule, FormsModule, DragDropModule, CdkDropList, CdkDrag, FlashcardEditComponent, TextFieldComponent, AudioInputComponent, ImageInputComponent, VideoInputComponent, TextInputComponent, LabelComponent, FormFieldComponent, SelectComponent, OptionComponent, ButtonComponent]
})
export class FlashcardEditContainerComponent {
  @Input() flashcardDeck: EditFlashcardDeckImpl | undefined = undefined;
  @Input() flashcardLanguages: EditFlashcardLanguageImpl[] = [];
  
  @Output() saveFlashcards = new EventEmitter<void>();

  currentDragIndex: number | undefined = undefined;
  frontLanguageId: string = '';
  backLanguageId: string = '';

  ngOnChanges(): void {
    if (this.flashcardDeck) {
      // Initialize global language selectors with most common values
      // Do this before adding default flashcards to avoid undefined issues
      this.initializeGlobalLanguages();
      
      // Add default flashcards if none exist to avoid empty state
      if (!this.flashcardDeck.flashcards.length) {
        this.handleAddFlashcard();
      }
    }
  }
  
  private initializeGlobalLanguages(): void {
    // Set the initial values for the global language selectors
    this.frontLanguageId = this.getMostCommonLanguage('left') || '';
    this.backLanguageId = this.getMostCommonLanguage('right') || '';
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
    // Use the globally selected languages if available
    if (this.frontLanguageId && newFlashcard.leftTextContent) {
      newFlashcard.leftTextContent.languageId = this.frontLanguageId;
    }
    // Fall back to most common language if global selection is empty
    else {
      const leftLanguageId = this.getMostCommonLanguage('left');
      if (leftLanguageId && newFlashcard.leftTextContent) {
        newFlashcard.leftTextContent.languageId = leftLanguageId;
      }
    }

    // Use the globally selected languages if available
    if (this.backLanguageId && newFlashcard.rightTextContent) {
      newFlashcard.rightTextContent.languageId = this.backLanguageId;
    }
    // Fall back to most common language if global selection is empty
    else {
      const rightLanguageId = this.getMostCommonLanguage('right');
      if (rightLanguageId && newFlashcard.rightTextContent) {
        newFlashcard.rightTextContent.languageId = rightLanguageId;
      }
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

  handleGlobalLeftLanguageChange(selectedLanguageId: string): void {
    const selectedLanguage = this.flashcardLanguages.find(l => l.id === selectedLanguageId);
    if (!selectedLanguage || !this.flashcardDeck?.flashcards?.length) 
      return;

    // Apply to all flashcards with left text content
    for(let flashcard of this.flashcardDeck.flashcards) {
      if (flashcard.state !== 'deleted' && 
          flashcard.leftContentType === 'text' && 
          flashcard.leftTextContent) {
        flashcard.leftTextContent.languageId = selectedLanguage.id;
      }
    }
  }

  handleGlobalRightLanguageChange(selectedLanguageId: string): void {
    const selectedLanguage = this.flashcardLanguages.find(l => l.id === selectedLanguageId);
    if (!selectedLanguage || !this.flashcardDeck?.flashcards?.length) 
      return;

    // Apply to all flashcards with right text content
    for(let flashcard of this.flashcardDeck.flashcards) {
      if (flashcard.state !== 'deleted' && 
          flashcard.rightContentType === 'text' && 
          flashcard.rightTextContent) {
        flashcard.rightTextContent.languageId = selectedLanguage.id;
      }
    }
  }
  
  // Keep these methods for backward compatibility with any existing code that might be using them
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
