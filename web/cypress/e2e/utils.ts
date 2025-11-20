
export function resetData() {
    cy.request('POST', 'http://localhost:8080/reset-graph');
}

export function goToSignUpPage() {
    cy.visit('/signup');
}

export function goToLoginPage() {
    cy.visit('/login');
}

function getCurrentPageType() {
    return cy.url().then(url => {
            if (url.includes('/signup')) return "signup";
            if (url.includes('/login')) return "login";
            return undefined;
    });
}

function setFieldValue(field: any, value: string) {
    field.clear()
    if (value) field.type(value);
}

export function setName(value: string) {
    getCurrentPageType().then(currentPageType => {;
        if (currentPageType === 'signup') 
            return setFieldValue(cy.get('#form-input-1'), value);
        else 
            throw new Error('Unsupported page type ' + currentPageType);
    });
}

export function setEmail(value: string) {
    return getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup') 
            return setFieldValue(cy.get('#form-input-2'), value);
        if (currentPageType === 'login')
            return setFieldValue(cy.get('#form-input-1'), value);
        else 
            throw new Error('Unsupported page type ' + currentPageType);
        });

}

export function setPassword(value: string) {
    return getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup') 
            return setFieldValue(cy.get('#form-input-3'), value);
        if (currentPageType === 'login')
            return setFieldValue(cy.get('#form-input-2'), value);
        else 
            throw new Error('Unsupported page type ' + currentPageType);
        });
}

export function setRepeatPassword(value: string) {
    return getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup') 
            return setFieldValue(cy.get('#form-input-4'), value);
        else 
            throw new Error('Unsupported page type ' + currentPageType);
        });
}

export function clickSignUpButton() {
    return getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'signup')
            return cy.contains('Sign up').click();
        else
            throw new Error('Unsupported page type ' + currentPageType);
        });
}

export function clickLoginButton() {
    return getCurrentPageType().then(currentPageType => {
        if (currentPageType === 'login')
            return cy.contains('Login').click();
        else
            throw new Error('Unsupported page type ' + currentPageType);
        });
}

export function assertLoginSuccess() {
    cy.url().should('equal', '/');
}

export function assertLoginError() {
    cy.url().should('include', '/login');
}

export function assertSignupButtonDisabled() {
    cy.contains('Sign up').should('be.disabled');
}

export function assertSignupSuccess() {
    cy.url().should('include', '/login');
}

export function assertSignupError() {
    cy.url().should('include', '/signup');
}
