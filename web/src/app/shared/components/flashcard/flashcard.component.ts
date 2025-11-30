import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';

@Component({
    selector: 'app-flashcard',
    templateUrl: './flashcard.component.html',
    styleUrls: ['./flashcard.component.css'],
    standalone: true,
    imports: [CommonModule]
})
export class FlashcardComponent {
    @Input() side: 'front' | 'back' = 'front';
    @Output() sideChange = new EventEmitter<'front' | 'back'>();

    @Input() isFlippable: boolean = true;

    flip() {
        if (this.isFlippable) {
            this.side = (this.side === 'front') ? 'back' : 'front';
            this.sideChange.emit(this.side);
        }
    }

    @HostListener('click')
    onClick() {
        this.flip();
    }

    @HostListener('keydown.enter')
    onEnter() {
        this.flip();
    }
}