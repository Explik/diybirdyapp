import { Component } from '@angular/core';
import { FlashcardComponent } from "../../components/flashcard/flashcard.component";
import { IconComponent } from "../../components/icon/icon.component";

@Component({
  selector: 'app-shared-components-page',
  standalone: true,
  imports: [FlashcardComponent, IconComponent],
  templateUrl: './shared-components-page.component.html'
})
export class SharedComponentsPageComponent { }
