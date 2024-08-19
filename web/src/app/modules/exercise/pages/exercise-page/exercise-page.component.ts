import { Component, Input, OnInit } from '@angular/core';
import { SessionContainerComponent } from '../../components/session-container/session-container.component';
import { ProgressBarComponent } from '../../../../shared/components/progress-bar/progress-bar.component';
import { ExitIconButtonComponent } from "../../../../shared/components/exit-icon-button/exit-icon-button.component";
import { InstructionComponent } from '../../components/instruction/instruction.component';
import { CorrectableTextFieldComponent } from "../../../../shared/components/correctable-text-field/correctable-text-field.component";
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule, NgComponentOutlet } from '@angular/common';
import { TextQuoteComponent } from "../../../../shared/components/text-quote/text-quote.component";
import { ExerciseService } from '../../services/exercise.service';
import { ActivatedRoute } from '@angular/router';
import { BaseExercise, BaseExerciseAnswer, MultipleChoiceExerciseAnswer, MultipleChoiceOption, MultipleTextChoiceOptionExercise, TranslateSentenceExercise, WriteSentenceUsingWordExercise, WrittenExerciseAnswer } from "../../models/exercise.interface";
import { InfoBoxComponent } from '../../components/info-box/info-box.component';
import { SelectOptionFieldComponent } from "../../components/select-option-field/select-option-field.component";
import { ExerciseComponent } from '../../models/exerciseComponent.interface';
import { ExerciseComponentService } from '../../services/exerciseComponent.service';

@Component({
    selector: 'app-exercise-page',
    standalone: true,
    templateUrl: './exercise-page.component.html',
    styleUrl: './exercise-page.component.css',
    imports: [CommonModule, FormsModule, NgComponentOutlet, SessionContainerComponent, ProgressBarComponent, ExitIconButtonComponent, InstructionComponent, CorrectableTextFieldComponent, TextButtonComponent, TextQuoteComponent, InfoBoxComponent, SelectOptionFieldComponent]
})
export class ExercisePageComponent {
    components: ExerciseComponent[] = []

    exerciseId: string | undefined = undefined;
    exerciseType: string | undefined = undefined;
    isTextExercise: boolean = true;
    feedbackType: string | undefined = undefined;
    lastAnswer: WrittenExerciseAnswer | undefined = undefined;

    instruction: string = "Unknown exercise";
    subInstruction: string = "Still unknown exercise";
    textQuote?: string = "Ehh..."
    
    input: string = 'Hello world'
    inputOptions: MultipleChoiceOption[] = [
        { id: "100", text: "Option 1", result: undefined },
        { id: "101", text: "Option 2", result: undefined },
        { id: "101", text: "Option 3", result: undefined },
        { id: "101", text: "Option 4", result: undefined },
    ]
    state: 'input'|'result' = 'input'
    result: 'success' | 'failure' | null = null;

    constructor(
        private route: ActivatedRoute,
        private exerciseService: ExerciseComponentService,
      ) {}

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
          const id = params.get('id') ?? "1";
          
          this.exerciseService.getExerciseComponents(id).subscribe(data => {
            this.components = data;
          });
        });
    }

    handleCheckClicked() {
        if (this.state === 'result')
            return;

        let exercise: BaseExercise; 
        if (this.exerciseType === "multiple-text-choice-exercise") {
            exercise = {
                id: this.exerciseId!, 
                exerciseType: this.exerciseType!,
                exerciseAnswer: { 
                    id: this.getRandomInt(1000) + "",
                    answerType: "multiple-choice-exercise-answer", 
                    optionId: this.inputOptions[0].id
                } as MultipleChoiceExerciseAnswer
            };
        }
        else {
            exercise = {
                id: this.exerciseId!, 
                exerciseType: this.exerciseType!,
                exerciseAnswer: { 
                    id: this.getRandomInt(1000) + "",
                    answerType: "written-exercise-answer", 
                    text: this.input 
                } as WrittenExerciseAnswer
            };
        }

        // this.exerciseService.submitExerciseAnswer(this.exerciseId!, exercise).subscribe(data => {
        //     this.state = 'result'
        //     this.result = Math.random() > 0.5 ? 'success' : 'failure';
        //     this.inputOptions.forEach(o => o.result = 'failure');
        //     this.inputOptions[this.getRandomInt(3)].result = 'success'

        //     this.feedbackType = data.feedbackType;
        //     this.lastAnswer = data.lastAnswer;
        // });
    }

    getRandomInt(max: number) {
        return Math.floor(Math.random() * max);
    }
}
