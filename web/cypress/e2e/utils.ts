
export function resetData() {
    cy.request('POST', 'http://localhost:8080/reset-graph');
}

export function goToSignUpPage() {
    cy.visit('/#/signup');
    cy.url().should('include', '/signup');
}

export function goToLoginPage() {
    cy.visit('/#/login');
    cy.url().should('include', '/login');
}

function getCurrentPageType() {
    return cy.url().then(url => {
        if (url.includes('/signup')) return 'signup';
        if (url.includes('/login')) return 'login';
        if (url.includes('/flashcard-deck/')) return 'flashcard-decks';
        if (url.includes('/flashcard-deck')) return 'flashcard-deck';
        return undefined;
    });
}

function setFieldValue(selector: string, value: string) {
    if (value) {
        cy.get(selector).clear().type(value);
    } else {
        cy.get(selector).clear();
    }
}

export function setName(value: string) {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup')
            setFieldValue('#name', value);
        else
            throw new Error('setName: unsupported page type ' + currentPageType);
    });
}

export function setEmail(value: string) {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup')
            setFieldValue('#email', value);
        else if (currentPageType === 'login')
            setFieldValue('#username', value);
        else
            throw new Error('setEmail: unsupported page type ' + currentPageType);
    });
}

export function setPassword(value: string) {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup')
            setFieldValue('#password', value);
        else if (currentPageType === 'login')
            setFieldValue('#password', value);
        else
            throw new Error('setPassword: unsupported page type ' + currentPageType);
    });
}

export function setRepeatPassword(value: string) {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup')
            setFieldValue('#repeatPassword', value);
        else
            throw new Error('setRepeatPassword: unsupported page type ' + currentPageType);
    });
}

export function clickSignUpButton() {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup')
            cy.get('button').contains('Sign up').click();
        else
            throw new Error('clickSignUpButton: unsupported page type ' + currentPageType);
    });
}

export function clickLoginButton() {
    getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'login')
            cy.get('button[type="submit"], button').contains(/login|sign in/i).click();
        else
            throw new Error('clickLoginButton: unsupported page type ' + currentPageType);
    });
}

export function assertLoginSuccess() {
    cy.url().should('not.include', '/login');
}

export function assertLoginError() {
    cy.url().should('include', '/login');
}

export function assertSignupButtonDisabled() {
    cy.get('button').contains('Sign up').should('be.disabled');
}

export function assertSignupSuccess() {
    cy.url().should('include', '/login');
}

export function assertSignupError() {
    cy.url().should('include', '/signup');
}
