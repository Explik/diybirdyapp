import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ExerciseService } from '../../services/exercise.service';

@Component({
  selector: 'app-exercise-review-progress-bar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex items-center justify-between p-4">
      <div class="w-full bg-white border border-black">
        <div class="border-2 border-white overflow-hidden">
          <div class="h-4 flex">
            <div class="h-full bg-green-600" [style.width.%]="longTermPercentage"></div>
            <div class="h-full bg-red-600" [style.width.%]="learningPercentage"></div>
            <div class="h-full bg-gray-300" [style.width.%]="newPercentage"></div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ExerciseReviewProgressBarComponent implements OnInit {
  longTermPercentage = 0;
  learningPercentage = 0;
  newPercentage = 0;

  constructor(private exerciseService: ExerciseService) {}

  ngOnInit(): void {
    this.exerciseService.getProgress().subscribe(progressData => {
      this.longTermPercentage = progressData?.reviewLongTermPercentage || 0;
      this.learningPercentage = progressData?.reviewLearningPercentage || 0;
      this.newPercentage = progressData?.reviewNewPercentage || 0;
      this.normalizeToHundred();
    });
  }

  private normalizeToHundred(): void {
    const total = this.longTermPercentage + this.learningPercentage + this.newPercentage;
    if (total <= 0) {
      return;
    }

    if (total === 100) {
      return;
    }

    const normalizedLongTerm = Math.round((this.longTermPercentage / total) * 100);
    const normalizedLearning = Math.round((this.learningPercentage / total) * 100);
    const normalizedNew = 100 - normalizedLongTerm - normalizedLearning;

    this.longTermPercentage = normalizedLongTerm;
    this.learningPercentage = normalizedLearning;
    this.newPercentage = Math.max(0, normalizedNew);
  }
}