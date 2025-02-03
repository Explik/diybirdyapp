import { CommonModule } from '@angular/common';
import { Component, HostListener, Input } from '@angular/core';

@Component({
    selector: 'app-flashcard',
    templateUrl: './flashcard.component.html',
    styleUrls: ['./flashcard.component.css'],
    standalone: true,
    imports: [CommonModule]
})
export class FlashcardComponent {
    @Input() side: 'front' | 'back' = 'front';
    @Input() isFlippable: boolean = true;

    flip() {
        if (this.isFlippable)
            this.side = (this.side === 'front') ? 'back' : 'front';
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