describe('Flashcard deck features', () => {
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

  it ('Flashcard is added when add flashcard is clicked', () => {
    createNewDeck();

    createNewFlashcard(); 
    
    cy.get('flaschart-edit-item').should('exist'); 
  }); 

  it ('Flashcard is removed when remove flashcard is clicked', () => {
    createNewDeck();
    createNewFlashcard(); 

    deleteFlashcard();

    cy.get('flaschart-edit-item').should('not.exist');
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

  it ('Text-text flashcard is not persisted when not saved', () => {
    createNewDeck();

    createNewFlashcard(); 
    setLeftTextContent('Question 1')
    setRightTextContent('Answer 1')
    cy.reload(); 

    cy.contains('Question 1').should('not.exist'); 
    cy.contains('Answer 1').should('not.exist');
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
  cy.get(':nth-child(1) > .flashcard-edit-item .left-content-type-input').select('Text'); 
  cy.get(':nth-child(1) > .flashcard-edit-item .left-side .text-input-container textarea').clear().type(content);
}

function setRightTextContent(content: string) {
  cy.get(':nth-child(1) > .flashcard-edit-item .right-content-type-input').select('Text');
  cy.get(':nth-child(1) > .flashcard-edit-item .right-side .text-input-container > textarea').clear().type(content);
} 

