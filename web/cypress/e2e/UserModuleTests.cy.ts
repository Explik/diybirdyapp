import {
    resetData,
    goToSignUpPage,
    setName,
    setEmail,
    setPassword,
    setRepeatPassword,
    clickSignUpButton,
    assertSignupButtonDisabled,
    assertSignupSuccess,
    assertSignupError
} from './utils';

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