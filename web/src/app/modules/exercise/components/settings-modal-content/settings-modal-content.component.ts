import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-settings-modal-content',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Settings for {{ sessionType }}
        </label>
        <p class="text-sm text-gray-500">
          Settings configuration will be available soon. This is a placeholder component.
        </p>
        <p class="text-xs text-gray-400 mt-2">
          Note: Configuration options will be loaded from sessionData.options when that field is added to the backend.
        </p>
      </div>
      
      <div class="flex justify-end space-x-2 pt-4 border-t">
        <button 
          type="button"
          class="px-4 py-2 text-gray-600 border border-gray-300 rounded hover:bg-gray-50"
          (click)="onCancel()"
        >
          Cancel
        </button>
        <button 
          type="button"
          class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          (click)="onSave()"
        >
          Save
        </button>
      </div>
    </div>
  `
})
export class SettingsModalContentComponent {
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