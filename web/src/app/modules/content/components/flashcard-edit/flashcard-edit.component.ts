import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { TextInputComponent } from '../text-input/text-input.component';
import { AudioInputComponent } from '../audio-input/audio-input.component';
import { ImageInputComponent } from '../image-input/image-input.component';
import { VideoInputComponent } from '../video-input/video-input.component';
import { LabelComponent } from '../../../../shared/components/label/label.component';
import { FormFieldComponent } from '../../../../shared/components/form-field/form-field.component';
import { SelectComponent } from '../../../../shared/components/select/select.component';
import { OptionComponent } from '../../../../shared/components/option/option.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { FormErrorComponent } from '../../../../shared/components/form-error/form-error.component';

@Component({
  selector: 'app-flashcard-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    TextInputComponent,
    AudioInputComponent,
    ImageInputComponent,
    VideoInputComponent,
    LabelComponent,
    FormFieldComponent,
    SelectComponent,
    OptionComponent,
    ButtonComponent,
    FormErrorComponent,
  ],
  templateUrl: './flashcard-edit.component.html',
  styleUrl: './flashcard-edit.component.css'
})
export class FlashcardEditComponent implements OnChanges, OnDestroy {
  /** The reactive FormGroup for this individual flashcard. */
  @Input() formGroup!: FormGroup;
  /** Zero-based position of this card in the deck (used for display and element IDs). */
  @Input() index: number = 0;
  /** Resolved display name for the front/left language (null = no language selected). */
  @Input() frontLanguageName: string | null = null;
  /** Resolved display name for the back/right language (null = no language selected). */
  @Input() backLanguageName: string | null = null;

  @Output() delete = new EventEmitter<void>();

  /** Exposed as component properties so Angular can track changes reactively. */
  leftContentType: string = 'text';
  rightContentType: string = 'text';

  private subscriptions = new Subscription();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['formGroup'] && this.formGroup) {
      this.subscriptions.unsubscribe();
      this.subscriptions = new Subscription();
      this._bindToFormGroup();
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private _bindToFormGroup(): void {
    this.leftContentType  = this.formGroup.get('leftContentType')?.value  ?? 'text';
    this.rightContentType = this.formGroup.get('rightContentType')?.value ?? 'text';

    this.subscriptions.add(
      this.formGroup.get('leftContentType')!.valueChanges.subscribe(v => {
        this.leftContentType = v;
      })
    );
    this.subscriptions.add(
      this.formGroup.get('rightContentType')!.valueChanges.subscribe(v => {
        this.rightContentType = v;
      })
    );
  }

  handleDelete() {
    this.delete.emit();
  }
}
