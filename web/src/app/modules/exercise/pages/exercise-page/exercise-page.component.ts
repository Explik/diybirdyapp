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

@Component({
    selector: 'app-exercise-page',
    standalone: true,
    templateUrl: './exercise-page.component.html',
    styleUrl: './exercise-page.component.css',
    imports: [CommonModule, FormsModule, NgComponentOutlet, ProgressBarComponent, ExitIconButtonComponent, InstructionComponent, CorrectableTextFieldComponent, TextButtonComponent, TextQuoteComponent, InfoBoxComponent]
})
export class ExercisePageComponent {
    exerciseId: string | undefined = undefined;
    exerciseType: string | undefined = undefined;
    exerciseComponent: Type<any> | null = null;

    constructor(
        private route: ActivatedRoute,
        private exerciseService: ExerciseService,
        private exerciseDataService: ExerciseDataService) {}

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
          const id = params.get('id') ?? "1";
          
          this.exerciseDataService.getExercise(id).subscribe(data => {
            this.exerciseId = data.id;
            this.exerciseType = data.exerciseType;
            this.exerciseService.setExercise(data);

            switch(this.exerciseType) {
                case "write-sentence-using-word-exercise": 
                    this.exerciseComponent = ExerciseWriteSentenceUsingWordContainerComponent;
                    break;
                case "write-translated-sentence-exercise": 
                    this.exerciseComponent = ExerciseWriteTranslatedSentenceContainerComponent;
                    break;
                default: 
                    throw new Error("Unknown exercise type " + this.exerciseType);
            }
          });
        });
    }
}
