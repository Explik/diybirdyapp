import { Component, OnDestroy, OnInit } from '@angular/core';

import { InstructionComponent } from '../../components/instruction/instruction.component';
import { DynamicFlashcardContentComponent } from '../../components/dynamic-flashcard-content/dynamic-flashcard-content.component';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseContentFlashcardDto } from '../../../../shared/api-client';
import { HotkeyService } from '../../../../shared/services/hotKey.service';
import { Subscription } from 'rxjs';
import { IconComponent } from '../../../../shared/components/icon/icon.component';

@Component({
  selector: 'app-exercise-content-sort-flashcard-container',
  standalone: true,
  imports: [InstructionComponent, DynamicFlashcardContentComponent, IconComponent],
  templateUrl: './exercise-content-sort-flashcard-container.component.html'
})
export class ExerciseContentSortFlashcardContainerComponent implements OnInit, OnDestroy {
  private subs = new Subscription();

  content?: ExerciseContentFlashcardDto;
  isSubmitting = false;

  constructor(
    private exerciseService: ExerciseService,
    private hotkeyService: HotkeyService
  ) {}

  ngOnInit(): void {
    this.exerciseService.getContent<ExerciseContentFlashcardDto>().subscribe(data => this.content = data);

    this.subs.add(
      this.hotkeyService.onHotkey({ key: 'left' }).subscribe(() => {
        void this.handlePileSelected('dont-know');
      })
    );

    this.subs.add(
      this.hotkeyService.onHotkey({ key: 'right' }).subscribe(() => {
        void this.handlePileSelected('know');
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  async handlePileSelected(pile: string) {
    if (!this.content || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    try {
      await this.exerciseService.submitAnswerAsync({ type: 'sort-options', pile });
      await this.exerciseService.nextExerciseAsync();
    } finally {
      this.isSubmitting = false;
    }
  }
}
