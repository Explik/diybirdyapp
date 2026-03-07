import { mount } from 'cypress/angular';
import { DynamicImageContentComponent } from './dynamic-image-content.component';
import { ExerciseContentImageDto } from '../../../../shared/api-client';

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

const SEL = {
  IMAGE: 'img',
} as const;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function createMockImageData(imageUrl: string = 'test-image.jpg'): ExerciseContentImageDto {
  return {
    id: '1',
    type: 'image-content',
    imageUrl,
  };
}

function mountComponent(data?: ExerciseContentImageDto) {
  return mount(DynamicImageContentComponent, {
    componentProperties: { data },
  });
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('DynamicImageContentComponent', () => {

  describe('Rendering', () => {
    it('renders the image when data is provided', () => {
      const testUrl = 'test-image.jpg';
      mountComponent(createMockImageData(testUrl));
      cy.get(SEL.IMAGE).should('exist');
      cy.get(SEL.IMAGE).should('have.attr', 'src', testUrl);
    });

    it('does not render the image when data is not provided', () => {
      mountComponent(undefined);
      cy.get(SEL.IMAGE).should('not.exist');
    });
  });
});
