import { mount } from 'cypress/angular';
import { FormBuilder, FormGroup } from '@angular/forms';
import { FlashcardEditComponent } from './flashcard-edit.component';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  CONTAINER:   '.flashcard-edit-item',
  CARD_NUMBER: '.flashcard-item-number',
  LEFT_TYPE:   '.left-content-type-input',
  RIGHT_TYPE:  '.right-content-type-input',
  LEFT_SIDE:   '.left-side',
  RIGHT_SIDE:  '.right-side',
} as const;

const BTN = {
  DELETE: 'Delete',
} as const;

const CONTENT_TYPE_LABEL = {
  TEXT:  'Text',
  AUDIO: 'Audio',
  IMAGE: 'Image',
  VIDEO: 'Video',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createFormGroup(overrides: Record<string, any> = {}): FormGroup {
  const fb = new FormBuilder();
  return fb.group({
    id:                ['test-id'],
    deckId:            ['deck-1'],
    deckOrder:         [1],
    leftContentType:   [overrides['leftContentType']  ?? 'text'],
    rightContentType:  [overrides['rightContentType'] ?? 'text'],
    leftTextContent:   [overrides['leftTextContent']  ?? { text: '' }],
    rightTextContent:  [overrides['rightTextContent'] ?? { text: '' }],
    leftAudioContent:  [overrides['leftAudioContent']  ?? null],
    rightAudioContent: [overrides['rightAudioContent'] ?? null],
    leftImageContent:  [overrides['leftImageContent']  ?? null],
    rightImageContent: [overrides['rightImageContent'] ?? null],
    leftVideoContent:  [overrides['leftVideoContent']  ?? null],
    rightVideoContent: [overrides['rightVideoContent'] ?? null],
  });
}

function mountCard(
  fg: FormGroup,
  index = 0,
  frontLanguageName: string | null = null,
  backLanguageName: string | null = null,
) {
  return mount(FlashcardEditComponent, {
    componentProperties: { formGroup: fg, index, frontLanguageName, backLanguageName },
  });
}

/** Click an app-select to open it, then click the option that matches the label. */
function selectOption(selectSel: string, optionLabel: string) {
  cy.get(selectSel).click();
  cy.get(selectSel).find('app-option').contains(optionLabel).click();
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('FlashcardEditComponent', () => {

  describe('Rendering', () => {
    it('renders the flashcard edit container', () => {
      mountCard(createFormGroup());
      cy.get(SEL.CONTAINER).should('exist');
    });

    it('renders the left-side and right-side slots', () => {
      mountCard(createFormGroup());
      cy.get(SEL.LEFT_SIDE).should('exist');
      cy.get(SEL.RIGHT_SIDE).should('exist');
    });
  });

  describe('Card number', () => {
    it('shows "#1" when index is 0', () => {
      mountCard(createFormGroup(), 0);
      cy.get(SEL.CARD_NUMBER).should('contain.text', '#1');
    });

    it('shows "#3" when index is 2', () => {
      mountCard(createFormGroup(), 2);
      cy.get(SEL.CARD_NUMBER).should('contain.text', '#3');
    });
  });

  describe('Content type — defaults', () => {
    it('shows the text input on both sides when content types are "text"', () => {
      mountCard(createFormGroup({ leftContentType: 'text', rightContentType: 'text' }));
      cy.get('#left-text-0').should('exist');
      cy.get('#right-text-0').should('exist');
    });
  });

  describe('Content type — left side switching', () => {
    it('shows audio input when front content type is "audio"', () => {
      mountCard(createFormGroup({ leftContentType: 'audio', rightContentType: 'text' }));
      cy.get('#left-audio-0 .audio-input-container').should('exist');
      cy.get('#left-text-0').should('not.exist');
    });

    it('shows image input when front content type is "image"', () => {
      mountCard(createFormGroup({ leftContentType: 'image', rightContentType: 'text' }));
      cy.get('#left-image-0 .image-input-container').should('exist');
      cy.get('#left-text-0').should('not.exist');
    });

    it('shows video input when front content type is "video"', () => {
      mountCard(createFormGroup({ leftContentType: 'video', rightContentType: 'text' }));
      cy.get('#left-video-0 .video-input-container').should('exist');
      cy.get('#left-text-0').should('not.exist');
    });

    it('switches to audio input via the left content-type selector', () => {
      mountCard(createFormGroup());
      selectOption(SEL.LEFT_TYPE, CONTENT_TYPE_LABEL.AUDIO);
      cy.get('#left-audio-0 .audio-input-container').should('exist');
      cy.get('#left-text-0').should('not.exist');
    });

    it('preserves front text content when switching away from text and back again', () => {
      mountCard(createFormGroup({ leftTextContent: { text: 'hello' } }));
      cy.get('#left-text-0 input').should('have.value', 'hello');

      selectOption(SEL.LEFT_TYPE, CONTENT_TYPE_LABEL.AUDIO);
      cy.get('#left-text-0').should('not.exist');

      selectOption(SEL.LEFT_TYPE, CONTENT_TYPE_LABEL.TEXT);
      cy.get('#left-text-0 input').should('have.value', 'hello');
    });
  });

  describe('Content type — right side switching', () => {
    it('shows audio input when back content type is "audio"', () => {
      mountCard(createFormGroup({ leftContentType: 'text', rightContentType: 'audio' }));
      cy.get('#right-audio-0 .audio-input-container').should('exist');
      cy.get('#right-text-0').should('not.exist');
    });

    it('shows image input when back content type is "image"', () => {
      mountCard(createFormGroup({ leftContentType: 'text', rightContentType: 'image' }));
      cy.get('#right-image-0 .image-input-container').should('exist');
      cy.get('#right-text-0').should('not.exist');
    });

    it('shows video input when back content type is "video"', () => {
      mountCard(createFormGroup({ leftContentType: 'text', rightContentType: 'video' }));
      cy.get('#right-video-0 .video-input-container').should('exist');
      cy.get('#right-text-0').should('not.exist');
    });

    it('switches to audio input via the right content-type selector', () => {
      mountCard(createFormGroup());
      selectOption(SEL.RIGHT_TYPE, CONTENT_TYPE_LABEL.AUDIO);
      cy.get('#right-audio-0 .audio-input-container').should('exist');
      cy.get('#right-text-0').should('not.exist');
    });

    it('preserves back text content when switching away from text and back again', () => {
      mountCard(createFormGroup({ rightTextContent: { text: 'hej' } }));
      cy.get('#right-text-0 input').should('have.value', 'hej');

      selectOption(SEL.RIGHT_TYPE, CONTENT_TYPE_LABEL.AUDIO);
      cy.get('#right-text-0').should('not.exist');

      selectOption(SEL.RIGHT_TYPE, CONTENT_TYPE_LABEL.TEXT);
      cy.get('#right-text-0 input').should('have.value', 'hej');
    });
  });

  describe('Language name labels', () => {
    it('shows the front language name when frontLanguageName is provided', () => {
      mountCard(createFormGroup(), 0, 'English', null);
      cy.get(SEL.LEFT_SIDE).should('contain.text', 'English');
    });

    it('shows the back language name when backLanguageName is provided', () => {
      mountCard(createFormGroup(), 0, null, 'Danish');
      cy.get(SEL.RIGHT_SIDE).should('contain.text', 'Danish');
    });

    it('does not show a language label when neither name is provided', () => {
      mountCard(createFormGroup(), 0, null, null);
      cy.get(SEL.LEFT_SIDE).should('not.contain.text', 'English');
      cy.get(SEL.RIGHT_SIDE).should('not.contain.text', 'Danish');
    });
  });

  describe('Validation errors', () => {
    it('shows "Front text required" error when the left text control has a text.required error', () => {
      const fg = createFormGroup();
      mountCard(fg).then(({ component }) => {
        component.formGroup.get('leftTextContent')?.setErrors({ 'text.required': true });
      });
      cy.get(`${SEL.LEFT_SIDE} app-form-error`).should('contain.text', 'Front text required');
    });

    it('shows "Back text required" error when the right text control has a text.required error', () => {
      const fg = createFormGroup();
      mountCard(fg).then(({ component }) => {
        component.formGroup.get('rightTextContent')?.setErrors({ 'text.required': true });
      });
      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).should('contain.text', 'Back text required');
    });

    it('shows "Front image required" error when the left image control has an image.required error', () => {
      const fg = createFormGroup({ leftContentType: 'image' });
      mountCard(fg).then(({ component }) => {
        component.formGroup.get('leftImageContent')?.setErrors({ 'image.required': true });
      });
      cy.get(`${SEL.LEFT_SIDE} app-form-error`).should('contain.text', 'Front image required');
    });

    it('shows "Back image required" error when the right image control has an image.required error', () => {
      const fg = createFormGroup({ rightContentType: 'image' });
      mountCard(fg).then(({ component }) => {
        component.formGroup.get('rightImageContent')?.setErrors({ 'image.required': true });
      });
      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).should('contain.text', 'Back image required');
    });

    it('shows "Front audio required" error when the left audio control has an audio.required error', () => {
      const fg = createFormGroup({ leftContentType: 'audio' });
      mountCard(fg).then(({ component }) => {
        component.formGroup.get('leftAudioContent')?.setErrors({ 'audio.required': true });
      });
      cy.get(`${SEL.LEFT_SIDE} app-form-error`).should('contain.text', 'Front audio required');
    });

    it('shows "Back audio required" error when the right audio control has an audio.required error', () => {
      const fg = createFormGroup({ rightContentType: 'audio' });
      mountCard(fg).then(({ component }) => {
        component.formGroup.get('rightAudioContent')?.setErrors({ 'audio.required': true });
      });
      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).should('contain.text', 'Back audio required');
    });

    it('shows "Front video required" error when the left video control has a video.required error', () => {
      const fg = createFormGroup({ leftContentType: 'video' });
      mountCard(fg).then(({ component }) => {
        component.formGroup.get('leftVideoContent')?.setErrors({ 'video.required': true });
      });
      cy.get(`${SEL.LEFT_SIDE} app-form-error`).should('contain.text', 'Front video required');
    });

    it('shows "Back video required" error when the right video control has a video.required error', () => {
      const fg = createFormGroup({ rightContentType: 'video' });
      mountCard(fg).then(({ component }) => {
        component.formGroup.get('rightVideoContent')?.setErrors({ 'video.required': true });
      });
      cy.get(`${SEL.RIGHT_SIDE} app-form-error`).should('contain.text', 'Back video required');
    });
  });

  describe('Delete button', () => {
    it('emits the delete event when the Delete button is clicked', () => {
      const fg = createFormGroup();
      mountCard(fg).then(({ component }) => {
        cy.spy(component.delete, 'emit').as('deleteSpy');
      });
      cy.contains('button', BTN.DELETE).click();
      cy.get('@deleteSpy').should('have.been.calledOnce');
    });
  });
});

