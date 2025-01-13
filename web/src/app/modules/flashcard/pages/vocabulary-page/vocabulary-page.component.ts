import { Component } from '@angular/core';
import { VocabularyService } from '../../services/vocabulary.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-vocabulary-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vocabulary-page.component.html'
})
export class VocabularyPageComponent {
  vocabulary?: VocabularyDto;

  constructor(private service: VocabularyService) {}

  ngOnInit() {
    this.service.getVocabulary().subscribe(vocabulary => {
      this.vocabulary = vocabulary;
    });
  }

  playAudio(url: string) {
    const audio = new Audio(url);
    audio.play();
  }
}
