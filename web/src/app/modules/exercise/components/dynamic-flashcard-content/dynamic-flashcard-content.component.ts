import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { FlashcardComponent } from "../../../../shared/components/flashcard/flashcard.component";

import { DynamicContentComponent } from '../dynamic-content/dynamic-content.component';
import { ExerciseContentFlashcardDto, ExerciseContentFlashcardSideDto } from '../../../../shared/api-client';
import { HotkeyService } from '../../../../shared/services/hotKey.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-dynamic-flashcard-content',
  standalone: true,
  imports: [FlashcardComponent, DynamicContentComponent],
  templateUrl: './dynamic-flashcard-content.component.html'
})
export class DynamicFlashcardContentComponent implements OnInit, OnChanges, OnDestroy{
  private subs = new Subscription(); 
  private currentSide: 'front' | 'back' = 'back';
  
  @ViewChild(FlashcardComponent) flashcardComponent?: FlashcardComponent;
  @Input() data?: ExerciseContentFlashcardDto | ExerciseContentFlashcardSideDto; 
  @Input() autoplay = true;

  constructor(private hotkeyService: HotkeyService) {}
  
  ngOnInit(): void {
    this.syncCurrentSide();

    this.subs.add(
      this.hotkeyService.onHotkey({ key: 'space'}).subscribe(() => {
        if (this.data?.type !== 'flashcard')
          return; 

        this.flashcardComponent?.flip();
      })
    );
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      this.syncCurrentSide();
    }
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  onSideChange(side: 'front' | 'back') {
    this.currentSide = side;
  }

  private syncCurrentSide() {
    this.currentSide = this.initialSide;
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
    return this.currentSide;
  }

  get frontAutoplay(): boolean {
    return this.autoplay && this.currentSide === 'front';
  }

  get backAutoplay(): boolean {
    return this.autoplay && this.currentSide === 'back';
  }

  private get initialSide(): 'front' | 'back' {
    if (this.flashcardData?.initialSide === 'front') 
      return 'front';

    return 'back';
  }
}
