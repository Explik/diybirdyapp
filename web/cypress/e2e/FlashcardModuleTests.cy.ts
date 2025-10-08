describe('Flashcard deck features', () => {
  describe('Flashcard deck data', () => {
    it('Name is updated when name update is saved', () => {
      createNewDeck(); 

      setDeckName('Updated deck');
      setLeftTextContent('Question 1', 'English')
      setRightTextContent('Answer 1', 'Danish')
      saveDeckChanges(); 

      // Check if name is updated on details page
      cy.contains('Updated deck').should('exist')

      // Check if name is updated on overview page
      goToFlashcardDeckOverview()
      cy.contains('Updated deck').should('exist')
    }); 

    it('Name is not updated when name update is not saved', () => {
      createNewDeck(); 

      setDeckName('Updated deck');
      cy.reload(); // Discard changes
      saveDeckChanges();

      // Check if name is not updated on details page
      cy.contains('Updated deck').should('not.exist')

      // Check if name is not updated on overview page
      goToFlashcardDeckOverview()
      cy.contains('Updated deck').should('not.exist')
    }); 

    it('Description is updated when description update is saved', () => {
      createNewDeck();

      setDeckDescription('New description');
      saveDeckChanges(); 

      // Check if description is updated on details page
      cy.contains('New description').should('exist')
    });

    it('Description is not updated when description update is not saved', () => {
      createNewDeck();

      setDeckDescription('New description');
      cy.reload(); // Discard changes
      saveDeckChanges();
      
      // Check if description is not updated on details page
      cy.contains('New description').should('not.exist')
    });

    // TODO Add test for deck languages
  }); 

  describe('Flashcard deck exercises', () => {
    it ('Start exercises options are disabled when no flashcards are present', () => {
      createNewDeck();
      deleteFlashcard();
      saveDeckChanges();

      cy.contains('Select flashcards').should('not.exist')
      cy.contains('Review flashcards').should('not.exist')
      cy.contains('Write flashcards').should('not.exist')
      cy.contains('Learn flashcards').should('not.exist')
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
    it ('Flashcard deck is initilized with a default flashcard', () => {
      createNewDeck()

      cy.get('.flashcard-edit-item').should('have.length', 1);
      cy.get('#leftLanguageSelect_0').contains('Select language').should('exist');
      cy.get('#rightLanguageSelect_0').contains('Select language').should('exist');
    });

    it ('Flashcard is added when add flashcard is clicked (before language select)', () => {
      createNewDeck();

      createNewFlashcard(); 
      
      cy.get('.flashcard-edit-item').should('have.length', 2);
    }); 

    it ('Flashcard is added when add flashcard is clicked (after language select)', () => {
      createNewDeck(); 
      setLeftLanguage('English');
      setRightLanguage('Danish');

      createNewFlashcard();

      cy.get('.flashcard-edit-item').should('have.length', 2);
      cy.get('#leftLanguageSelect_1').contains('English').should('exist');
      cy.get('#rightLanguageSelect_1').contains('Danish').should('exist');
    });

    it ('Flashcard is removed when remove flashcard is clicked', () => {
      createNewDeck();
      createNewFlashcard(); 

      deleteFlashcard();

      cy.get('.flashcard-edit-item').should('have.length', 1);
    }); 

    it ('Flascard language change is reflected on all flashcards', () => {
      createNewDeck();
      createNewFlashcard();
      createNewFlashcard();
      setLeftLanguage('English');
      setRightLanguage('Danish');
      
      cy.get('#leftLanguageSelect_0').contains('English').should('exist');
      cy.get('#rightLanguageSelect_0').contains('Danish').should('exist');
      cy.get('#leftLanguageSelect_1').contains('English').should('exist');
      cy.get('#rightLanguageSelect_1').contains('Danish').should('exist');
      cy.get('#leftLanguageSelect_2').contains('English').should('exist');
      cy.get('#rightLanguageSelect_2').contains('Danish').should('exist');
    });

    it ('Flashcard is not persisted when not saved', () => {
      createNewDeck();

      setLeftTextContent('Question 1')
      setRightTextContent('Answer 1')
      cy.reload(); 

      cy.contains('Question 1').should('not.exist'); 
      cy.contains('Answer 1').should('not.exist');
    }); 

    it ('Text-text flaschards input options are available when selected', () => {
      createNewDeck(); 

      setLeftContentType('Text');
      setRightContentType('Text');

      cy.get(':nth-child(1) > .flashcard-edit-item .left-side .text-input-container').should('exist');
      cy.get(':nth-child(1) > .flashcard-edit-item .right-side .text-input-container').should('exist');
    }); 

    it ('Audio-audio flaschards input options are available when selected', () => {
      createNewDeck();

      setLeftContentType('Audio');
      setRightContentType('Audio');

      cy.get(':nth-child(1) > .flashcard-edit-item .left-side .audio-input-container').should('exist');
      cy.get(':nth-child(1) > .flashcard-edit-item .right-side .audio-input-container').should('exist');
    });

    it ('Image-image flaschards input options are available when selected', () => {
      createNewDeck();

      setLeftContentType('Image');
      setRightContentType('Image');

      cy.get(':nth-child(1) > .flashcard-edit-item .left-side .image-input-container').should('exist');
      cy.get(':nth-child(1) > .flashcard-edit-item .right-side .image-input-container').should('exist');
    }); 

    it ('Video-video flaschards input options are available when selected', () => {
      createNewDeck();

      setLeftContentType('Video');
      setRightContentType('Video');

      cy.get(':nth-child(1) > .flashcard-edit-item .left-side .video-input-container').should('exist');
      cy.get(':nth-child(1) > .flashcard-edit-item .right-side .video-input-container').should('exist');
    }); 

    it('Text-text flascard is immidiately available for review', () => {
      createNewDeck(); 

      setLeftContentType('Text');
      setRightContentType('Text');
      setLeftTextContent('Question 1')
      setRightTextContent('Answer 1')

      cy.get('.flashcard-deck-edit-container').contains('Question 1').should('exist');
      cy.get('.flashcard-deck-edit-container').contains('Answer 1').should('exist');
    }); 

    it ('Audio-audio flashcard is immidiately available for review', () => {
      createNewDeck();

      setLeftAudioContent('./cypress/files/example.mp3')
      setRightAudioContent('./cypress/files/example.mp3')

      cy.get('.flashcard-deck-edit-container .flashcard-front .audio-preview-container').should('exist');
      cy.get('.flashcard-deck-edit-container .flashcard-back .audio-preview-container').should('exist');
    }); 

    it ('Image-image flashcard is immidiately available for review', () => {
      createNewDeck();

      setLeftImageContent('./cypress/files/example.jpg')
      setRightImageContent('./cypress/files/example.jpg')

      cy.get('.flashcard-deck-edit-container .flashcard-front .image-preview-container').should('exist');
      cy.get('.flashcard-deck-edit-container .flashcard-back .image-preview-container').should('exist');
    });

    it ('Video-video flashcard is immidiately available for review', () => {
      createNewDeck();

      setLeftVideoContent('./cypress/files/example.mp4')
      setRightVideoContent('./cypress/files/example.mp4')
      
      cy.get('.flashcard-deck-edit-container .flashcard-front .video-preview-container').should('exist');
      cy.get('.flashcard-deck-edit-container .flashcard-back .video-preview-container').should('exist');
    });

    it ('Text-text flashcard is persisted when saved', () => {
      createNewDeck();

      setLeftTextContent('Question 1')
      setRightTextContent('Answer 1')
      saveDeckChanges();

      cy.reload(); 
      cy.contains('Question 1').should('exist'); 
      cy.contains('Answer 1').should('exist');
    });   

    it ('Audio-audio flashcard is persisted when saved', () => {
      createNewDeck();

      setLeftAudioContent('./cypress/files/example.mp3')
      setRightAudioContent('./cypress/files/example.mp3')
      saveDeckChanges();

      cy.reload();

     cy.get('.flashcard-deck-edit-container .flashcard-front .audio-preview-container').should('exist');
     cy.get('.flashcard-deck-edit-container .flashcard-back .audio-preview-container').should('exist');
    }); 

    it ('Image-image flashcard is persisted when saved', () => {
      createNewDeck();

      setLeftImageContent('./cypress/files/example.jpg')
      setRightImageContent('./cypress/files/example.jpg')
      saveDeckChanges();

      cy.reload();

      cy.get('.flashcard-deck-edit-container .flashcard-front .image-preview-container').should('exist');
      cy.get('.flashcard-deck-edit-container .flashcard-back .image-preview-container').should('exist');
    }); 

    it ('Video-video flashcard is persisted when saved', () => {
      createNewDeck();

      setLeftVideoContent('./cypress/files/example.mp4')
      setRightVideoContent('./cypress/files/example.mp4')
      saveDeckChanges();

      cy.reload();

      cy.get('.flashcard-deck-edit-container .flashcard-front .video-preview-container').should('exist');
      cy.get('.flashcard-deck-edit-container .flashcard-back .video-preview-container').should('exist');
    });


  });
});

// Navigation functions
function goToFlashcardDeckOverview() {
  cy.visit('/flashcard-deck/')
}

// Flashcard deck functions 
function createNewDeck() {
  goToFlashcardDeckOverview()
  cy.contains('Add deck').click()
}

function setDeckName(name: string) {
  cy.get('.deck-name-input > .border').clear().type(name); 
}

function setDeckDescription(description: string) {
  cy.get('.deck-description-input > .border').clear().type(description);
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

function setLeftTextContent(content: string, languageName?: string) {
  setLeftContentType('Text'); 
  if (languageName) setLeftLanguage(languageName);
  cy.get(':nth-child(1) > .flashcard-edit-item .left-side .text-input-container > textarea').clear().type(content);
}

function setRightTextContent(content: string, languageName?: string) {
  setRightContentType('Text');
  if (languageName) setRightLanguage(languageName);
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

function setLeftLanguage(languageName: string) {
  cy.get(':nth-child(1) > .flashcard-edit-item #leftLanguageSelect_0').select(languageName); 
}

function setRightLanguage(languageName: string) {
  cy.get(':nth-child(1) > .flashcard-edit-item #rightLanguageSelect_0').select(languageName); 
}