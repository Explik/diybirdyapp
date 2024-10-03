import { Component, Injector, Input, OnInit, Type } from '@angular/core';
import { ProgressBarComponent } from '../../../../shared/components/progress-bar/progress-bar.component';
import { ExitIconButtonComponent } from "../../../../shared/components/exit-icon-button/exit-icon-button.component";
import { InstructionComponent } from '../../components/instruction/instruction.component';
import { CorrectableTextFieldComponent } from "../../../../shared/components/correctable-text-field/correctable-text-field.component";
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule, NgComponentOutlet } from '@angular/common';
import { TextQuoteComponent } from "../../../../shared/components/text-quote/text-quote.component";
import { ExerciseContentService } from '../../services/exerciseContent.service';
import { ActivatedRoute } from '@angular/router';
import { InfoBoxComponent } from '../../components/info-box/info-box.component';
import { ExerciseDataService } from '../../services/exerciseData.service';
import { ExerciseWriteSentenceUsingWordContainerComponent } from '../../components/exercise-write-sentence-using-word-container/exercise-write-sentence-using-word-container.component';
import { ExerciseWriteTranslatedSentenceContainerComponent } from '../../components/exercise-write-translated-sentence-container/exercise-write-translated-sentence-container.component';
import { ExerciseService } from '../../services/exercise.service';
import { ExerciseMultipleTextChoiceContainerComponent } from '../../components/exercise-multiple-text-choice-container/exercise-multiple-text-choice-container.component';
import { ExerciseReviewFlashcardContentContainerComponent } from '../../components/exercise-review-flashcard-content-container/exercise-review-flashcard-content-container.component';
import { ExerciseComponentService } from '../../services/exerciseComponent.service';

@Component({
    selector: 'app-session-page',
    standalone: true,
    templateUrl: './session-page.component.html',
    styleUrl: './session-page.component.css',
    imports: [CommonModule, FormsModule, NgComponentOutlet, ProgressBarComponent, ExitIconButtonComponent, InstructionComponent, CorrectableTextFieldComponent, TextButtonComponent, TextQuoteComponent, InfoBoxComponent]
})
export class SessionPageComponent {
    sessionId: string | undefined = undefined;
    exerciseId: string | undefined = undefined;
    exerciseType: string | undefined = undefined;
    exerciseComponent: Type<any> | null = null;

    constructor(
        private route: ActivatedRoute,
        private exerciseService: ExerciseService,
        private exerciseComponentService: ExerciseComponentService,
        private exerciseDataService: ExerciseDataService) {}

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
          this.sessionId = params.get('id') ?? "1";
          this.loadNextExercise(this.sessionId);
        });
    }

    loadNextExercise(sessionId: string) {
        this.exerciseDataService.getNextExercise(sessionId).subscribe(data => {
            if (!data)
                return;

            this.exerciseId = data.id;
            this.exerciseType = data.type;
            this.exerciseService.setExercise(data);
            this.exerciseComponent = this.exerciseComponentService.getComponent(data.type);
        });
    }
}
