import { Component, Injector, Input, OnInit, Type, ViewChild, ViewContainerRef, ComponentRef } from '@angular/core';
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
import { Observable, map, take, Subscription } from 'rxjs';
import { SessionOptionsComponentService } from '../../services/sessionOptionsComponent.service';
import { SessionOptionsLearnFlashcardComponent } from '../../container-components/session-options-learn-flashcard/session-options-learn-flashcard.component';
import { ExerciseSessionOptionsDto, ExerciseSessionOptionsLearnFlashcardsDto } from '../../../../shared/api-client';

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
    /** Staged options object passed into child and modified there until applied on close */
    stagedOptions?: ExerciseSessionOptionsDto | ExerciseSessionOptionsLearnFlashcardsDto | undefined;

    @ViewChild('optionsHost', { read: ViewContainerRef }) optionsHost!: ViewContainerRef;
    private optionsComponentRef?: ComponentRef<any>;
    private optionsOutputSub?: Subscription;

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
        // get the concrete options component type for this session first
        this.sessionOptionsComponent.getComponent().pipe(take(1)).subscribe(componentType => {
            // clear previous
            try { this.optionsHost.clear(); } catch {}
            this.optionsComponentRef?.destroy();
            this.optionsOutputSub?.unsubscribe();

            if (!componentType) {
                // still show modal (no component to render)
                this.isSettingsModalOpen = true;
                return;
            }

            // create the dynamic component first
            this.optionsComponentRef = this.optionsHost.createComponent(componentType);
            const inst = this.optionsComponentRef.instance as any;

            // set up outputs before opening modal
            if (inst.optionsChange && inst.optionsChange.subscribe) {
                this.optionsOutputSub = inst.optionsChange.subscribe((o: any) => this.onOptionsChanged(o));
            } else if (inst.optionsChangeCallback) {
                inst.optionsChangeCallback = (o: any) => this.onOptionsChanged(o);
            }

            // open modal immediately with component in place
            this.isSettingsModalOpen = true;

            // then fetch and apply data to the component (component handles loading state)
            this.exerciseService.getExerciseSessionOptions().pipe(take(1)).subscribe(opt => {
                this.stagedOptions = opt;
                if (this.stagedOptions && this.optionsComponentRef) {
                    // use setInput so Angular runs ngOnChanges on the dynamically created component
                    // setInput was introduced to properly notify the component of input changes
                    try {
                        this.optionsComponentRef.setInput('options', this.stagedOptions);
                        // ensure change detection runs on the child
                        this.optionsComponentRef.changeDetectorRef.detectChanges();
                    } catch {
                        // fallback: direct assignment (older APIs)
                        inst.options = this.stagedOptions;
                        this.optionsComponentRef.changeDetectorRef.detectChanges();
                    }
                }
            });
        });
    }

    closeSettingsModal() {
        // apply staged options (if any) when closing
        const finalize = () => {
            this.isSettingsModalOpen = false;
            // cleanup dynamic component
            try { this.optionsHost.clear(); } catch {}
            this.optionsComponentRef?.destroy();
            this.optionsComponentRef = undefined;
            this.optionsOutputSub?.unsubscribe();
            this.optionsOutputSub = undefined;
        };

        if (this.stagedOptions) {
            this.exerciseService.applyExerciseSessionOptions(this.stagedOptions as any).pipe(take(1)).subscribe(() => {
                finalize();
            }, () => finalize());
        } else {
            finalize();
        }
    }

    onOptionsChanged(options: ExerciseSessionOptionsDto | ExerciseSessionOptionsLearnFlashcardsDto) {
        // update staged options as child emits changes
        this.stagedOptions = options;
    }
}
