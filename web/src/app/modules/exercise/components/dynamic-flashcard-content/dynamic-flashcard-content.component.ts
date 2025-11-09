import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";
import { CommonModule } from '@angular/common';
import { DynamicContentComponent } from '../dynamic-content/dynamic-content.component';
import { ExerciseContentFlashcardDto, ExerciseContentFlashcardSideDto } from '../../../../shared/api-client';
import { HotkeyService } from '../../../../shared/services/hotKey.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-dynamic-flashcard-content',
  standalone: true,
  imports: [CommonModule, FlashcardComponent, DynamicContentComponent],
  templateUrl: './dynamic-flashcard-content.component.html'
})
export class DynamicFlashcardContentComponent implements OnInit, OnDestroy{
  private subs = new Subscription(); 
  
  @ViewChild(FlashcardComponent) flashcardComponent?: FlashcardComponent;
  @Input() data?: ExerciseContentFlashcardDto | ExerciseContentFlashcardSideDto; 

  constructor(private hotkeyService: HotkeyService) {}
  
  ngOnInit(): void {
    this.subs.add(
      this.hotkeyService.onHotkey({ key: 'space'}).subscribe(() => {
        if (this.data?.type !== 'flashcard')
          return; 

        this.flashcardComponent?.flip();
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  get flashcardData(): ExerciseContentFlashcardDto | undefined {
    if (this.data?.type !== 'flashcard') 
      return undefined; 

    return this.data as ExerciseContentFlashcardDto;
  }

  get flashcardSideData(): ExerciseContentFlashcardSideDto | undefined {
    if (this.data?.type !== 'flashcard-side') 
      return undefined; 

    return this.data as ExerciseContentFlashcardSideDto; 
  }

  get side(): 'front' | 'back' {
    if (this.flashcardData?.initialSide === 'front') 
      return 'front';
    return 'back';
  }
}
