import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormGroup } from '@angular/forms';

import { FlashcardEditContainerComponent } from './flashcard-edit-container.component';
import {
  EditFlashcardDeckImpl,
  EditFlashcardImpl,
  EditFlashcardTextImpl,
} from '../../models/editFlashcard.model';

describe('FlashcardEditContainerComponent', () => {
  let component: FlashcardEditContainerComponent;
  let fixture: ComponentFixture<FlashcardEditContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlashcardEditContainerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlashcardEditContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('syncs changed content types and media controls to the flashcard model on save', () => {
    const deck = new EditFlashcardDeckImpl();
    deck.id = 'deck-1';
    deck.name = 'Deck';
    deck.description = '';

    const flashcard = EditFlashcardImpl.createDefault();
    flashcard.id = 'flashcard-1';
    flashcard.deckId = deck.id;
    flashcard.state = 'unchanged';

    flashcard.leftTextContent = EditFlashcardTextImpl.create();
    flashcard.leftTextContent.text = 'Front';
    flashcard.leftTextContent.languageId = 'en';

    flashcard.rightTextContent = EditFlashcardTextImpl.create();
    flashcard.rightTextContent.text = 'Back';
    flashcard.rightTextContent.languageId = 'en';

    deck.flashcards = [flashcard];

    component.flashcardDeck = deck;
    component.ngOnChanges();

    const flashcardsForm = component.form.get('flashcards') as FormArray;
    const flashcardFormGroup = flashcardsForm.at(0) as FormGroup;
    const imageContent = {
      imageFile: new File(['img'], 'front.png', { type: 'image/png' })
    } as any;

    flashcardFormGroup.get('leftContentType')?.setValue('image');
    flashcardFormGroup.get('leftImageContent')?.setValue(imageContent);

    spyOn(component.saveFlashcards, 'emit');
    component.handleSaveFlashcards();

    expect(flashcard.leftContentType).toBe('image');
    expect(flashcard.leftImageContent).toBe(imageContent);
    expect(flashcard.state).toBe('updated');
    expect(component.saveFlashcards.emit).toHaveBeenCalled();
  });

  it('matches form changes to flashcards by id after reordering', () => {
    const deck = new EditFlashcardDeckImpl();
    deck.id = 'deck-1';
    deck.name = 'Deck';
    deck.description = '';

    const firstCard = EditFlashcardImpl.createDefault();
    firstCard.id = 'card-1';
    firstCard.deckId = deck.id;
    firstCard.leftTextContent = EditFlashcardTextImpl.create();
    firstCard.leftTextContent.text = 'First front';
    firstCard.leftTextContent.languageId = 'en';
    firstCard.rightTextContent = EditFlashcardTextImpl.create();
    firstCard.rightTextContent.text = 'First back';
    firstCard.rightTextContent.languageId = 'en';

    const secondCard = EditFlashcardImpl.createDefault();
    secondCard.id = 'card-2';
    secondCard.deckId = deck.id;
    secondCard.leftTextContent = EditFlashcardTextImpl.create();
    secondCard.leftTextContent.text = 'Second front';
    secondCard.leftTextContent.languageId = 'en';
    secondCard.rightTextContent = EditFlashcardTextImpl.create();
    secondCard.rightTextContent.text = 'Second back';
    secondCard.rightTextContent.languageId = 'en';

    deck.flashcards = [firstCard, secondCard];

    component.flashcardDeck = deck;
    component.ngOnChanges();

    component.handleRearrangeFlashcard({ previousIndex: 0, currentIndex: 1 } as any);

    const flashcardsForm = component.form.get('flashcards') as FormArray;
    const topFormGroup = flashcardsForm.at(0) as FormGroup;
    const audioContent = { type: 'file' } as any;

    topFormGroup.get('leftContentType')?.setValue('audio');
    topFormGroup.get('leftAudioContent')?.setValue(audioContent);

    spyOn(component.saveFlashcards, 'emit');
    component.handleSaveFlashcards();

    expect(firstCard.leftContentType).toBe('text');
    expect(secondCard.leftContentType).toBe('audio');
    expect(secondCard.leftAudioContent).toBe(audioContent);
    expect(component.saveFlashcards.emit).toHaveBeenCalled();
  });
});
