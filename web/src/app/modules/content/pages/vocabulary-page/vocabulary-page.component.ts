import { Component } from '@angular/core';
import { VocabularyService } from '../../services/vocabulary.service';
import { CommonModule } from '@angular/common';
import { IconComponent } from '../../../../shared/components/icon/icon.component';
import { AudioPlayService } from '../../../../shared/services/audioPlay.service';
import { VocabularyDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-vocabulary-page',
  standalone: true,
  imports: [CommonModule, IconComponent],
  templateUrl: './vocabulary-page.component.html'
})
export class VocabularyPageComponent {
  vocabulary?: VocabularyDto;

  constructor(
    private vocabularyService: VocabularyService,
    private audioPlayService: AudioPlayService) {}

  ngOnInit() {
    this.vocabularyService.getVocabulary().subscribe(vocabulary => {
      this.vocabulary = vocabulary;
    });
  }

  playAudio(url: string) {
    if (!url) return;
    this.audioPlayService.toggle(url);
  }
}
