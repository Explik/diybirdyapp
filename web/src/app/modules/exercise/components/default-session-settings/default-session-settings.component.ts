import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { LanguageControllerService } from '../../../../shared/api-client/api/language-controller.service';
import { FlashcardLanguageDto } from '../../../../shared/api-client/model/flashcard-language-dto';
import { ExerciseSessionOptions, DEFAULT_SESSION_OPTIONS } from '../../models/exercise-session-options.interface';
import { SettingsDataService } from '../../services/settingsData.service';

@Component({
  selector: 'app-default-session-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <div>
        <h4 class="text-lg font-medium text-gray-900 mb-4">Session Options</h4>
        
        <!-- Text to Speech -->
        <div class="flex items-center justify-between py-3">
          <div>
            <label class="text-sm font-medium text-gray-700">
              Text to Speech
            </label>
            <p class="text-xs text-gray-500">
              Enable audio pronunciation for text content
            </p>
          </div>
          <label class="relative inline-flex items-center cursor-pointer">
            <input 
              type="checkbox" 
              class="sr-only peer"
              [(ngModel)]="localConfig.textToSpeechEnabled"
            >
            <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
          </label>
        </div>

        <!-- Retype Correct Answer -->
        <div class="flex items-center justify-between py-3 border-t border-gray-200">
          <div>
            <label class="text-sm font-medium text-gray-700">
              Retype Correct Answer
            </label>
            <p class="text-xs text-gray-500">
              Require typing correct answers after incorrect attempts
            </p>
          </div>
          <label class="relative inline-flex items-center cursor-pointer">
            <input 
              type="checkbox" 
              class="sr-only peer"
              [(ngModel)]="localConfig.retypeCorrectAnswerEnabled"
            >
            <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
          </label>
        </div>

        <!-- Initial Flashcard Language -->
        <div class="py-3 border-t border-gray-200">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Initial Flashcard Language
          </label>
          <p class="text-xs text-gray-500 mb-3">
            Default language to show on flashcards
          </p>
          <select 
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            [(ngModel)]="localConfig.initialFlashcardLanguageId"
          >
            <option value="">Select a language...</option>
            <option 
              *ngFor="let language of availableLanguages" 
              [value]="language.id"
            >
              {{ language.name }} ({{ language.abbreviation }})
            </option>
          </select>
        </div>

        <!-- Answer Languages -->
        <div class="py-3 border-t border-gray-200">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Answer Languages
          </label>
          <p class="text-xs text-gray-500 mb-3">
            Languages that can be used for answers
          </p>
          
          <div class="space-y-2 max-h-32 overflow-y-auto">
            <label 
              *ngFor="let language of availableLanguages" 
              class="flex items-center space-x-2 text-sm"
            >
              <input 
                type="checkbox" 
                [value]="language.id"
                [checked]="isLanguageSelected(language.id)"
                (change)="onLanguageToggle(language.id, $event)"
                class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              >
              <span>{{ language.name }} ({{ language.abbreviation }})</span>
            </label>
          </div>
          
          <div *ngIf="localConfig.answerLanguageIds.length === 0" class="text-xs text-orange-600 mt-2">
            No languages selected. All languages will be available.
          </div>
        </div>
      </div>
      
      <!-- Action Buttons -->
      <div class="flex justify-end space-x-2 pt-4 border-t border-gray-200">
        <button 
          type="button"
          class="px-4 py-2 text-gray-600 border border-gray-300 rounded hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
          (click)="onCancel()"
        >
          Cancel
        </button>
        <button 
          type="button"
          class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
          (click)="onSave()"
        >
          Save Settings
        </button>
      </div>
    </div>
  `
})
export class DefaultSessionSettingsComponent implements OnInit, OnDestroy {
  sessionType = '';
  localConfig: ExerciseSessionOptions = { ...DEFAULT_SESSION_OPTIONS };
  availableLanguages: FlashcardLanguageDto[] = [];
  isLoading = true;
  
  private subscriptions = new Subscription();

  constructor(
    private languageService: LanguageControllerService,
    private settingsDataService: SettingsDataService
  ) {}

  ngOnInit() {
    // Subscribe to session type and config from service
    this.subscriptions.add(
      this.settingsDataService.getSessionType().subscribe(sessionType => {
        this.sessionType = sessionType;
      })
    );

    this.subscriptions.add(
      this.settingsDataService.getConfig().subscribe(config => {
        if (config) {
          this.localConfig = { ...DEFAULT_SESSION_OPTIONS, ...config };
        }
      })
    );
    
    // Load available languages
    this.languageService.getAll().subscribe({
      next: (languages) => {
        this.availableLanguages = languages;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load languages:', error);
        this.isLoading = false;
      }
    });
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  isLanguageSelected(languageId: string): boolean {
    return this.localConfig.answerLanguageIds.includes(languageId);
  }

  onLanguageToggle(languageId: string, event: Event) {
    const target = event.target as HTMLInputElement;
    const isChecked = target.checked;

    if (isChecked) {
      // Add language if not already present
      if (!this.localConfig.answerLanguageIds.includes(languageId)) {
        this.localConfig.answerLanguageIds = [...this.localConfig.answerLanguageIds, languageId];
      }
    } else {
      // Remove language
      this.localConfig.answerLanguageIds = this.localConfig.answerLanguageIds.filter(id => id !== languageId);
    }
  }

  onSave() {
    this.settingsDataService.save(this.localConfig);
  }

  onCancel() {
    this.settingsDataService.cancel();
  }
}