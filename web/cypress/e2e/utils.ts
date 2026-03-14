export enum PageType {
    Unknown = 'unknown',
    Signup = 'signup',
    Login = 'login',
    FlashcardDeckEdit = 'flashcard-deck-edit',
    FlashcardDeckView = 'flashcard-deck-view',
    FlashcardDeckOverview = 'flashcard-deck-overview',
}

export function resetData(): void {
    cy.request('POST', 'http://localhost:8080/reset-graph');
}

export function goToSignUpPage(): void {
    cy.visit('/#/signup');
    cy.url().should('include', '/signup');
}

export function goToLoginPage(): void {
    cy.visit('/#/login');
    cy.url().should('include', '/login');
}

function getCurrentPageType(): Cypress.Chainable<PageType> {
    return cy.url().then((url) => {
        if (url.includes('/signup')) return PageType.Signup;
        if (url.includes('/login')) return PageType.Login;
        if (/\/flashcard-deck\/[^/]+\/edit/.test(url)) return PageType.FlashcardDeckEdit;
        if (/\/flashcard-deck\/[^/]+/.test(url)) return PageType.FlashcardDeckView;
        if (url.includes('/flashcard-deck')) return PageType.FlashcardDeckOverview;
        return PageType.Unknown;
    });
}

function selectOption(selector: string, value: string): void {
    cy.get(selector).click();
    cy.get(selector).find('app-option').contains(value).click();
}

function setFieldValue(selector: string, value: string): void {
    if (value) {
        cy.get(selector).clear().type(value);
    } else {
        cy.get(selector).clear();
    }
}

export function setName(value: string): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.Signup)
            setFieldValue('#name', value);
        else
            throw new Error('setName: unsupported page type ' + currentPageType);
    });
}

export function setEmail(value: string): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.Signup)
            setFieldValue('#email', value);
        else if (currentPageType === PageType.Login)
            setFieldValue('#username', value);
        else
            throw new Error('setEmail: unsupported page type ' + currentPageType);
    });
}

export function setPassword(value: string): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.Signup)
            setFieldValue('#password', value);
        else if (currentPageType === PageType.Login)
            setFieldValue('#password', value);
        else
            throw new Error('setPassword: unsupported page type ' + currentPageType);
    });
}

export function setRepeatPassword(value: string): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.Signup)
            setFieldValue('#repeatPassword', value);
        else
            throw new Error('setRepeatPassword: unsupported page type ' + currentPageType);
    });
}

export function clickSignUpButton(): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.Signup)
            cy.get('button').contains('Sign up').click();
        else
            throw new Error('clickSignUpButton: unsupported page type ' + currentPageType);
    });
}

export function clickLoginButton(): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.Login)
            cy.get('button').contains("Login").click();
        else
            throw new Error('clickLoginButton: unsupported page type ' + currentPageType);
    });
}

export function assertLoginSuccess(): void {
    cy.url().should('not.include', '/login');
}

export function assertLoginError(): void {
    cy.url().should('include', '/login');
}

export function assertSignupButtonDisabled(): void {
    cy.get('button').contains('Sign up').should('be.disabled');
}

export function assertSignupSuccess(): void {
    cy.url().should('include', '/login');
}

export function assertSignupError(): void {
    cy.url().should('include', '/signup');
}

// ── Flashcard deck navigation ──────────────────────────────────────────────

export function goToFlashcardDeckOverview(): void {
    cy.visit('/#/flashcard-deck/');
    cy.url().should('include', '/flashcard-deck/');
}

// ── Flashcard deck actions ─────────────────────────────────────────────────

export function createNewDeck(): void {
    goToFlashcardDeckOverview();
    cy.contains('Add deck').click();
    cy.url().should('match', /\/flashcard-deck\/[^/]+\/edit/);
}

export function setDeckName(name: string): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get('#deck-name').clear().type(name);
        else
            throw new Error('setDeckName: unsupported page type ' + currentPageType);
    });
}

export function setDeckDescription(description: string): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get('#deck-description').clear().type(description);
        else
            throw new Error('setDeckDescription: unsupported page type ' + currentPageType);
    });
}

export function saveDeckChanges(): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.contains('Save changes').click();
        else
            throw new Error('saveDeckChanges: unsupported page type ' + currentPageType);
    });
}

// ── Flashcard item actions ─────────────────────────────────────────────────

export function createNewFlashcard(): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.contains('Add flashcard').click();
        else
            throw new Error('createNewFlashcard: unsupported page type ' + currentPageType);
    });
}

export function deleteFlashcard(index = 0): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`:nth-child(${index + 1}) > .flashcard-edit-item`).contains('Delete').click();
        else
            throw new Error('deleteFlashcard: unsupported page type ' + currentPageType);
    });
}

export function setLeftLanguage(languageName: string): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            selectOption('#front-language', languageName);
        else
            throw new Error('setLeftLanguage: unsupported page type ' + currentPageType);
    });
}

export function setRightLanguage(languageName: string): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            selectOption('#back-language', languageName);
        else
            throw new Error('setRightLanguage: unsupported page type ' + currentPageType);
    });
}

export function setLeftContentType(contentType: string, index = 0): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            selectOption(`#left-content-type-${index}`, contentType);
        else
            throw new Error('setLeftContentType: unsupported page type ' + currentPageType);
    });
}

export function setRightContentType(contentType: string, index = 0): void {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            selectOption(`#right-content-type-${index}`, contentType);
        else
            throw new Error('setRightContentType: unsupported page type ' + currentPageType);
    });
}

export function setLeftTextContent(content: string, languageName?: string, index = 0): void {
    setLeftContentType('Text', index);
    if (languageName) setLeftLanguage(languageName);
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`#left-text-${index}`).clear().type(content);
        else
            throw new Error('setLeftTextContent: unsupported page type ' + currentPageType);
    });
}

export function setRightTextContent(content: string, languageName?: string, index = 0): void {
    setRightContentType('Text', index);
    if (languageName) setRightLanguage(languageName);
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`#right-text-${index}`).clear().type(content);
        else
            throw new Error('setRightTextContent: unsupported page type ' + currentPageType);
    });
}

export function setLeftAudioContent(fileName: string, index = 0): void {
    setLeftContentType('Audio', index);
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`#left-audio-${index}`).selectFile(fileName, { force: true });
        else
            throw new Error('setLeftAudioContent: unsupported page type ' + currentPageType);
    });
}

export function setRightAudioContent(fileName: string, index = 0): void {
    setRightContentType('Audio', index);
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`#right-audio-${index}`).selectFile(fileName, { force: true });
        else
            throw new Error('setRightAudioContent: unsupported page type ' + currentPageType);
    });
}

export function setLeftImageContent(fileName: string, index = 0): void {
    setLeftContentType('Image', index);
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`#left-image-${index}`).selectFile(fileName, { force: true });
        else
            throw new Error('setLeftImageContent: unsupported page type ' + currentPageType);
    });
}

export function setRightImageContent(fileName: string, index = 0): void {
    setRightContentType('Image', index);
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`#right-image-${index}`).selectFile(fileName, { force: true });
        else
            throw new Error('setRightImageContent: unsupported page type ' + currentPageType);
    });
}

export function setLeftVideoContent(fileName: string, index = 0): void {
    setLeftContentType('Video', index);
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`#left-video-${index}`).selectFile(fileName, { force: true });
        else
            throw new Error('setLeftVideoContent: unsupported page type ' + currentPageType);
    });
}

export function setRightVideoContent(fileName: string, index = 0): void {
    setRightContentType('Video', index);
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === PageType.FlashcardDeckEdit)
            cy.get(`#right-video-${index}`).selectFile(fileName, { force: true });
        else
            throw new Error('setRightVideoContent: unsupported page type ' + currentPageType);
    });
}
