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

    cy.contains('Add flashcard').click();
    
    cy.get(':nth-child(1) > .bg-white').should('exist'); 
  }); 
});

function createNewDeck() {
  cy.visit('/flashcard-deck/')
  cy.contains('Add deck').click()
  cy.contains('Deck "New deck"').click()
}

function setDeckName(name: string) {
  cy.get('.ng-untouched > .border').clear().type(name); 
}

function setLeftTextContent(content: string) {
  cy.get('#leftContentTypeSelect').select('Text'); 
  cy.get(':nth-child(1) > .bg-white > .items-center > :nth-child(1) > app-text-input > app-text-field.ng-untouched > .border').clear().type(content);
}

function setRightTextContent(content: string) {
  cy.get('#rightContentTypeSelect').select('Text');
  cy.get(':nth-child(1) > .bg-white > .items-center > :nth-child(1) > app-text-input > app-text-field.ng-untouched > .border').clear().type(content);
} 

function saveDeckChanges() {
  cy.contains('Save changes').click(); 
}