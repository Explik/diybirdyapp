describe('Flashcard deck features', () => {
  describe('Flashcard deck data', () => {
    it('Name is updated when name update is saved', () => {
      createNewDeck(); 

      setDeckName('Updated deck');
      saveDeckChanges(); 

      // Check if name is updated on details page
      cy.reload();
      cy.contains('Updated deck').should('exist')

      // Check if name is updated on overview page
      cy.visit('/flashcard-deck/')
      cy.contains('Updated deck').should('exist')
    }); 

    it ('Name is not updated when name update is not saved', () => {
      createNewDeck(); 

      setDeckName('Updated deck');

      // Check if name is not updated on details page
      cy.reload();
      cy.contains('Updated deck').should('not.exist')

      // Check if name is not updated on overview page
      cy.visit('/flashcard-deck/')
      cy.contains('Updated deck').should('not.exist')
    }); 

    // TODO Add test for deck languages
  }); 

  describe('Flashcard deck exercises', () => {
    it ('Start exercises options are disabled when no flashcards are present', () => {
      createNewDeck();

      cy.contains('Select flashcards').should('be.disabled')
      cy.contains('Review flashcards').should('be.disabled')
      cy.contains('Write flashcards').should('be.disabled')
      cy.contains('Learn flashcards').should('be.disabled')
    });

    it ('Start exercises options are enabled when flashcards are present', () => {
      createNewDeck(); 

      cy.contains('Add flashcard').click(); 
      setLeftTextContent('Question 1')
      setRightTextContent('Answer 1')
      cy.contains('Save changes').click(); 

      cy.contains('Select flashcards').should('not.be.disabled')
      cy.contains('Review flashcards').should('not.be.disabled')
      cy.contains('Write flashcards').should('not.be.disabled')
      cy.contains('Learn flashcards').should('not.be.disabled')
    });
  }); 

  describe('Flashcard deck flashcards', () => {
    it ('Flashcard is added when add flashcard is clicked', () => {
      createNewDeck();

      createNewFlashcard(); 
      
      cy.get('.flashcard-edit-item').should('exist'); 
    }); 

    it ('Flashcard is removed when remove flashcard is clicked', () => {
      createNewDeck();
      createNewFlashcard(); 

      deleteFlashcard();

      cy.get('.flashcard-edit-item').should('not.exist');
    }); 

    it ('Flashcard is not persisted when not saved', () => {
      createNewDeck();

      createNewFlashcard(); 
      setLeftTextContent('Question 1')
      setRightTextContent('Answer 1')
      cy.reload(); 

      cy.contains('Question 1').should('not.exist'); 
      cy.contains('Answer 1').should('not.exist');
    }); 

    it ('Text-text flaschards input options are available when selected', () => {
      createNewDeck(); 

      createNewFlashcard();
      setLeftContentType('Text');
      setRightContentType('Text');

      cy.get(':nth-child(1) > .flashcard-edit-item .left-side .text-input-container').should('exist');
      cy.get(':nth-child(1) > .flashcard-edit-item .right-side .text-input-container').should('exist');
    }); 

    it ('Audio-audio flaschards input options are available when selected', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftContentType('Audio');
      setRightContentType('Audio');

      cy.get(':nth-child(1) > .flashcard-edit-item .left-side .audio-input-container').should('exist');
      cy.get(':nth-child(1) > .flashcard-edit-item .right-side .audio-input-container').should('exist');
    });

    it ('Image-image flaschards input options are available when selected', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftContentType('Image');
      setRightContentType('Image');

      cy.get(':nth-child(1) > .flashcard-edit-item .left-side .image-input-container').should('exist');
      cy.get(':nth-child(1) > .flashcard-edit-item .right-side .image-input-container').should('exist');
    }); 

    it ('Video-video flaschards input options are available when selected', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftContentType('Video');
      setRightContentType('Video');

      cy.get(':nth-child(1) > .flashcard-edit-item .left-side .video-input-container').should('exist');
      cy.get(':nth-child(1) > .flashcard-edit-item .right-side .video-input-container').should('exist');
    }); 

    it ('Text-text flascard is immidiately available for review', () => {
      createNewDeck(); 

      createNewFlashcard();
      setLeftContentType('Text');
      setRightContentType('Text');
      setLeftTextContent('Question 1')
      setRightTextContent('Answer 1')

      cy.get('.flashcard-review-container').contains('Question 1').should('exist');
      cy.get('.flashcard-review-container').contains('Answer 1').should('exist');
    }); 

    it ('Audio-audio flashcard is immidiately available for review', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftAudioContent('./cypress/files/example.mp3')
      setRightAudioContent('./cypress/files/example.mp3')

      cy.get('.flashcard-review-container .flashcard-front .audio-preview-container').should('exist');
      cy.get('.flashcard-review-container .flashcard-back .audio-preview-container').should('exist');
    }); 

    it ('Image-image flashcard is immidiately available for review', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftImageContent('./cypress/files/example.jpg')
      setRightImageContent('./cypress/files/example.jpg')

      cy.get('.flashcard-review-container .flashcard-front .image-preview-container').should('exist');
      cy.get('.flashcard-review-container .flashcard-back .image-preview-container').should('exist');
    });

    it ('Video-video flashcard is immidiately available for review', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftVideoContent('./cypress/files/example.mp4')
      setRightVideoContent('./cypress/files/example.mp4')
      
      cy.get('.flashcard-review-container .flashcard-front .video-preview-container').should('exist');
      cy.get('.flashcard-review-container .flashcard-back .video-preview-container').should('exist');
    });

    it ('Text-text flashcard is persisted when saved', () => {
      createNewDeck();

      createNewFlashcard(); 
      setLeftTextContent('Question 1')
      setRightTextContent('Answer 1')
      saveDeckChanges();

      cy.reload(); 
      cy.contains('Question 1').should('exist'); 
      cy.contains('Answer 1').should('exist');
    });   

    it ('Audio-audio flashcard is persisted when saved', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftAudioContent('./cypress/files/example.mp3')
      setRightAudioContent('./cypress/files/example.mp3')
      saveDeckChanges();

      cy.reload();

     cy.get('.flashcard-review-container .flashcard-front .audio-preview-container').should('exist');
     cy.get('.flashcard-review-container .flashcard-back .audio-preview-container').should('exist');
    }); 

    it ('Image-image flashcard is persisted when saved', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftImageContent('./cypress/files/example.jpg')
      setRightImageContent('./cypress/files/example.jpg')
      saveDeckChanges();

      cy.reload();

      cy.get('.flashcard-review-container .flashcard-front .image-preview-container').should('exist');
      cy.get('.flashcard-review-container .flashcard-back .image-preview-container').should('exist');
    }); 

    it ('Video-video flashcard is persisted when saved', () => {
      createNewDeck();

      createNewFlashcard();
      setLeftVideoContent('./cypress/files/example.mp4')
      setRightVideoContent('./cypress/files/example.mp4')
      saveDeckChanges();

      cy.reload();

      cy.get('.flashcard-review-container .flashcard-front .video-preview-container').should('exist');
      cy.get('.flashcard-review-container .flashcard-back .video-preview-container').should('exist');
    });
  });
});

// Flashcard deck functions 
function createNewDeck() {
  cy.visit('/flashcard-deck/')
  cy.contains('Add deck').click()
  cy.contains('Deck "New deck"').click()
}

function setDeckName(name: string) {
  cy.get('.deck-name-input > .border').clear().type(name); 
}

function saveDeckChanges() {
  cy.contains('Save changes').click(); 
}

// Flashcard functions 
function createNewFlashcard() {
  cy.contains('Add flashcard').click();
}

function deleteFlashcard() {
  cy.get(':nth-child(1) > .flashcard-edit-item').contains('Delete').click(); 
}

function setLeftTextContent(content: string) {
  setLeftContentType('Text'); 
  cy.get(':nth-child(1) > .flashcard-edit-item .left-side .text-input-container > textarea').clear().type(content);
}

function setRightTextContent(content: string) {
  setRightContentType('Text');
  cy.get(':nth-child(1) > .flashcard-edit-item .right-side .text-input-container > textarea').clear().type(content);
} 

function setLeftAudioContent(fileName: string) {
  setLeftContentType('Audio'); 
  cy.get(':nth-child(1) > .flashcard-edit-item .left-side .audio-input-container input[type=file]').selectFile(fileName, { force: true });
}

function setRightAudioContent(fileName: string) {
  setRightContentType('Audio'); 
  cy.get(':nth-child(1) > .flashcard-edit-item .right-side .audio-input-container input[type=file]').selectFile(fileName, { force: true });
}

function setLeftImageContent(fileName: string) {
  setLeftContentType('Image');
  cy.get(':nth-child(1) > .flashcard-edit-item .left-side .image-input-container input[type=file]').selectFile(fileName, { force: true });
}

function setRightImageContent(fileName: string) {
  setRightContentType('Image');
  cy.get(':nth-child(1) > .flashcard-edit-item .right-side .image-input-container input[type=file]').selectFile(fileName, { force: true });
}

function setLeftVideoContent(fileName: string) {
  setLeftContentType('Video');
  cy.get(':nth-child(1) > .flashcard-edit-item .left-side .video-input-container input[type=file]').selectFile(fileName, { force: true });
}

function setRightVideoContent(fileName: string) {
  setRightContentType('Video');
  cy.get(':nth-child(1) > .flashcard-edit-item .right-side .video-input-container input[type=file]').selectFile(fileName, { force: true });
}

function setLeftContentType(contentType: string) {
  cy.get(':nth-child(1) > .flashcard-edit-item .left-content-type-input').select(contentType); 
}

function setRightContentType(contentType: string) {
  cy.get(':nth-child(1) > .flashcard-edit-item .right-content-type-input').select(contentType); 
}