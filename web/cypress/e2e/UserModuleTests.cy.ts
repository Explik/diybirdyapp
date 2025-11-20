import { cli } from "cypress";

function resetData() {
    cy.request('POST', 'http://localhost:8080/reset-graph');
}

function goToSignUpPage() {
    cy.visit('/signup');
}

function getCurrentPageType() {
    return "signup";
}

function setFieldValue(field: any, value: string) {
    field.clear()
    if (value) field.type(value);
}

function setName(value: string) {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup') 
        return setFieldValue(cy.get('#form-input-1'), value);
    else 
        throw new Error('Unsupported page type' + currentPageType);
}

function setEmail(value: string) {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup')
        return setFieldValue(cy.get('#form-input-2'), value);
    else
        throw new Error('Unsupported page type' + currentPageType);
}

function setPassword(value: string) {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup')
        return setFieldValue(cy.get('#form-input-3'), value);
    else
        throw new Error('Unsupported page type' + currentPageType);
}

function setRepeatPassword(value: string) {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup')
        return setFieldValue(cy.get('#form-input-4'), value);
    else
        throw new Error('Unsupported page type' + currentPageType);
}

function clickSignUpButton() {
    let currentPageType = getCurrentPageType();
    if (currentPageType === 'signup')
        return cy.contains('Sign up').click();
    else
        throw new Error('Unsupported page type' + currentPageType);
}

function assertSignupButtonDisabled() {
    cy.contains('Sign up').should('be.disabled');
}

function assertSignupSuccess() {
    cy.url().should('include', '/login');
}

function assertSignupError() {
    cy.url().should('include', '/signup');
}

describe('Sign Up and Login Flow', () => {
    describe('Sign Up Tests', () => {
        function populateFormWithValidData() {
            const validPassword = "validPassword123";
            const validEmail = "newuser@email.com";

            setName("Test User");
            setEmail(validEmail);
            setPassword(validPassword);
            setRepeatPassword(validPassword);
        }

        beforeEach(resetData); 

        it ('Successfull sign-up with valid credentials', () => {
            goToSignUpPage();
            
            populateFormWithValidData();
            clickSignUpButton();

            assertSignupSuccess(); 
        }); 

        it ('Blocked sign-up with missing email', () => {
            goToSignUpPage();
            
            populateFormWithValidData();
            setEmail("");

            assertSignupButtonDisabled();
        }); 

        it ('Blocked sign-up with missing password', () => {
            goToSignUpPage();

            populateFormWithValidData();
            setPassword("");

            assertSignupButtonDisabled();
        }); 

        it ('Blocked sign-up with missing repeated password', () => {
            goToSignUpPage();

            populateFormWithValidData();
            setRepeatPassword("");

            assertSignupButtonDisabled();
        });

        it ('Blocked sign-up with non-matching repeated password', () => {
            goToSignUpPage();

            populateFormWithValidData();
            setPassword("validPassword123");
            setRepeatPassword("differentPassword123");

            assertSignupButtonDisabled();
        });

        it ('Failed sign-up with already registered email', () => {
            // Registering user once 
            goToSignUpPage();
            populateFormWithValidData();
            clickSignUpButton();

            cy.wait(2500); // Wait for a moment to ensure the first registration is processed

            // Attempting to register again with the same email
            goToSignUpPage();
            populateFormWithValidData();
            clickSignUpButton();

            assertSignupError();
        });
    }); 
}); 