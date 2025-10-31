import { Component, Injector, Input, OnInit, Type } from '@angular/core';
import { ProgressBarComponent } from '../../../../shared/components/progress-bar/progress-bar.component';
import { ExitIconButtonComponent } from "../../../../shared/components/exit-icon-button/exit-icon-button.component";
import { InstructionComponent } from '../../components/instruction/instruction.component';
import { ExerciseInputWriteTextComponent } from "../../components/exercise-input-write-text/exercise-input-write-text.component";
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { CommonModule, NgComponentOutlet } from '@angular/common';
import { TextQuoteComponent } from "../../../../shared/components/text-quote/text-quote.component";
import { ActivatedRoute, Router } from '@angular/router';
import { InfoBoxComponent } from '../../components/info-box/info-box.component';
import { ExerciseSessionDataService } from '../../services/exerciseSessionData.service';
import { ExerciseContentWriteSentenceUsingWordContainerComponent } from '../../container-components/exercise-content-write-sentence-using-word-container/exercise-content-write-sentence-using-word-container.component';
import { ExerciseContentWriteTranslatedSentenceContainerComponent } from '../../container-components/exercise-content-write-translated-sentence-container/exercise-content-write-translated-sentence-container.component';
import { ExerciseContentMultipleTextChoiceContainerComponent } from '../../container-components/exercise-content-multiple-text-choice-container/exercise-content-multiple-text-choice-container.component';
import { ExerciseContentReviewFlashcardContainerComponent } from '../../container-components/exercise-content-review-flashcard-container/exercise-content-review-flashcard-container.component';
import { ExerciseComponentService } from '../../services/exerciseComponent.service';
import { ExerciseService } from '../../services/exercise.service';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { IconComponent } from '../../../../shared/components/icon/icon.component';
import { Observable, map } from 'rxjs';
import { SessionOptionsComponentService } from '../../services/sessionOptionsComponent.service';

@Component({
    selector: 'app-session-page',
    standalone: true,
    templateUrl: './session-page.component.html',
    imports: [CommonModule, FormsModule, NgComponentOutlet, ProgressBarComponent, ExitIconButtonComponent, ModalComponent, IconComponent]
})
export class SessionPageComponent {
    sessionId: string | undefined = undefined;

    exerciseFeedback: string | undefined = undefined;
    isSettingsModalOpen = false;
    currentSessionType = '';
    currentConfig: any = {};

    sessionProgress$: Observable<number>;
    exerciseComponent$: Observable<Type<any>>;
    exerciseNavigationComponent$: Observable<Type<any>|null>;
    sessionOptionsComponent$: Observable<Type<any>|null>;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private exerciseService: ExerciseService,
        private exerciseComponentService: ExerciseComponentService,
        private sessionOptionsComponent: SessionOptionsComponentService
        ) {
            this.sessionProgress$ = this.exerciseService.getProgress().pipe(map(progress => progress || 0));
            this.exerciseComponent$ = this.exerciseComponentService.getComponent();
            this.exerciseNavigationComponent$ = this.exerciseComponentService.getNavigationComponent();
            this.sessionOptionsComponent$ = this.sessionOptionsComponent.getComponent();
        }

    ngOnInit(): void {
        // Load the session
        this.route.paramMap.subscribe(params => {
          this.sessionId = params.get('id') ?? "1";
          this.exerciseService.loadExerciseSession(this.sessionId);
        });   

        // Attach exercise listernes 
        this.exerciseService.getFeedbackMessage().subscribe(message => {
            this.exerciseFeedback = message;
        });

        // Attach session listeners
        this.exerciseService.getExerciseSession().subscribe(session => {
            if (session?.completed) {
                this.exerciseService.setExerciseSession(undefined);
                this.router.navigate(['/']);
            }
        });
    }

    handleExit() {
        this.router.navigate(['/']);
    }

    openSettingsModal() {
        this.isSettingsModalOpen = true;
    }

    closeSettingsModal() {
        this.isSettingsModalOpen = false;
    }
}
