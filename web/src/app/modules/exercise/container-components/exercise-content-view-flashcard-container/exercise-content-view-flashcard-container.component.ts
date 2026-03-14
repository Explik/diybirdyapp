import { Component, OnInit } from '@angular/core';
import { InstructionComponent } from '../../components/instruction/instruction.component';
import { DynamicFlashcardContentComponent } from '../../components/dynamic-flashcard-content/dynamic-flashcard-content.component';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseContentFlashcardDto } from '../../../../shared/api-client';

@Component({
  selector: 'app-exercise-content-view-flashcard-container',
  standalone: true,
  imports: [InstructionComponent, DynamicFlashcardContentComponent],
  templateUrl: './exercise-content-view-flashcard-container.component.html'
})
export class ExerciseContentViewFlashcardContainerComponent implements OnInit {
  content?: ExerciseContentFlashcardDto;

  constructor(private exerciseService: ExerciseService) {}

  ngOnInit(): void {
    this.exerciseService.getContent<ExerciseContentFlashcardDto>().subscribe(data => this.content = data);
  }
}
