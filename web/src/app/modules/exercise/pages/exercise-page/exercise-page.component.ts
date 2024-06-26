import { Component, Input, OnInit } from '@angular/core';
import { SessionContainerComponent } from '../../components/session-container/session-container.component';
import { ProgressBarComponent } from '../../../../shared/components/progress-bar/progress-bar.component';
import { ExitIconButtonComponent } from "../../../../shared/components/exit-icon-button/exit-icon-button.component";
import { InstructionComponent } from '../../components/instruction/instruction.component';
import { TextFieldComponent } from "../../components/text-field/text-field.component";
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule } from '@angular/common';
import { TextQuoteComponent } from "../../../../shared/components/text-quote/text-quote.component";
import { ExerciseService } from '../../services/exercise.service';
import { ActivatedRoute } from '@angular/router';
import { BaseExercise, BaseExerciseAnswer, TranslateSentenceExercise, WriteSentenceUsingWordExercise, WrittenExerciseAnswer } from "../../models/exercise.interface";

@Component({
    selector: 'app-exercise-page',
    standalone: true,
    templateUrl: './exercise-page.component.html',
    styleUrl: './exercise-page.component.css',
    imports: [CommonModule, FormsModule, SessionContainerComponent, ProgressBarComponent, ExitIconButtonComponent, InstructionComponent, TextFieldComponent, TextButtonComponent, TextQuoteComponent]
})
export class ExercisePageComponent {
    exerciseId: string | undefined = undefined;
    exerciseType: string | undefined = undefined;
    feedbackType: string | undefined = undefined;
    lastAnswer: WrittenExerciseAnswer | undefined = undefined;

    instruction: string = "Unknown exercise";
    subInstruction: string = "Still unknown exercise";
    textQuote?: string = "Ehh..."
    
    input: string = 'Hello world'
    state: 'input'|'result' = 'input'
    result: 'success' | 'failure' | null = null;

    constructor(
        private route: ActivatedRoute,
        private exerciseService: ExerciseService,
      ) {}

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
          const id = params.get('id') ?? "1";
          
          this.exerciseService.getExercise(id).subscribe(data => {
            this.exerciseId = data.id;
            this.exerciseType = data.exerciseType;

            if (data.exerciseType === "write-translated-sentence-exercise") {
                const exercise = <TranslateSentenceExercise>data;
                this.instruction = "Translate the sentence"; 
                this.subInstruction = `Translate the sentence below into ${exercise.targetLanguage}`;
                this.textQuote = exercise.originalSentence;
            }
            if (data.exerciseType === 'write-sentence-using-word-exercise') {
                const exercise = <WriteSentenceUsingWordExercise>data;
                this.instruction = "Write an original sentence";
                this.subInstruction = `Write a sentence using the word "${exercise.word}"`;
                this.textQuote = undefined;
            }
          });
        });
    }

    handleCheckClicked() {
        if (this.state === 'result')
            return;

        const exercise: BaseExercise = {
            id: this.exerciseId!, 
            exerciseType: this.exerciseType!,
            exerciseAnswer: { 
                id: this.getRandomInt(1000) + "",
                answerType: "written-exercise-answer", 
                text: this.input 
            } as WrittenExerciseAnswer
        };

        this.exerciseService.submitExerciseAnswer(this.exerciseId!, exercise).subscribe(data => {
            this.state = 'result'
            this.result = Math.random() > 0.5 ? 'success' : 'failure';

            this.feedbackType = data.feedbackType;
            this.lastAnswer = data.lastAnswer;
        });
    }

    getRandomInt(max: number) {
        return Math.floor(Math.random() * max);
    }
}
