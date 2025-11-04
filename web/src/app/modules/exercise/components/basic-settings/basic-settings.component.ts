import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-basic-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-4">
      <div>
        <label i18n class="block text-sm font-medium text-gray-700 mb-2">
          Settings for {{ sessionType }}
        </label>
        <p i18n class="text-sm text-gray-500">
          Settings configuration will be available soon.
        </p>
      </div>
      
      <div class="flex justify-end space-x-2 pt-4 border-t">
        <button 
          type="button"
          i18n
          class="px-4 py-2 text-gray-600 border border-gray-300 rounded hover:bg-gray-50"
          (click)="onCancel()"
        >
          Cancel
        </button>
        <button 
          type="button"
          i18n
          class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          (click)="onSave()"
        >
          Save
        </button>
      </div>
    </div>
  `
})
export class BasicSettingsComponent {
  @Input() sessionType: string = '';
  @Input() config: any = {};
  @Output() save = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  onSave() {
    this.save.emit(this.config);
  }

  onCancel() {
    this.cancel.emit();
  }
}