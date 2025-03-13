import { Component, Injector, Input, OnInit, Type } from '@angular/core';
import { ProgressBarComponent } from '../../../../shared/components/progress-bar/progress-bar.component';
import { ExitIconButtonComponent } from "../../../../shared/components/exit-icon-button/exit-icon-button.component";
import { InstructionComponent } from '../../components/instruction/instruction.component';
import { ExerciseInputWriteTextComponent } from "../../components/exercise-input-write-text/exercise-input-write-text.component";
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { TextButtonComponent } from "../../../../shared/components/text-button/text-button.component";
import { CommonModule, NgComponentOutlet } from '@angular/common';
import { TextQuoteComponent } from "../../../../shared/components/text-quote/text-quote.component";
import { ActivatedRoute } from '@angular/router';
import { InfoBoxComponent } from '../../components/info-box/info-box.component';
import { ExerciseSessionDataService } from '../../services/exerciseSessionData.service';
import { ExerciseContentWriteSentenceUsingWordContainerComponent } from '../../container-components/exercise-content-write-sentence-using-word-container/exercise-content-write-sentence-using-word-container.component';
import { ExerciseContentWriteTranslatedSentenceContainerComponent } from '../../container-components/exercise-content-write-translated-sentence-container/exercise-content-write-translated-sentence-container.component';
import { ExerciseComponentService } from '../../services/exerciseComponent.service';
import { ExerciseService } from '../../services/exercise.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-exercise-page',
    standalone: true,
    templateUrl: './exercise-page.component.html',
    imports: [CommonModule, FormsModule, NgComponentOutlet, ProgressBarComponent, ExitIconButtonComponent]
})
export class ExercisePageComponent {
    sessionId: string | undefined = undefined;
    exerciseId: string | undefined = undefined;
    exerciseType: string | undefined = undefined;
    exerciseComponent$: Observable<Type<any>> | null = null;
    exerciseNavigationComponent$: Observable<Type<any>|null> | null = null;

    constructor(
        private route: ActivatedRoute,
        private exerciseService: ExerciseService,
        private exerciseComponentService: ExerciseComponentService,
        private exerciseDataService: ExerciseSessionDataService) { }

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const id = params.get('id') ?? "1";

            this.exerciseDataService.getExercise(id).subscribe(data => {
                if (!data)
                    return;

                this.exerciseId = data.id;
                this.exerciseType = data.type;
                this.exerciseService.setExercise(data);
                this.exerciseComponent$ = this.exerciseComponentService.getComponent();
                this.exerciseNavigationComponent$ = this.exerciseComponentService.getNavigationComponent();
            });
        });
    }
}
