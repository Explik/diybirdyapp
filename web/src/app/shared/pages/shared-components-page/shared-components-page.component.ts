import { Component } from '@angular/core';
import { FlashcardComponent } from "../../components/flashcard/flashcard.component";
import { IconComponent } from "../../components/icon/icon.component";
import { ProgressBarComponent } from "../../components/progress-bar/progress-bar.component";

@Component({
  selector: 'app-shared-components-page',
  standalone: true,
  imports: [FlashcardComponent, IconComponent, ProgressBarComponent],
  templateUrl: './shared-components-page.component.html'
})
export class SharedComponentsPageComponent { }
