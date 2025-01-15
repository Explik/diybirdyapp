import { Component } from '@angular/core';
import { FlashcardComponent } from "../../components/flashcard/flashcard.component";

@Component({
  selector: 'app-shared-components-page',
  standalone: true,
  imports: [FlashcardComponent],
  templateUrl: './shared-components-page.component.html'
})
export class SharedComponentsPageComponent { }
