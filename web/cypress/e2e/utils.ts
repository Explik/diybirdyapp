
export function resetData() {
    cy.request('POST', 'http://localhost:8080/reset-graph');
}

export function goToSignUpPage() {
    cy.visit('/signup');
}

function getCurrentPageType() {
    return "signup";
}

function setFieldValue(field: any, value: string) {
    field.clear()
    if (value) field.type(value);
}

export function setName(value: string) {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup') 
        return setFieldValue(cy.get('#form-input-1'), value);
    else 
        throw new Error('Unsupported page type' + currentPageType);
}

export function setEmail(value: string) {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup')
        return setFieldValue(cy.get('#form-input-2'), value);
    else
        throw new Error('Unsupported page type' + currentPageType);
}

export function setPassword(value: string) {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup')
        return setFieldValue(cy.get('#form-input-3'), value);
    else
        throw new Error('Unsupported page type' + currentPageType);
}

export function setRepeatPassword(value: string) {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup')
        return setFieldValue(cy.get('#form-input-4'), value);
    else
        throw new Error('Unsupported page type' + currentPageType);
}

export function clickSignUpButton() {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup')
        return cy.contains('Sign up').click();
    else
        throw new Error('Unsupported page type' + currentPageType);
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
