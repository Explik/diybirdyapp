import { Injectable, Type } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { BasicSettingsComponent } from '../components/basic-settings/basic-settings.component';
import { DefaultSessionSettingsComponent } from '../components/default-session-settings/default-session-settings.component';

@Injectable({
  providedIn: 'root'
})
export class SettingsComponentService {
  private settingsComponent$ = new BehaviorSubject<Type<any> | null>(null);

  getComponent(): Observable<Type<any> | null> {
    return this.settingsComponent$.asObservable();
  }

  setComponent(component: Type<any> | null) {
    this.settingsComponent$.next(component);
  }

  // Method to determine component based on session type
  dispatchComponentByType(sessionType: string): Type<any> | null {
    switch (sessionType) {
      case 'flashcard':
        // TODO: Create FlashcardSettingsComponent
        return DefaultSessionSettingsComponent;
      case 'quiz':
        // TODO: Create QuizSettingsComponent  
        return DefaultSessionSettingsComponent;
      case 'practice':
        // TODO: Create PracticeSettingsComponent
        return DefaultSessionSettingsComponent;
      default:
        // Use DefaultSessionSettingsComponent for unknown/null types
        return DefaultSessionSettingsComponent;
    }
  }
}