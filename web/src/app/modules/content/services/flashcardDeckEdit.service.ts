import { Injectable } from "@angular/core";
import { FlashcardService } from "./flashcard.service";
import { EditFlashcard, EditFlashcardImpl } from "../models/editFlashcard.model";

@Injectable({
    providedIn: 'root'
})
export class FlashcardDeckEditService {
    private currentFlashcards: EditFlashcardImpl[] = [];
    
    constructor(private dataService: FlashcardService) { }

    fetchFlashcardDeck(id: string) {
        this.dataService.getFlashcards(id).subscribe(flashcards => {
            this.currentFlashcards = flashcards;
        });
    }

    updateFlashcardDeck() {
        // TODO fetch all changes
    }

    getFlashcards(): EditFlashcard[] {
        return this.currentFlashcards;
    }

    addFlashcard() {
        this.currentFlashcards.push(EditFlashcardImpl.createDefault());
    }

    duplicateFlashcard(id: string) {
        const flashcard = this.currentFlashcards.find(f => f.id === id)!;
        this.currentFlashcards.push(flashcard.duplicate());
    }

    removeFlashcard(id: string) {
        this.currentFlashcards = this.currentFlashcards.filter(f => f.id !== id);
    }
}

