import { TestBed } from '@angular/core/testing';
import { Subscription, of } from 'rxjs';

import { EditFlashcardAudio } from '../models/editFlashcard.model';
import { AudioPlaybackState, AudioPlayingService } from './audioPlaying.service';
import { FlashcardService } from './flashcard.service';

type MockAudioHandle = {
  element: HTMLAudioElement;
  pauseSpy: jasmine.Spy;
  playSpy: jasmine.Spy;
  triggerEnded: () => void;
  triggerError: (error?: unknown) => void;
};

const expectJasmine = expect as unknown as (actual: unknown) => any;

function createMockAudioElement(initialCurrentTime = 0): MockAudioHandle {
  const audioLike: {
    paused: boolean;
    currentTime: number;
    onended: (() => void) | null;
    onerror: ((error?: unknown) => void) | null;
    pause: () => void;
    play: () => Promise<void>;
  } = {
    paused: false,
    currentTime: initialCurrentTime,
    onended: null,
    onerror: null,
    pause: () => {
      // replaced by pauseSpy below
    },
    play: () => Promise.resolve(),
  };

  const pauseSpy = jasmine.createSpy('pause').and.callFake(() => {
    audioLike.paused = true;
  });
  const playSpy = jasmine.createSpy('play').and.returnValue(Promise.resolve());

  audioLike.pause = pauseSpy;
  audioLike.play = playSpy;

  return {
    element: audioLike as unknown as HTMLAudioElement,
    pauseSpy,
    playSpy,
    triggerEnded: () => {
      audioLike.onended?.();
    },
    triggerError: (error?: unknown) => {
      audioLike.onerror?.(error);
    },
  };
}

function createMockEditFlashcardAudio(audioElement: HTMLAudioElement): EditFlashcardAudio {
  return {
    generateElement: () => audioElement,
    getChanges: () => undefined,
  };
}

describe('AudioPlayingService', () => {
  let service: AudioPlayingService;
  let flashcardServiceSpy: jasmine.SpyObj<FlashcardService>;
  let subscriptions: Subscription[];

  const ownerA = Symbol('owner-a');
  const ownerB = Symbol('owner-b');

  beforeEach(() => {
    flashcardServiceSpy = jasmine.createSpyObj<FlashcardService>('FlashcardService', ['fetchTextPronounciation']);

    TestBed.configureTestingModule({
      providers: [
        AudioPlayingService,
        { provide: FlashcardService, useValue: flashcardServiceSpy },
      ],
    });

    service = TestBed.inject(AudioPlayingService);
    subscriptions = [];
  });

  afterEach(() => {
    subscriptions.forEach((subscription) => subscription.unsubscribe());
    subscriptions = [];
  });

  function trackStates(owner?: string | symbol): AudioPlaybackState[] {
    const emittedStates: AudioPlaybackState[] = [];
    const subscription = service.getCurrentState(owner).subscribe((state) => emittedStates.push(state));
    subscriptions.push(subscription);
    return emittedStates;
  }

  it('starts with a stopped global state', () => {
    const globalStates = trackStates();

    expectJasmine(globalStates).toEqual(['stopped']);
  });

  it('emits play and stop only for the active owner while preserving global state', async () => {
    const globalStates = trackStates();
    const ownerAStates = trackStates(ownerA);
    const ownerBStates = trackStates(ownerB);
    const mockAudio = createMockAudioElement();

    const completionPromise = service.startPlayingEditFlashcard(
      createMockEditFlashcardAudio(mockAudio.element),
      ownerA
    );

    expectJasmine(mockAudio.playSpy).toHaveBeenCalled();
    expectJasmine(globalStates.at(-1)).toBe('playing');
    expectJasmine(ownerAStates.at(-1)).toBe('playing');
    expectJasmine(ownerBStates.at(-1)).toBe('stopped');

    mockAudio.triggerEnded();
    await completionPromise;

    expectJasmine(globalStates.at(-1)).toBe('stopped');
    expectJasmine(ownerAStates.at(-1)).toBe('stopped');
    expectJasmine(ownerBStates.at(-1)).toBe('stopped');
  });

  it('emits paused globally and for active owner only', () => {
    const globalStates = trackStates();
    const ownerAStates = trackStates(ownerA);
    const ownerBStates = trackStates(ownerB);
    const mockAudio = createMockAudioElement();

    service.startPlayingEditFlashcard(createMockEditFlashcardAudio(mockAudio.element), ownerA);
    service.pausePlaying();

    expectJasmine(mockAudio.pauseSpy).toHaveBeenCalled();
    expectJasmine(globalStates.at(-1)).toBe('paused');
    expectJasmine(ownerAStates.at(-1)).toBe('paused');
    expectJasmine(ownerBStates.at(-1)).toBe('stopped');
  });

  it('switches immediately to a new track and stops the previous one', async () => {
    const globalStates = trackStates();
    const ownerAStates = trackStates(ownerA);
    const ownerBStates = trackStates(ownerB);

    const firstAudio = createMockAudioElement(7);
    const secondAudio = createMockAudioElement();

    const firstPlayback = service.startPlayingEditFlashcard(
      createMockEditFlashcardAudio(firstAudio.element),
      ownerA
    );

    const secondPlayback = service.startPlayingEditFlashcard(
      createMockEditFlashcardAudio(secondAudio.element),
      ownerB
    );

    await firstPlayback;

    expectJasmine(firstAudio.pauseSpy).toHaveBeenCalled();
    expectJasmine(firstAudio.element.currentTime).toBe(0);
    expectJasmine(secondAudio.playSpy).toHaveBeenCalled();
    expectJasmine(globalStates.at(-1)).toBe('playing');
    expectJasmine(ownerAStates.at(-1)).toBe('stopped');
    expectJasmine(ownerBStates.at(-1)).toBe('playing');

    secondAudio.triggerEnded();
    await secondPlayback;

    expectJasmine(globalStates.at(-1)).toBe('stopped');
    expectJasmine(ownerBStates.at(-1)).toBe('stopped');
  });

  it('stops playback, resets currentTime, and clears owner-scoped state', () => {
    const globalStates = trackStates();
    const ownerAStates = trackStates(ownerA);
    const ownerBStates = trackStates(ownerB);
    const mockAudio = createMockAudioElement(42);

    service.startPlayingEditFlashcard(createMockEditFlashcardAudio(mockAudio.element), ownerA);
    service.stopPlaying();

    expectJasmine(mockAudio.pauseSpy).toHaveBeenCalled();
    expectJasmine(mockAudio.element.currentTime).toBe(0);
    expectJasmine(globalStates.at(-1)).toBe('stopped');
    expectJasmine(ownerAStates.at(-1)).toBe('stopped');
    expectJasmine(ownerBStates.at(-1)).toBe('stopped');
  });

  it('plays text pronunciation through blob URL and revokes URL on completion', async () => {
    const ownerAStates = trackStates(ownerA);
    const mockAudio = createMockAudioElement();
    const blobUrl = 'blob://test-audio-url';
    const originalAudioCtor = window.Audio;
    const audioCtorSpy = jasmine.createSpy('Audio').and.callFake(function (_url?: string) {
      return mockAudio.element;
    });

    flashcardServiceSpy.fetchTextPronounciation.and.returnValue(of(new Blob(['audio'], { type: 'audio/mpeg' })));

    const createObjectURLSpy = spyOn(URL, 'createObjectURL').and.returnValue(blobUrl);
    const revokeObjectURLSpy = spyOn(URL, 'revokeObjectURL');

    Object.defineProperty(window, 'Audio', {
      configurable: true,
      writable: true,
      value: audioCtorSpy,
    });

    try {
      const completionPromise = service.startPlayingTextPronounciation('text-content-id', ownerA);

      // startPlayingTextPronounciation now awaits blob retrieval before constructing Audio.
      await Promise.resolve();

      expectJasmine(flashcardServiceSpy.fetchTextPronounciation).toHaveBeenCalledWith('text-content-id');
      expectJasmine(createObjectURLSpy).toHaveBeenCalled();
      expectJasmine(audioCtorSpy).toHaveBeenCalledWith(blobUrl);
      expectJasmine(mockAudio.playSpy).toHaveBeenCalled();
      expectJasmine(ownerAStates.at(-1)).toBe('playing');

      mockAudio.triggerEnded();
      await completionPromise;

      expectJasmine(revokeObjectURLSpy).toHaveBeenCalledWith(blobUrl);
      expectJasmine(ownerAStates.at(-1)).toBe('stopped');
    } finally {
      Object.defineProperty(window, 'Audio', {
        configurable: true,
        writable: true,
        value: originalAudioCtor,
      });
    }
  });
});
